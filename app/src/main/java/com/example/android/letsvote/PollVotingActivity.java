package com.example.android.letsvote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.android.letsvote.Model.PollData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PollVotingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView pollDescView;
    private TextView votedFor;
    private TextView wonView;
    private TextView optionsTag;
    private RecyclerView recyclerView;

    private PollData pollData;
    private ArrayList<String> optionsList;
    private int[] voteCount;

    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_voting);

        pollData = (PollData) getIntent().getSerializableExtra("Data");
        optionsList = pollData.getPollOptions();
        voteCount = new int[optionsList.size()];

        mDataBase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        toolbar = findViewById(R.id.voting_toolbar);
        toolbar.setTitle(pollData.getPollName().substring(0, 1).toUpperCase() + pollData.getPollName().substring(1));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PollVotingActivity.super.onBackPressed();
            }
        });

        pollDescView = findViewById(R.id.poll_voting_desc);
        pollDescView.setText(pollData.getPollDesc().substring(0,1).toUpperCase() + pollData.getPollDesc().substring(1));

        votedFor = findViewById(R.id.voted_for);
        optionsTag = findViewById(R.id.options_tag);
        wonView = findViewById(R.id.voting_winner);
        wonView.setVisibility(View.GONE);

        // Recycler view

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.poll_voting_recycler_view);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new PollOptionAdapter(this, pollData.getPollOptions(), pollData));

        votedFor.setVisibility(View.GONE);

        mDataBase.child("Responses").child(pollData.getPollId()).child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            votedFor.setVisibility(View.VISIBLE);
                            votedFor.setText("You Voted for " + dataSnapshot.getValue().toString().substring(0,1).toUpperCase()
                                    +  dataSnapshot.getValue().toString().substring(1));
                            optionsTag.setText("Available options was: ");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if(pollData.getIsActive().equalsIgnoreCase("false")) {
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
}
