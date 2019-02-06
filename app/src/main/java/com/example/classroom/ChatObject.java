package com.example.classroom;

public class ChatObject {
    String userId,name,imageUrl;

    public ChatObject(String userId, String name,String imageUrl){
        this.userId=userId;
        this.name=name;
        this.imageUrl=imageUrl;

    }
    public String getUserId(){
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
}
