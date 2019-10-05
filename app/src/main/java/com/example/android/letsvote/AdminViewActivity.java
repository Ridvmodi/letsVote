package com.example.android.letsvote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
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
import android.widget.ProgressBar;
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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AdminViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog.Builder editDialogBuilder;
    private AlertDialog mDialog;
    private AlertDialog editPollDialog;
    private Button pollAddBtn;
    private EditText pollNameView;
    private EditText pollDescView;
    private EditText pollOptionsView;
    private EditText editPolllName;
    private EditText editPollDesc;
    private EditText editPollOptions;
    private Button editBtn;
    private Button endBtn;
    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private PollData editPollData = new PollData();

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

        editDialogBuilder = new AlertDialog.Builder(this);
        final View editPollView = inflater.inflate(R.layout.edit_poll, null);
        editDialogBuilder.setView(editPollView);
        editPollDialog = editDialogBuilder.create();
        progressDialog = new ProgressDialog(this);

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

        // finding the edit poll views

        editPolllName = editPollView.findViewById(R.id.edit_poll_name);
        editPollDesc = editPollView.findViewById(R.id.edit_poll_desc);
        editPollOptions = editPollView.findViewById(R.id.edit_poll_options);
        editBtn = editPollView.findViewById(R.id.update_btn);
        endBtn = editPollView.findViewById(R.id.end_btn);

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPollDialog.dismiss();
                editPollData.setIsActive("false");
                mDatabase.child("Polls").child(editPollData.getPollId()).setValue(editPollData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(AdminViewActivity.this, "Poll Ended", Toast.LENGTH_SHORT).show();
                                } else {
                                    editPollData.setIsActive("true");
                                    Toast.makeText(AdminViewActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Processing..");
                editPoll();
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

        PollData pollData = new PollData(pollId, pollName, pollDesc, pollOptions, currentUserId, "true");

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

    private void editPoll() {

        String pollName = editPolllName.getText().toString().trim();
        String pollDesc = editPollDesc.getText().toString().trim();
        String pollOptionsString = editPollOptions.getText().toString().trim();

        if (TextUtils.isEmpty(pollName)) {
            editPolllName.setError("Required..");
            editPolllName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pollDesc)) {
            editPollDesc.setError("Required..");
            editPollDesc.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pollOptionsString)) {
            editPollOptions.setError("Required..");
            editPollOptions.requestFocus();
            return;
        }

        pollOptionsString += ",";
        ArrayList<String> pollOptions = new ArrayList<>();
        pollOptions.addAll(Arrays.asList(pollOptionsString.split(",")));
        String currentUserId = mAuth.getCurrentUser().getUid();

        String pollId = editPollData.getPollId();

        PollData pollData = new PollData(pollId, pollName, pollDesc, pollOptions, currentUserId, editPollData.getIsActive());

        editPollDialog.dismiss();
        progressDialog.show();

        mDatabase.child("Polls").child(pollId).setValue(pollData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    progressDialog.dismiss();
                    Toast.makeText(AdminViewActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AdminViewActivity.this, "Poll Edited Successfully", Toast.LENGTH_SHORT).show();
                    editPolllName.setText("");
                    editPollDesc.setText("");
                    editPollOptions.setText("");
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<PollData, MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PollData, MyViewHolder>(
                PollData.class, R.layout.poll_data,
                MyViewHolder.class, mDatabase.child("Polls")
        ) {
            @Override
            protected void populateViewHolder(final MyViewHolder viewHolder, final PollData model, int position) {
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
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Intent intent = new Intent(AdminViewActivity.this, PollStatusActivity.class);
                    intent.putExtra("Data", model);
                    startActivity(intent);
                    }
                });

                viewHolder.myView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        editPollData = model;
                        if(editPollData.getIsActive().equals("false")) {
                            viewHolder.myView.setOnLongClickListener(null);

                        } else {
                            editPolllName.setText(editPollData.getPollName());
                            editPollDesc.setText(editPollData.getPollDesc());
                            String options = "";
                            ArrayList<String> optionsList = editPollData.getPollOptions();
                            for (String str : optionsList) {
                                options = str + ",";
                            }
                            editPollOptions.setText(options);
                            editPollDialog.show();
                        }
                        return false;
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