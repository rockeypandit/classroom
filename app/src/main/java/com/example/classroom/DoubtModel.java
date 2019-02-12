package com.example.classroom;

import android.media.Image;

import java.io.Serializable;
import java.util.List;

public class DoubtModel implements Serializable {
    String question;
    String answer;
    List<String> tags;
    List<Image> images;

    public DoubtModel(String question) {
        this.question = question;
    }

    public DoubtModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
