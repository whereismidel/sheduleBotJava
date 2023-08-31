package com.midel.group;

public class SubjectFullInfo {
    private String name;
    private String lecturerForFirstGroupAndGeneralLesson;
    private String lecturerForSecondGroup;
    private String auditoryForFirstGroupAndGeneralLesson;
    private String auditoryForSecondGroup;
    private String linkForFirstGroupAndGeneralLesson;
    private String linkForSecondGroup;
    private String noteForFirstGroupAndGeneralLesson;
    private String noteForSecondGroup;
    private final boolean common;

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

    public SubjectFullInfo(String name, Boolean common,
                    String linkForFirstGroupAndGeneralLesson, String noteForFirstGroupAndGeneralLesson,
                    String lecturerForFirstGroupAndGeneralLesson, String auditoryForFirstGroupAndGeneralLesson,
                    String linkForSecondGroup, String noteForSecondGroup,
                    String lecturerForSecondGroup, String auditoryForSecondGroup) {
        this.name = name;
        this.common = common;

        this.lecturerForFirstGroupAndGeneralLesson = lecturerForFirstGroupAndGeneralLesson;
        this.auditoryForFirstGroupAndGeneralLesson = auditoryForFirstGroupAndGeneralLesson;
        this.linkForFirstGroupAndGeneralLesson = linkForFirstGroupAndGeneralLesson;
        this.noteForFirstGroupAndGeneralLesson = noteForFirstGroupAndGeneralLesson;

        this.lecturerForSecondGroup = lecturerForSecondGroup;
        this.auditoryForSecondGroup = auditoryForSecondGroup;
        this.linkForSecondGroup = linkForSecondGroup;
        this.noteForSecondGroup = noteForSecondGroup;
    }

    public void copy(SubjectFullInfo subjectFullInfo) {
        this.name = subjectFullInfo.name;

        this.lecturerForFirstGroupAndGeneralLesson = subjectFullInfo.lecturerForFirstGroupAndGeneralLesson;
        this.auditoryForFirstGroupAndGeneralLesson = subjectFullInfo.auditoryForFirstGroupAndGeneralLesson;
        this.linkForFirstGroupAndGeneralLesson = subjectFullInfo.linkForFirstGroupAndGeneralLesson;
        this.noteForFirstGroupAndGeneralLesson = subjectFullInfo.noteForFirstGroupAndGeneralLesson;

        this.lecturerForSecondGroup = subjectFullInfo.lecturerForSecondGroup;
        this.auditoryForSecondGroup = subjectFullInfo.auditoryForSecondGroup;
        this.linkForSecondGroup = subjectFullInfo.linkForSecondGroup;
        this.noteForSecondGroup = subjectFullInfo.noteForSecondGroup;
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

    public boolean isCommon() {
        return common;
    }
}
