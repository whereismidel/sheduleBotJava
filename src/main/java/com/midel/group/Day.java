package com.midel.group;


import com.midel.type.Common;
import com.midel.type.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day {
    private List<Tuple<Subject>> lessons;
    private int numberOfLessons;
    private int lastLessonNum;

    public Day(List<Tuple<Subject>> lessons){
        this.lessons = lessons;
        this.numberOfLessons = this.lessons == null? 0 : this.lessons.size();
        this.lastLessonNum = findLastLessonNum();
    }

    public List<Tuple<Subject>> getLessons() {
        return new ArrayList<>(lessons);
    }

    public int getNumberOfLessons() {
        return numberOfLessons;
    }

    public int getLastLessonNum() {
        return lastLessonNum;
    }

    public void setLessons(List<Tuple<Subject>> lessons) {
        this.lessons = lessons;
        this.numberOfLessons = this.lessons == null? 0 : this.lessons.size();
        this.lastLessonNum = findLastLessonNum();
    }

    public void addLesson(Tuple<Subject> lesson){
        lessons.add(lesson);
        numberOfLessons++;
        this.lastLessonNum = findLastLessonNum();
    }

    public Tuple<Subject> getLessonByNum(int num){
        if (num < lessons.size()){
            return lessons.get(num);
        } else {
            return null;
        }
    }

    private int findLastLessonNum(){

        for (int i = numberOfLessons-1; i >= 0; i--) {
            if (!(lessons.get(i) instanceof Common) || ((Common<Subject>) lessons.get(i)).get() != null)
                return i+1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Day{" +
                "lessons={\n" +
                IntStream.range(0, lessons.size()).mapToObj(i -> i+1 + ". " + lessons.get(i) + "\n").collect(Collectors.joining()) +
                "}, numberOfLessons=" + numberOfLessons +
                ", lastLessonNum=" + lastLessonNum +
                '}';
    }
}
