package com.example.android.letsvote.Model;

public class PollData {

    private String pollId;
    private  String pollName;
    private String pollDesc;
    private String[] pollOptions;
    private String createdBy;
    private String[] responsedBy;

    public PollData(String pollId, String pollName, String pollDesc, String[] pollOptions, String createdBy, String[] responsedBy) {
        this.pollId = pollId;
        this.pollName = pollName;
        this.pollDesc = pollDesc;
        this.pollOptions = pollOptions;
        this.createdBy = createdBy;
        this.responsedBy = responsedBy;
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

    public String[] getPollOptions() {
        return pollOptions;
    }

    public void setPollOptions(String[] pollOptions) {
        this.pollOptions = pollOptions;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String[] getResponsedBy() {
        return responsedBy;
    }

    public void setResponsedBy(String[] responsedBy) {
        this.responsedBy = responsedBy;
    }
}
