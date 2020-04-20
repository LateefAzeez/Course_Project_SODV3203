package com.sodv3203.connectmeapp;

public class Groups {
    String groupId;
    String groupTitle;
    String timeStamp;
    String participants;

    public Groups(String groupId, String groupTitle, String timeStamp, String participants) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.timeStamp = timeStamp;
        this.participants = participants;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getParticipants() {
        return participants;
    }
}
