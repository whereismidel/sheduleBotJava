package com.midel.schedulebott.group;

public class Student {
    String id;
    boolean isLeader = false;
    String group;

    public Student(String id, boolean isLeader) {
        this.id = id;
        this.isLeader = isLeader;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public boolean isLeader() {
        return isLeader;
    }

    private void setLeader(boolean leader) {
        isLeader = leader;
    }
}
