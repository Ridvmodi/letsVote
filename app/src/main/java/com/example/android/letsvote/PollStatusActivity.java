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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PollStatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView pollDescView;
    private RecyclerView recyclerView;
    private PollData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_status);

        data = (PollData) getIntent().getSerializableExtra("Data");

        toolbar = findViewById(R.id.poll_status_toolbar);
        setSupportActionBar(toolbar);
        String toolbarName = data.getPollName().substring(0, 1).toUpperCase() + data.getPollName().substring(1);
        getSupportActionBar().setTitle(toolbarName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PollStatusActivity.super.onBackPressed();
            }
        });

        pollDescView = findViewById(R.id.poll_status_desc);
        pollDescView.setText(data.getPollDesc().substring(0, 1).toUpperCase() + data.getPollDesc().substring(1));

        recyclerView = findViewById(R.id.poll_status_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerView.Adapter pollStatusAdapter = new PollStatusAdapter(getApplicationContext(), data.getPollOptions());

        recyclerView.setAdapter(pollStatusAdapter);

    }

}
