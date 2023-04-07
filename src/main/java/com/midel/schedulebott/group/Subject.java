package com.midel.schedulebott.group;

public class Subject {
    private String name;
    private String lecturerForFirstGroupAndGeneralLesson;
    private String lecturerForSecondGroup;
    private String auditoryForFirstGroupAndGeneralLesson;
    private String auditoryForSecondGroup;
    private String linkForFirstGroupAndGeneralLesson;
    private String linkForSecondGroup;
    private String noteForFirstGroupAndGeneralLesson;
    private String noteForSecondGroup;

    @Override
    public String toString() {
        return "Subject{" +
                "name=" + name +
                ", lecturerForFirstGroupAndGeneralLesson=" + lecturerForFirstGroupAndGeneralLesson +
                ", lecturerForSecondGroup=" + lecturerForSecondGroup +
                ", auditoryForFirstGroupAndGeneralLesson=" + auditoryForFirstGroupAndGeneralLesson +
                ", auditoryForSecondGroup=" + auditoryForSecondGroup +
                ", linkForFirstGroupAndGeneralLesson=" + linkForFirstGroupAndGeneralLesson +
                ", linkForSecondGroup=" + linkForSecondGroup +
                ", noteForFirstGroupAndGeneralLesson=" + noteForFirstGroupAndGeneralLesson +
                ", noteForSecondGroup=" + noteForSecondGroup +
                '}';
    }

    Subject(String name,
            String linkForFirstGroupAndGeneralLesson, String noteForFirstGroupAndGeneralLesson,
            String lecturerForFirstGroupAndGeneralLesson, String auditoryForFirstGroupAndGeneralLesson,
            String linkForSecondGroup, String noteForSecondGroup,
            String lecturerForSecondGroup, String auditoryForSecondGroup) {
        this.name = name;

        this.lecturerForFirstGroupAndGeneralLesson = lecturerForFirstGroupAndGeneralLesson;
        this.auditoryForFirstGroupAndGeneralLesson = auditoryForFirstGroupAndGeneralLesson;
        this.linkForFirstGroupAndGeneralLesson = linkForFirstGroupAndGeneralLesson;
        this.noteForFirstGroupAndGeneralLesson = noteForFirstGroupAndGeneralLesson;

        this.lecturerForSecondGroup = lecturerForSecondGroup;
        this.auditoryForSecondGroup = auditoryForSecondGroup;
        this.linkForSecondGroup = linkForSecondGroup;
        this.noteForSecondGroup = noteForSecondGroup;
    }

    public void copy(Subject subject) {
        this.name = subject.name;

        this.lecturerForFirstGroupAndGeneralLesson = subject.lecturerForFirstGroupAndGeneralLesson;
        this.auditoryForFirstGroupAndGeneralLesson = subject.auditoryForFirstGroupAndGeneralLesson;
        this.linkForFirstGroupAndGeneralLesson = subject.linkForFirstGroupAndGeneralLesson;
        this.noteForFirstGroupAndGeneralLesson = subject.noteForFirstGroupAndGeneralLesson;

        this.lecturerForSecondGroup = subject.lecturerForSecondGroup;
        this.auditoryForSecondGroup = subject.auditoryForSecondGroup;
        this.linkForSecondGroup = subject.linkForSecondGroup;
        this.noteForSecondGroup = subject.noteForSecondGroup;
    }

    public String getName() {
        return name;
    }

    public String getLinkForFirstGroupAndGeneralLesson() {
        return linkForFirstGroupAndGeneralLesson;
    }

    public String getLinkForSecondGroup() {
        return linkForSecondGroup;
    }

    public String getNoteForFirstGroupAndGeneralLesson() {
        return noteForFirstGroupAndGeneralLesson;
    }

    public String getNoteForSecondGroup() {
        return noteForSecondGroup;
    }

    public String getLecturerForFirstGroupAndGeneralLesson() {
        return lecturerForFirstGroupAndGeneralLesson;
    }

    public String getLecturerForSecondGroup() {
        return lecturerForSecondGroup;
    }

    public String getAuditoryForFirstGroupAndGeneralLesson() {
        return auditoryForFirstGroupAndGeneralLesson;
    }

    public String getAuditoryForSecondGroup() {
        return auditoryForSecondGroup;
    }

}
