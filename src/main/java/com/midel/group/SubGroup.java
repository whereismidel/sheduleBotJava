package com.midel.group;

public enum SubGroup {
    COMMON(0),
    FIRST_GROUP(1),
    SECOND_GROUP(2);

    private int value;
    SubGroup(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
