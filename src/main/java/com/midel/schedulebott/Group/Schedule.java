package com.midel.schedulebott.Group;

import org.javatuples.Pair;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
class Rescheduled {
    LocalDate resheduledDate;
    int lessonNum;

    String rescheduledLessonName;
    String rescheduledLessonNote;
}
    ToDO Можливість додавати кастомні пари з кастомними нотатками
*/

public class Schedule {

    private HashMap<String, Subject> subjectList;
    private List<List<Object>> subjectListTable;
    private ArrayList<ArrayList<Pair<String, String>>> firstWeek;
    private ArrayList<ArrayList<Pair<String, String>>> secondWeek;

    public Subject getSubjectByName(String subject) {
        return subjectList.get(subject);
    }

    public Boolean isLastLesson(DayOfWeek dayOfWeek, int weekNumber, int numberOfSubject){

        boolean isLastLesson = true;
        // Парний тиждень - друга неділя, непарний - перша.
        for(int i = numberOfSubject; i < 6; i++){
            Pair<String, String> subgroups;
            if (weekNumber % 2 == 0) {
                subgroups = secondWeek.get(dayOfWeek.getValue() - 1).get(i);
            } else {
                subgroups = firstWeek.get(dayOfWeek.getValue() - 1).get(i);
            }
            if (!subgroups.getValue0().equals("-") || !subgroups.getValue1().equals("-")){
                isLastLesson = false;
                break;
            }
        }

        return isLastLesson;
    }

    public Pair<Pair<Subject, Subject>, Boolean> getSubjectByNumber(DayOfWeek dayOfWeek, int weekNumber, int numberOfSubject) {

        Pair<Subject, Subject> subjects;
        Pair<String, String> subgroups;
        // Парний тиждень - друга неділя, непарний - перша.
        if (weekNumber % 2 == 0) {
            // dayOfWeek нумерація з неділі, неділя = 0 -> неділя - 1 -> понелілок = 0.
            // отримуємо пари по підгрупам
            subgroups = secondWeek.get(dayOfWeek.getValue()-1).get(numberOfSubject-1);
        } else {
            // dayOfWeek нумерація з неділі, неділя = 0 -> неділя - 1 -> понелілок = 0.
            // отримуємо пари по підгрупам
            subgroups = firstWeek.get(dayOfWeek.getValue()-1).get(numberOfSubject-1);
        }

        String lessonForFirstGroup = subgroups.getValue0();
        String lessonForSecondGroup = subgroups.getValue1();
            /*
                '-' and ('-' or  ' ') -> no lesson
                'value1' and (('value2' and ('value1' = 'value2')) or  ' ') -> same lesson
                ('value1' and 'value2') or ('value1' and '-') or ('-' and 'value2') -> different lesson
            */

        if (lessonForFirstGroup.equals("-") &&
                (lessonForSecondGroup.equals("-") || lessonForSecondGroup.trim().equals(""))){
            return new Pair<>(null, isLastLesson(dayOfWeek, weekNumber, numberOfSubject));
        } else if (!lessonForFirstGroup.equals("-") &&
                ((!lessonForSecondGroup.equals("-") && lessonForFirstGroup.equals(lessonForSecondGroup)) || lessonForSecondGroup.trim().equals(""))) {
                Subject subject = getSubjectByName(lessonForFirstGroup);
                subjects = new Pair<>(subject, subject);
        } else {
            if (lessonForFirstGroup.equals("-"))
                subjects = new Pair<>(null, getSubjectByName(lessonForSecondGroup));
            else if (lessonForSecondGroup.equals("-"))
                subjects = new Pair<>(getSubjectByName(lessonForFirstGroup), null);
            else {
                subjects = new Pair<>(getSubjectByName(lessonForFirstGroup), getSubjectByName(lessonForSecondGroup));
            }
        }

        return new Pair<>(subjects, isLastLesson(dayOfWeek, weekNumber, numberOfSubject));
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "subjectList=" + subjectList +
                ", subjectListTable=" + subjectListTable +
                ", firstWeek=" + firstWeek +
                ", secondWeek=" + secondWeek +
                '}';
    }

    public HashMap<String, Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(HashMap<String, Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public ArrayList<ArrayList<Pair<String, String>>> getFirstWeek() {
        return firstWeek;
    }

    public void setFirstWeek(ArrayList<ArrayList<Pair<String, String>>> firstWeek) {
        this.firstWeek = firstWeek;
    }

    public ArrayList<ArrayList<Pair<String, String>>> getSecondWeek() {
        return secondWeek;
    }

    public void setSecondWeek(ArrayList<ArrayList<Pair<String, String>>> secondWeek) {
        this.secondWeek = secondWeek;
    }

    public List<List<Object>> getSubjectListTable() {
        return subjectListTable;
    }

    public void setSubjectListTable(List<List<Object>> subjectListTable) {
        this.subjectListTable = subjectListTable;
    }
}
