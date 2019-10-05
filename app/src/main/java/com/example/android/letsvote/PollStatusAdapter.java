package com.example.android.letsvote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.letsvote.Model.PollData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PollStatusAdapter extends RecyclerView.Adapter<PollStatusAdapter.ViewHolder> {

    ArrayList<String> optionslist;
    private DatabaseReference mDataBase;
    private PollData pollData;
    private int voteCount = 0;

    public PollStatusAdapter(Context context, PollData pollData) {
        this.pollData = pollData;
        this.optionslist = pollData.getPollOptions();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Responses");
    }

    @Override
    public PollStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_options_status_data,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PollStatusAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.optionNameView.setText(optionslist.get(position).substring(0,1).toUpperCase() + optionslist.get(position).substring(1));
        mDataBase.child(pollData.getPollId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(dataSnapshot1.getValue().toString().equalsIgnoreCase(optionslist.get(position))) {
                        voteCount++;
                    }
                }
                viewHolder.optionResultView.setText(String.valueOf(voteCount));
                voteCount = 0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return optionslist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View myView;

        protected TextView optionNameView;
        protected TextView optionResultView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.myView = itemView;
            optionNameView = myView.findViewById(R.id.option_text);
            optionResultView = myView.findViewById(R.id.option_result);
        }
    }
}
