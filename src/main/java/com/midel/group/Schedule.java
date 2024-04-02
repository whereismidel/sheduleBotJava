package com.midel.group;



import com.midel.type.Common;
import com.midel.type.Pair;
import com.midel.type.Tuple;

import java.util.HashMap;
import java.util.List;

public class Schedule {

    private HashMap<String, Tuple<String>> subjectListWithLink; // contain all uniq subject with links for subgroup name from schedule
    private HashMap<String, SubjectFullInfo> subjectInfo;
    private List<List<Object>> subjectListTable; // contain exported from google sheet subject table

    private List<Week> weeks; // contain list of weeks from schedule

    public Schedule(List<Week> weeks, HashMap<String, Tuple<String>> subjectListWithLink){
        this.weeks = weeks;
        this.subjectListWithLink = subjectListWithLink;
        this.subjectInfo = new HashMap<>();
    }

    public void setSubjectListWithLink(HashMap<String, Tuple<String>> subjectListWithLink) {
        this.subjectListWithLink = subjectListWithLink;
    }

    public String getLinkForSubgroup(String subjectName, SubGroup subGroup){
        Tuple<String> links = subjectListWithLink.get(subjectName);

        if (links == null)
                return null;

        if (links instanceof Common){
            return ((Common<String>) links).get();
        } else {
            return ((Pair<String>) links).get(subGroup.getValue()-1);
        }
    }

    public void setSubjectListTable(List<List<Object>> subjectListTable) {
        this.subjectListTable = subjectListTable;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }

    public HashMap<String, Tuple<String>> getSubjectListWithLink() {
        return subjectListWithLink;
    }

    public List<List<Object>> getSubjectListTable() {
        return subjectListTable;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public HashMap<String, SubjectFullInfo> getSubjectInfo() {
        return subjectInfo;
    }

    public void setSubjectInfo(HashMap<String, SubjectFullInfo> subjectInfo) {
        this.subjectInfo = subjectInfo;
    }
}
