package com.example.classroom.personalChat;

public class PersonalObject {
    String message;
    Boolean currentUser;
    public PersonalObject(String message,Boolean currentUser){
            this.message = message;
            this.currentUser=currentUser;
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
}

