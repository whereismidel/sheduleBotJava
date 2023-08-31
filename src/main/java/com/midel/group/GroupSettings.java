package com.midel.group;

import java.time.LocalDateTime;

public class GroupSettings {
    private Boolean dailyNotification = true;
    private Boolean state = false;
    private Boolean valid = false;
    private LocalDateTime lastRequestToTable;

    private Boolean showLecture = true;
    private Boolean showAuditory = true;
    private Boolean showTimeBorder = true;
    private Boolean showAbsenceLesson = false;

    // ToDo регулювати час відправки розкладу
    // ToDo якщо група не отримала повідомлення бо мало прав, відключати групу від бота(або відслідковувати зміну прав/кік)

    public Boolean isDailyNotification() {
        return dailyNotification;
    }

    public void setDailyNotification(boolean dailyNotification) {
        this.dailyNotification = dailyNotification;
    }

    public Boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public LocalDateTime getLastRequestToTable() {
        return lastRequestToTable;
    }

    public void setLastRequestToTable(LocalDateTime lastRequestToTable) {
        this.lastRequestToTable = lastRequestToTable;
    }

    public Boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "GroupSettings{" +
                "dailyNotification=" + dailyNotification +
                ", state=" + state +
                ", valid=" + valid +
                ", lastModifiedFile=" + lastRequestToTable +
                '}';
    }

    public Boolean getShowLecture() {
        return showLecture;
    }

    public void setShowLecture(Boolean showLecture) {
        this.showLecture = showLecture;
    }

    public Boolean getShowAuditory() {
        return showAuditory;
    }

    public void setShowAuditory(Boolean showAuditory) {
        this.showAuditory = showAuditory;
    }

    public Boolean getShowTimeBorder() {
        return showTimeBorder;
    }

    public void setShowTimeBorder(Boolean showTimeBorder) {
        this.showTimeBorder = showTimeBorder;
    }

    public Boolean getShowAbsenceLesson() {
        return showAbsenceLesson;
    }

    public void setShowAbsenceLesson(Boolean showAbsenceLesson) {
        this.showAbsenceLesson = showAbsenceLesson;
    }
}
