package com.midel.schedulebott.group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupController {
    static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    public static Group getGroupByName(String groupName) {
        try {
            return GroupRepo.groups.values().stream()
                    .filter(group -> groupName.equalsIgnoreCase(group.getGroupName()))
                    .findAny()
                    .orElse(null);
        } catch (Exception e){
            logger.error("Failed get group by groupName. groupName = {}", groupName, e);
            return null;
        }
    }

    public static Group getGroupByLeader(String leaderId) {
        try {
            return GroupRepo.groups.values().stream()
                    .filter(group -> leaderId.equalsIgnoreCase(group.getLeaderId()))
                    .findAny()
                    .orElse(null);
        } catch (Exception e){
            logger.error("Failed get group by leaderId. leaderId = {}", leaderId, e);
            return null;
        }
    }

    public static ArrayList<String> getMatchesByGroupName(String match){
        try {
            ArrayList<String> result = new ArrayList<>();

            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(match);
            if (!matcher.find()) {
                return null;
            }

            for (Group group : GroupRepo.groups.values()) {
                if (group.getGroupName().contains(matcher.group())) {
                    result.add(group.getGroupName());
                }
            }

            if (result.size() == 0) {
                return null;
            } else {
                return result;
            }
        } catch (Exception e){
            logger.error("Failed get matches by groupName. Matches = {}", match, e);
            return null;
        }
    }
}


