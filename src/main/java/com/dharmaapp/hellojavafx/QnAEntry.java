package com.dharmaapp.hellojavafx;

import java.sql.Timestamp;

public class QnAEntry {
    private int id;
    private String username;
    private String question;
    private String answer;
    private Timestamp timestamp;

    public QnAEntry(int id, String username, String question, String answer, Timestamp timestamp) {
        this.id = id;
        this.username = username;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public Timestamp getTimestamp() { return timestamp; }
}

