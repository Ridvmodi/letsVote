package com.example.android.letsvote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PollStatusAdapter extends RecyclerView.Adapter<PollStatusAdapter.ViewHolder> {

    ArrayList<String> arrayList = new ArrayList<>();

    public PollStatusAdapter(Context context, ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public PollStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_options_status_data,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PollStatusAdapter.ViewHolder viewHolder, int position) {
        viewHolder.optionNameView.setText(arrayList.get(position).substring(0,1).toUpperCase() + arrayList.get(position).substring(1));
        viewHolder.optionResultView.setText(String.valueOf(arrayList.size()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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
