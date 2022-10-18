package com.midel.schedulebott.Group;

public class GroupSettings {
    private boolean dailyNotification = true;
    private boolean state = true;

    public boolean isDailyNotification() {
        return dailyNotification;
    }

    public void setDailyNotification(boolean dailyNotification) {
        this.dailyNotification = dailyNotification;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
