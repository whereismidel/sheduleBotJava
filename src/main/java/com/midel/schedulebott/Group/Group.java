package com.midel.schedulebott.Group;

import java.util.ArrayList;
import java.util.HashMap;

// Google Sheet -> List Group: admin_id(староста), channel_id(канал з розкладом), sheet_id(розклад і інформація про пару), settings_list(??)
public class Group {
    private String groupName;
    private String leaderId;
    private String channelId;
    private String sheetId;
    private GroupSettings settings; // ToDO налаштування для груп -> dailyNotification for each group
    private Schedule schedule = new Schedule();

    public Group(String groupName, String leaderId) {
        this.groupName = groupName;
        this.leaderId = leaderId;

        schedule.setFirstWeek(new ArrayList<>());
        schedule.setSecondWeek(new ArrayList<>());

        settings = new GroupSettings();
    }

    public Group(String groupName, String leaderId, String channelId, String sheetId, Boolean state) {
        this.groupName = groupName;
        this.leaderId = leaderId;
        this.channelId = channelId;
        this.sheetId = sheetId;

        schedule.setFirstWeek(new ArrayList<>());
        schedule.setSecondWeek(new ArrayList<>());
        schedule.setSubjectList(new HashMap<>());

        settings = new GroupSettings();
        settings.setState(state);
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupName='" + groupName + '\'' +
                ", leaderId='" + leaderId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", sheetId='" + sheetId + '\'' +
                ", schedule=" + schedule +
                '}';
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getSheetId() {
        return sheetId;
    }

    public void setSheetId(String sheetId) {
        this.sheetId = sheetId;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public GroupSettings getSettings() {
        return settings;
    }

    public void setSettings(GroupSettings settings) {
        this.settings = settings;
    }
}
