package com.midel.schedulebott.Group;

public class Subject {
    private String name;
    private String linkForFirstGroupAndGeneralLesson;
    private String linkForSecondGroup;
    private String noteForFirstGroupAndGeneralLesson;
    private String noteForSecondGroup;

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                ", linkForFirstGroupAndGeneralLesson='" + linkForFirstGroupAndGeneralLesson + '\'' +
                ", linkForSecondGroup='" + linkForSecondGroup + '\'' +
                ", noteForFirstGroupAndGeneralLesson='" + noteForFirstGroupAndGeneralLesson + '\'' +
                ", noteForSecondGroup='" + noteForSecondGroup + '\'' +
                '}';
    }

    Subject(String name, String linkForFirstGroupAndGeneralLesson, String noteForFirstGroupAndGeneralLesson, String linkForSecondGroup, String noteForSecondGroup) {
        this.name = name;
        this.linkForFirstGroupAndGeneralLesson = linkForFirstGroupAndGeneralLesson;
        this.linkForSecondGroup = linkForSecondGroup;
        this.noteForFirstGroupAndGeneralLesson = noteForFirstGroupAndGeneralLesson;
        this.noteForSecondGroup = noteForSecondGroup;
    }

    Subject(String name, String linkForGeneralLesson, String noteForGeneralLesson){
        this.name = name;
        this.linkForFirstGroupAndGeneralLesson = linkForGeneralLesson;
        this.linkForSecondGroup = linkForGeneralLesson;
        this.noteForFirstGroupAndGeneralLesson = noteForGeneralLesson;
        this.noteForSecondGroup = noteForGeneralLesson;
    }

    Subject(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkForFirstGroupAndGeneralLesson() {
        return linkForFirstGroupAndGeneralLesson;
    }

    public void setLinkForFirstGroupAndGeneralLesson(String linkForFirstGroupAndGeneralLesson) {
        this.linkForFirstGroupAndGeneralLesson = linkForFirstGroupAndGeneralLesson;
    }

    public String getLinkForSecondGroup() {
        return linkForSecondGroup;
    }

    public void setLinkForSecondGroup(String linkForSecondGroup) {
        this.linkForSecondGroup = linkForSecondGroup;
    }

    public String getNoteForFirstGroupAndGeneralLesson() {
        return noteForFirstGroupAndGeneralLesson;
    }

    public void setNoteForFirstGroupAndGeneralLesson(String noteForFirstGroupAndGeneralLesson) {
        this.noteForFirstGroupAndGeneralLesson = noteForFirstGroupAndGeneralLesson;
    }

    public String getNoteForSecondGroup() {
        return noteForSecondGroup;
    }

    public void setNoteForSecondGroup(String noteForSecondGroup) {
        this.noteForSecondGroup = noteForSecondGroup;
    }
}
