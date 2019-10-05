package com.example.android.letsvote;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.android.letsvote.Model.PollData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserViewActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lets Vote");

        // Recycler view

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.user_recycler_view);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void onBackPressed() {
        if(mAuth.getCurrentUser() != null) {
            ActivityCompat.finishAffinity(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<PollData, AdminViewActivity.MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PollData, AdminViewActivity.MyViewHolder>(
                PollData.class, R.layout.poll_data,
                AdminViewActivity.MyViewHolder.class, mDataBase.child("Polls")
        ) {
            @Override
            protected void populateViewHolder(final AdminViewActivity.MyViewHolder viewHolder, final PollData model, int position) {
            viewHolder.setPollName(model.getPollName());
            viewHolder.setPollDesc(model.getPollDesc());

            mDataBase.child(model.getCreatedBy()).child("userName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        viewHolder.setCreatedBy((String)dataSnapshot.getValue());
                    }
                    else {
                        viewHolder.setCreatedBy("Null");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserViewActivity.this, PollVotingActivity.class);
                    intent.putExtra("Data", model);
                    startActivity(intent);
                }
            });

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}
