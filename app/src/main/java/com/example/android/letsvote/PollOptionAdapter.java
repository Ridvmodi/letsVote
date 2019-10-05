package com.example.android.letsvote;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.letsvote.Model.PollData;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PollOptionAdapter extends RecyclerView.Adapter<PollOptionAdapter.MyViewHolder> {

    private ArrayList<String> pollOptions = new ArrayList<>();
    private PollData pollData;
    private Boolean isVoted = false;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private Button yesBtn;
    private Button noBtn;
    Context context;
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;

    public PollOptionAdapter(Context context, ArrayList<String> pollOptions, PollData pollData) {
        this.pollOptions = pollOptions;
        this.context = context;
        this.pollData = pollData;
//        alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setView(LayoutInflater.from(context).inflate(R.layout.prompt, null));
//        alertDialog = alertDialogBuilder.create();
//
//        yesBtn = alertDialog.findViewById(R.id.prompt_yes_btn);
//        noBtn = alertDialog.findViewById(R.id.prompt_no_btn);
//        noBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });

        mDataBase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poll_voting_option, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {
        viewHolder.optionBtn.setText(pollOptions.get(position).substring(0,1).toUpperCase() + pollOptions.get(position).substring(1));

        mDataBase.child("Responses").child(pollData.getPollId()).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    viewHolder.optionBtn.setEnabled(false);
                    viewHolder.optionBtn.setBackgroundColor(Color.rgb(143,143,143));
                    isVoted = true;
                } else {
                    viewHolder.optionBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String pollOption = pollOptions.get(position);
                            mDataBase.child("Responses").child(pollData.getPollId())
                                    .child(String.valueOf(mAuth.getCurrentUser().getUid())).setValue(pollOption)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Voted for " + pollOption.substring(0,1).toUpperCase() + pollOption.substring(1), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Please try again :(", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return pollOptions.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        protected Button optionBtn;

        View myView;
        public MyViewHolder(View itemView) {
            super(itemView);
            myView = itemView;

            optionBtn = myView.findViewById(R.id.voting_option_btn);

        }


    }

}
