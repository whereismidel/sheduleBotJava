package com.midel.student;

import com.midel.group.Group;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Student {
    private final String id;
    private Boolean isLeader;
    private Group group;

    private LocalDateTime lastMessageTime;

    public Student(String id, Boolean isLeader, Group group, LocalDateTime lastMessageTime) {
        this.id = id;
        this.isLeader = isLeader;
        this.group = group;
        this.lastMessageTime = lastMessageTime;
    }

    public Student(String id, Boolean isLeader) {
        this(id, isLeader, null, LocalDateTime.now(ZoneId.of("Europe/Kiev")).minusMinutes(1));
    }

    public String getId() {
        return id;
    }

    public Boolean isLeader() {
        return isLeader;
    }

    public void setLeader(Boolean leader) {
        isLeader = leader;
    }
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }



    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", isLeader=" + isLeader +
                ", group=" + group +
                ", lastMessage=" + lastMessageTime +
                '}';
    }

    public List<Object> toList() {
        return new ArrayList<>(Arrays.asList(id == null? "null" : id,
                                isLeader == null?"null":isLeader?"yes":"no",
                                group == null? "null" : group.getGroupName()));
    }

    public void copy(Student student) {
        this.isLeader = student.isLeader();
        this.group = student.getGroup();
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
