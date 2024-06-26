package com.midel.group;

import java.time.LocalDateTime;
import java.util.*;

// Google Sheet -> List Group: admin_id(староста), channel_id(канал з розкладом), sheet_id(розклад і інформація про пару), settings_list(??)
public class Group {
    private String groupName;
    private String leaderId;
    private String leaderEmail;
    private String channelId;
    private String sheetId;
    private Integer deleteMessage;
    private final GroupSettings settings;
    private final Schedule schedule;

    public Group(String groupName, String leaderId, String leaderEmail, String channelId, String sheetId, Boolean state, LocalDateTime lastModifiedDate, Integer deleteMessage) {
        this.groupName = groupName;
        this.leaderId = leaderId;
        this.leaderEmail = leaderEmail;
        this.channelId = channelId;
        this.sheetId = sheetId;
        this.deleteMessage = deleteMessage;

        this.schedule = new Schedule(new ArrayList<>(), new HashMap<>());

        this.settings = new GroupSettings();
        settings.setState(state);
        settings.setLastRequestToTable(lastModifiedDate);
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupName=" + groupName +
                ", leaderId=" + leaderId +
                ", leaderEmail=" + leaderEmail +
                ", channelId=" + channelId +
                ", sheetId=" + sheetId +
                ", schedule=" + schedule +
                ", settings=" + settings +
                ", deleteMessageId=" + deleteMessage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return groupName.equals(group.groupName) && Objects.equals(leaderId, group.leaderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, leaderId);
    }

    public void copy(Group group) {
        this.groupName = group.getGroupName();
        this.leaderId = group.getLeaderId();
        this.channelId = group.getChannelId();
        this.sheetId = group.getSheetId();
        this.deleteMessage = group.getDeleteMessage();
        this.getSettings().setState(group.getSettings().isState());
        this.getSettings().setLastRequestToTable(group.getSettings().getLastRequestToTable());
    }

    public List<Object> toList(){
        return new ArrayList<>(Arrays.asList(
                groupName == null?"null":groupName,
                leaderId == null?"null":leaderId,
                leaderEmail == null?"null":leaderEmail,
                channelId == null?"null":channelId,
                sheetId == null?"null":sheetId,
                settings.isState() ? "on" : "off",
                settings.getLastRequestToTable().toString(),
                deleteMessage == null?"null":deleteMessage)
        );
    }

    public String getGroupName() {
        return groupName;
    }

    public String getLeaderId() {
        return leaderId;
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

    public GroupSettings getSettings() {
        return settings;
    }

    public void setLeaderEmail(String leaderEmail) {
        this.leaderEmail = leaderEmail;
    }

    public String getLeaderEmail() {
        return leaderEmail;
    }

    public Integer getDeleteMessage() {
        return deleteMessage;
    }

    public void setDeleteMessage(Integer deleteMessage) {
        this.deleteMessage = deleteMessage;
    }
}
