package com.example.classroom;

public class ChatObject {
    private String userId, name, imageUrl;
    private boolean isGroupChat;

    public ChatObject(String userId, String name, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isGroupChat = false;
    }

    public ChatObject(String groupChatName) {
        this.imageUrl = null;
        this.isGroupChat = true;
        this.userId = null;
        this.name = groupChatName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId)

    {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }
}
