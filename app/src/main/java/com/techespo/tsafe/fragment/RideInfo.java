package com.techespo.tsafe.fragment;

public class RideInfo {
    String date;

    public String getInitialLoc() {
        return initialLoc;
    }

    public void setInitialLoc(String initialLoc) {
        this.initialLoc = initialLoc;
    }

    String initialLoc;

    public String getFinalLoc() {
        return finalLoc;
    }

    public void setFinalLoc(String finalLoc) {
        this.finalLoc = finalLoc;
    }

    String finalLoc;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    String startTime;

    public String getReachTime() {
        return reachTime;
    }

    public void setReachTime(String reachTime) {
        this.reachTime = reachTime;
    }

    String reachTime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
