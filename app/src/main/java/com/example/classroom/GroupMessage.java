package com.example.classroom;

public class GroupMessage {
    private String senderUid;
    private String message;
    private String timestamp;

    public String getMessage() {
        return message;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
