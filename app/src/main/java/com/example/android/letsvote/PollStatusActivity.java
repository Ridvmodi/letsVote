package com.example.android.letsvote;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.android.letsvote.Model.PollData;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PollStatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView pollDescView;
    private TextView wonView;
    private RecyclerView recyclerView;
    private PollData pollData;
    private DatabaseReference mDataBase;

    private ArrayList<String> optionsList;
    private int[] voteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_status);

        pollData = (PollData) getIntent().getSerializableExtra("Data");

        optionsList = pollData.getPollOptions();
        voteCount = new int[optionsList.size()];

        mDataBase = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.poll_status_toolbar);
        setSupportActionBar(toolbar);
        String toolbarName = pollData.getPollName().substring(0, 1).toUpperCase() + pollData.getPollName().substring(1);
        getSupportActionBar().setTitle(toolbarName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PollStatusActivity.super.onBackPressed();
            }
        });

        pollDescView = findViewById(R.id.poll_status_desc);
        pollDescView.setText(pollData.getPollDesc().substring(0, 1).toUpperCase() + pollData.getPollDesc().substring(1));
        wonView = findViewById(R.id.winner);

        wonView.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.poll_status_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerView.Adapter pollStatusAdapter = new PollStatusAdapter(getApplicationContext(), pollData);

        recyclerView.setAdapter(pollStatusAdapter);

        mDataBase.child("Polls").child(pollData.getPollId()).child("isActive")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue().equals("false")) {
                            wonView.setVisibility(View.VISIBLE);
                            mDataBase.child("Responses").child(pollData.getPollId())
                                    .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                        for(int i = 0;i<optionsList.size();i++) {
                                            if(dataSnapshot1.getValue().toString()
                                                    .equalsIgnoreCase(optionsList.get(i))) {
                                                voteCount[i]++;
                                            }
                                        }
                                    }
                                    int index = 0, max = -32768;
                                    for(int i = 0;i<voteCount.length;i++) {
                                        if(max < voteCount[i]) {
                                            max = voteCount[i];
                                            index = i;
                                        }
                                    }
                                    wonView.setText(optionsList.get(index).substring(0,1).toUpperCase() +
                                            optionsList.get(index).substring(1) + " Won");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

}
