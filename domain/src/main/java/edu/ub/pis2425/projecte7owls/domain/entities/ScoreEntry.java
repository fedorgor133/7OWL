package edu.ub.pis2425.projecte7owls.domain.entities;

import java.util.Date;

public class ScoreEntry {
    public Date timestamp;
    public int scoreChange;

    public ScoreEntry(Date timestamp, int scoreChange) {
        this.timestamp = timestamp;
        this.scoreChange = scoreChange;
    }
}
