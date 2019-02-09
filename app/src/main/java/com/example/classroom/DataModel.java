package com.example.classroom;

public class DataModel {
    String videoTitle;
    String videoId;
    String videoUrl;
    String thumbnailUrl;

    /*public DataModel(String videoId, String videoTitle) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoUrl = "https://youtu.be/".concat(videoId);
        this.thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
    }*/

    public DataModel(String videoTitle, String videoUrl) {
        if (videoUrl.contains("v=")) {
            this.videoId = videoUrl.split("v=")[1];
        } else {
            this.videoId = videoUrl.split("be/")[1];
        }
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = "https://img.youtube.com/vi/" + this.videoId + "/0.jpg";
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
