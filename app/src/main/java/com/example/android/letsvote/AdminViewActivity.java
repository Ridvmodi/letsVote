package com.example.android.letsvote;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.letsvote.Model.PollData;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class AdminViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mDialog;
    private Button pollAddBtn;
    private EditText pollNameView;
    private EditText pollDescView;
    private EditText pollOptionsView;

    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lets Vote");

        mDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View addPollView = inflater.inflate(R.layout.add_poll, null);
        mDialogBuilder.setView(addPollView);
        mDialog = mDialogBuilder.create();

        fabBtn = findViewById(R.id.fab_btn);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
            }
        });

        // finding the add poll views

        pollAddBtn = addPollView.findViewById(R.id.poll_add_btn);
        pollNameView = addPollView.findViewById(R.id.poll_name);
        pollDescView = addPollView.findViewById(R.id.poll_desc);
        pollOptionsView = addPollView.findViewById(R.id.poll_options);

        pollAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPoll();
            }
        });

        // Recycler View
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onBackPressed() {
        if (mAuth.getCurrentUser() != null) {
            ActivityCompat.finishAffinity(this);
        } else {
            super.onBackPressed();
        }
    }

    private void addNewPoll() {

        String pollName = pollNameView.getText().toString().trim();
        String pollDesc = pollDescView.getText().toString().trim();
        String pollOptionsString = pollOptionsView.getText().toString().trim();

        if (TextUtils.isEmpty(pollName)) {
            pollNameView.setError("Required..");
            pollNameView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pollDesc)) {
            pollDescView.setError("Required..");
            pollDescView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pollOptionsString)) {
            pollOptionsView.setError("Required..");
            pollOptionsView.requestFocus();
            return;
        }

        pollOptionsString += ",";
        ArrayList<String> pollOptions = new ArrayList<>();
        pollOptions.addAll(Arrays.asList(pollOptionsString.split(",")));
        String pollId = mDatabase.push().getKey();
        String currentUserId = mAuth.getCurrentUser().getUid();

        PollData pollData = new PollData(pollId, pollName, pollDesc, pollOptions, currentUserId);

        mDatabase.child("Polls").child(pollId).setValue(pollData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(AdminViewActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminViewActivity.this, "Poll Added Successfully", Toast.LENGTH_SHORT).show();
                    pollNameView.setText("");
                    pollDescView.setText("");
                    pollOptionsView.setText("");
                }
            }
        });

        mDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<PollData, MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PollData, MyViewHolder>(
                PollData.class, R.layout.poll_data,
                MyViewHolder.class, mDatabase.child("Polls")
        ) {
            @Override
            protected void populateViewHolder(final MyViewHolder viewHolder, PollData model, int position) {
                viewHolder.setPollName(model.getPollName());
                viewHolder.setPollDesc(model.getPollDesc());

                mDatabase.child(model.getCreatedBy()).child("userName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            viewHolder.setCreatedBy((String) dataSnapshot.getValue());
                        } else {
                            viewHolder.setCreatedBy("Null");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.myView = itemView;
        }

        public void setPollName(String pollName) {
            TextView textView = myView.findViewById(R.id.poll_data_name);
            pollName = pollName.substring(0,1).toUpperCase() + pollName.substring(1);
            textView.setText(pollName);
        }

        public void setPollDesc(String pollDesc) {

            TextView textView = myView.findViewById(R.id.poll_data_desc);
            pollDesc = pollDesc.substring(0, 1).toUpperCase() + pollDesc.substring(1);
            textView.setText(pollDesc);

        }

        public void setCreatedBy(String createdBy) {

            TextView textView = myView.findViewById(R.id.poll_data_createdby);
            createdBy = "- Created by " + createdBy;
            textView.setText(createdBy);

        }
    }
}