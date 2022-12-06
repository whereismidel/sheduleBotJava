package com.midel.schedulebott.group;

public class GroupSettings {
    private boolean dailyNotification = true;
    private boolean state = true;
    private boolean validTable = false;

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

    public boolean isValidTable() {
        return validTable;
    }

    public void setValidTable(boolean invalidTable) {
        this.validTable = invalidTable;
    }
}
