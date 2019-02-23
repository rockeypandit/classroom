package com.example.classroom;

public class GroupChatObject {
    private String message, senderName;
    private Boolean currentUser;

    public GroupChatObject(String message, String senderName, Boolean currentUser) {
        this.message = message;
        this.senderName = senderName;
        this.currentUser = currentUser;
    }

    public GroupChatObject(String message, String senderName) {
        this.message = message;
        this.senderName = senderName;
        this.currentUser = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
