package com.midel.group;

import com.midel.exceptions.TooManyDaysException;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Week {
    private static final int maxDays = 5;
    private List<Day> days;
    private boolean isInvalid = false;

    public Week(List<Day> days) throws TooManyDaysException {
        if (days.size() > maxDays)
            throw new TooManyDaysException("Кількість днів в тижні перевищує " + maxDays);

        this.days = days;
    }

    public Week() {
        this.days = new ArrayList<>();
        for(int i = 0; i < maxDays; i++)
            days.add(null);
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) throws TooManyDaysException {
        if (days.size() > maxDays)
            throw new TooManyDaysException("Кількість днів в тижні перевищує " + maxDays);

        this.days = days;
    }

    public Day getDay(int day) throws TooManyDaysException {
        if (day < 0 || day >= maxDays)
            throw new TooManyDaysException("Номер дня повинен бути від 0 до " + (maxDays-1));
        return days.get(day);
    }

    public Day getDay(DayOfWeek dayOfWeek) throws TooManyDaysException {
        return getDay((dayOfWeek.getValue()+6) % 7);
    }

    public Week setDay(Day day, int pos) throws TooManyDaysException {
        if (pos >= maxDays)
            throw new TooManyDaysException("Кількість днів в тижні перевищує 7.");

        days.set(pos, day);
        return this;
    }

    public Week setDay(Day day, DayOfWeek dayOfWeek) throws TooManyDaysException {
        return setDay(day, (dayOfWeek.getValue()+6) % 7);
    }

    @Override
    public String toString() {
        return "Week{" +
                "days={\n" +
                IntStream.range(0, days.size()).mapToObj(i -> DayOfWeek.of((i + 1) % 7) + "\n" + days.get(i) + "\n").collect(Collectors.joining()) +
                "}}";
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void setInvalid(boolean invalid) {
        isInvalid = invalid;
    }
}
