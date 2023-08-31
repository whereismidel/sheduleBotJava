package com.midel.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Template {
    private final String groupName;
    private String sheetId;


    public Template(String groupName, String sheetId) {
        this.sheetId = sheetId;
        this.groupName = groupName;
    }

    public String getSheetId() {
        return sheetId;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public String toString() {
        return "Template{" +
                "groupName='" + groupName + '\'' +
                ", sheetId='" + sheetId + '\'' +
                '}';
    }

    public void copy(Template student) {
        this.sheetId = student.sheetId;
    }

    public List<Object> toList() {
        return new ArrayList<>(Arrays.asList(groupName, sheetId));
    }
}
