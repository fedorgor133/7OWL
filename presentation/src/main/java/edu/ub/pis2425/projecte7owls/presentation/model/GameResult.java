package edu.ub.pis2425.projecte7owls.presentation.model;

import java.util.Date;

public class GameResult {
    private String userId;
    private int score;
    private Date timestamp;

    public GameResult() {}

    public GameResult(String userId, int score, Date timestamp) {
        this.userId = userId;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
