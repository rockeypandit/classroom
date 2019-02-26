package com.example.classroom;

import java.io.Serializable;
import java.util.List;

public class DoubtModel implements Serializable {
    private String question;
    private String answer;
    private String attachmentLink;
    private List<String> tags;

    public DoubtModel(String question) {
        this.question = question;
    }

    public DoubtModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public DoubtModel(String question, String answer, String attachmentLink) {
        this.question = question;
        this.answer = answer;
        this.attachmentLink = attachmentLink;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAttachmentLink() {
        return this.attachmentLink;
    }

    public void setAttachmentLink(String link) {
        this.attachmentLink = link;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
