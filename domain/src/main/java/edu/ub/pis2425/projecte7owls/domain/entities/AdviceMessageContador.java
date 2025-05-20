package edu.ub.pis2425.projecte7owls.domain.entities;

public class AdviceMessageContador {
    public int minDays;
    public int maxDays;
    public String message;

    public AdviceMessageContador() {} // Necesario para Firebase

    public AdviceMessageContador(int minDays, int maxDays, String message) {
        this.minDays = minDays;
        this.maxDays = maxDays;
        this.message = message;
    }

    public int getMinDays() {
        return minDays;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public String getMessage() {
        return message;
    }
}
