package com.example.android.letsvote;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.letsvote.Model.PollData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mDialog;
    private Button pollAddBtn;
    private EditText pollNameView;
    private EditText pollDescView;
    private EditText pollOptionsView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Polls");

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

    }

    @Override
    public void onBackPressed() {
        if(mAuth.getCurrentUser() != null) {
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
        } if(TextUtils.isEmpty(pollDesc)) {
            pollDescView.setError("Required..");
            pollDescView.requestFocus();
            return;
        } if(TextUtils.isEmpty(pollOptionsString)) {
            pollOptionsView.setError("Required..");
            pollOptionsView.requestFocus();
            return;
        }

        String pollOptions[] = pollOptionsString.split(",");

        String pollId = mDatabase.push().getKey();
        String currentUserId = mAuth.getCurrentUser().getUid();

        PollData pollData = new PollData(pollId, pollName, pollDesc, pollOptions, currentUserId, null);

        mDatabase.child(pollId).setValue(pollData);

        mDialog.dismiss();
    }

}
