package com.marfeel.exercise.domain;

/**
 * Created by eduardo.ponte on 29/09/2015.
 */
public class EnqueueResult {
    private int totalEnqueuedForProcessing;

    public EnqueueResult(int totalEnqueuedForProcessing) {
        this.totalEnqueuedForProcessing = totalEnqueuedForProcessing;
    }

    public int getTotalEnqueuedForProcessing() {
        return totalEnqueuedForProcessing;
    }

    public void setTotalEnqueuedForProcessing(int totalEnqueuedForProcessing) {
        this.totalEnqueuedForProcessing = totalEnqueuedForProcessing;
    }
}
