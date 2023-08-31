package com.midel.schedule;

import java.time.LocalTime;

public class ScheduleTime{
    final LocalTime timeNotification;
    private final LocalTime startTime;
    private final LocalTime endTime;
    final int durability;
    final int numberOfLesson;

    ScheduleTime(int hourForReal, int minuteForReal, int durability, int minutesAheadForNotification, int numberOfLesson){
        this.startTime = LocalTime.of(hourForReal, minuteForReal);
        this.durability = durability;
        this.endTime = this.startTime.plusMinutes(durability);
        this.timeNotification = startTime.minusMinutes(minutesAheadForNotification);
        this.numberOfLesson = numberOfLesson;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
}