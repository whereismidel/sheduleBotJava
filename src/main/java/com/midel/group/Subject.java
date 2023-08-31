package com.midel.group;

import com.midel.type.Common;
import com.midel.type.Tuple;

import java.util.HashMap;
import java.util.stream.Collectors;

public class Subject {

    private String keyName;
    private String titleForMessage;
    private String lector;
    private String auditory;
    private Note note;

    public Subject(String keyName, String titleForMessage, String lector, String auditory, Note notes) {
        this.keyName = keyName;
        this.titleForMessage = titleForMessage;
        this.lector = lector;
        this.auditory = auditory;
        this.note = notes;
    }

    public String getTitleForMessage() {
        return titleForMessage;
    }

    public String getLector() {
        return lector;
    }

    public String getAuditory() {
        return auditory;
    }

    public void setTitleForMessage(String titleForMessage) {
        this.titleForMessage = titleForMessage;
    }

    public void setLector(String lector) {
        this.lector = lector;
    }

    public void setAuditory(String auditory) {
        this.auditory = auditory;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "keyName='" + keyName + '\'' +
                ", titleForMessage='" + titleForMessage + '\'' +
                ", lector='" + lector + '\'' +
                ", auditory='" + auditory + '\'' +
                '}';
    }

    public static Subject getSubjectFromSubjectInfo(String subjectName, int numSubGroup, HashMap<String, SubjectFullInfo> subjectFullInfo){
        SubjectFullInfo subjectInfo = subjectFullInfo.get(subjectName);
        if (subjectName.trim().equals("-") || subjectName.isEmpty() || subjectInfo == null || numSubGroup < 0 || numSubGroup > 2){
            return null;
        }

        if (numSubGroup == 0 || numSubGroup == 1){
            Note note = subjectInfo.getNoteForFirstGroupAndGeneralLesson() == null? null : new Note(subjectInfo.getNoteForFirstGroupAndGeneralLesson());

            return new Subject(subjectName, subjectInfo.getName(), subjectInfo.getLecturerForFirstGroupAndGeneralLesson(), subjectInfo.getAuditoryForFirstGroupAndGeneralLesson(), note);
        } else {
            Note note = subjectInfo.getNoteForSecondGroup() == null? null : new Note(subjectInfo.getNoteForSecondGroup());

            return new Subject(subjectName, subjectInfo.getName(), subjectInfo.getLecturerForSecondGroup(), subjectInfo.getAuditoryForSecondGroup(), note);
        }
    }

    public static Tuple<Subject> getLesson(String firstGroupSubject, String secondGroupSubject, HashMap<String, SubjectFullInfo> subjectInfo){
        if (firstGroupSubject.equals(secondGroupSubject) || secondGroupSubject.equals("")){
            return new Common<>(Subject.getSubjectFromSubjectInfo(firstGroupSubject, 0, subjectInfo));
        } else {
            return new com.midel.type.Pair<>(
                    Subject.getSubjectFromSubjectInfo(firstGroupSubject, 1, subjectInfo),
                    Subject.getSubjectFromSubjectInfo(secondGroupSubject, 2, subjectInfo)
            );
        }
    }

    // text from note
    public String removeNoteIfNotPermanent(Note note, boolean isGlobal, Group group, SubGroup subGroup) {
        if (!note.isPermanent() && isGlobal) {
            // ToDo only for google sheet
            group.getSchedule().getSubjectListTable()
                    .stream()
                    .filter(sub->sub.stream().anyMatch(cell-> cell.equals(getKeyName())))
                    .collect(Collectors.toList()).get(0).set(3 + ( 4 * subGroup.getValue()), "");

            GroupRepo.exportGroupSubjects(group, group.getSchedule().getSubjectListTable());
        }
        return note.getText();
    }

    public String getKeyName() {
        return keyName;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note notes) {
        this.note = notes;
    }
}
