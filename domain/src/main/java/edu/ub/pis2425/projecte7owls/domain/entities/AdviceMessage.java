package edu.ub.pis2425.projecte7owls.domain.entities;

public class AdviceMessage {
    private long minTimeMinutes;
    private long maxTimeMinutes;
    private String advice;


    public AdviceMessage() {}

    public AdviceMessage(long minTimeMinutes, long maxTimeMinutes, String advice) {
        this.minTimeMinutes = minTimeMinutes;
        this.maxTimeMinutes = maxTimeMinutes;
        this.advice = advice;
    }

    public long getMinTimeMinutes() {
        return minTimeMinutes;
    }

    public void setMinTimeMinutes(long minTimeMinutes) {
        this.minTimeMinutes = minTimeMinutes;
    }

    public long getMaxTimeMinutes() {
        return maxTimeMinutes;
    }

    public void setMaxTimeMinutes(long maxTimeMinutes) {
        this.maxTimeMinutes = maxTimeMinutes;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    // Útil para saber si un tiempo dado cae en el rango del mensaje
    public boolean isApplicable(long minutes) {
        return minutes >= minTimeMinutes && minutes <= maxTimeMinutes;
    }
}
