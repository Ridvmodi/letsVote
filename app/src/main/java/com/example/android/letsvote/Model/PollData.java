package com.example.android.letsvote.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class PollData implements Serializable {

    private String pollId;
    private  String pollName;
    private String pollDesc;
    private ArrayList<String> pollOptions;
    private String createdBy;

    public PollData() {
    }

    public PollData(String pollId, String pollName, String pollDesc, ArrayList<String> pollOptions, String createdBy) {
        this.pollId = pollId;
        this.pollName = pollName;
        this.pollDesc = pollDesc;
        this.pollOptions = pollOptions;
        this.createdBy = createdBy;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public String getPollName() {
        return pollName;
    }

    public void setPollName(String pollName) {
        this.pollName = pollName;
    }

    public String getPollDesc() {
        return pollDesc;
    }

    public void setPollDesc(String pollDesc) {
        this.pollDesc = pollDesc;
    }

    public ArrayList<String> getPollOptions() {
        return pollOptions;
    }

    public void setPollOptions(ArrayList<String> pollOptions) {
        this.pollOptions = pollOptions;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}
