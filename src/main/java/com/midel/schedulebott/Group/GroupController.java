package com.midel.schedulebott.Group;

import com.midel.schedulebott.Config.DBConfig;
import com.midel.schedulebott.SheetAPI.SheetValidator;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.midel.schedulebott.SheetAPI.SheetController.readSheetForRange;

public class GroupController {
    public static ArrayList<Group> groups;
    static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    public static void updateGroupList() throws GeneralSecurityException, IOException {
        try {
            List<List<Object>> valuesFromGroupList = readSheetForRange(DBConfig.initGroupSheet, "GroupInfo!A2:E200");
            groups = new ArrayList<Group>(){
                @Override
                public String toString() {
                    StringBuilder result = new StringBuilder("[");
                    for (int i = 0; i < this.size()-1; i++) {
                        result.append(this.get(i)).append(",\n");
                    }
                    result.append(this.get(this.size()-1)).append("]");
                    return result.toString();
                }
            };

            if (valuesFromGroupList == null || valuesFromGroupList.isEmpty()) {
                logger.warn("No data found in group list sheet.");
            } else {
                for (List<Object> rowGroup : valuesFromGroupList) {
                    Group group = new Group(rowGroup.get(0).toString(), rowGroup.get(1).toString(), rowGroup.get(2).toString(), rowGroup.get(3).toString(), rowGroup.get(4).toString().trim().equalsIgnoreCase("on"));
                    logger.debug("{} - {}", group.getGroupName(), group.getSettings().isState());
                    groups.add(group);
                }
                logger.trace("Group info list: \n{}", groups);
            }
        } catch (Exception e) {
            logger.error("Error while reading Group List table.", e);
        }
        logger.info("Successful update of data from GroupList table.");
    }

    public static void updateGroupSchedule() throws GeneralSecurityException, IOException {

        for (Group group : groups) {
            try {
                // initialize schedule for group from sheetid
                List<List<Object>> valuesFromGroupSchedule = readSheetForRange(group.getSheetId(), "РОЗКЛАД!A5:S22");

                if (valuesFromGroupSchedule == null || valuesFromGroupSchedule.isEmpty()) {
                    logger.warn("No data found in Group Schedule sheet. GroupName = {}, SheetID = {}", group.getGroupName(), group.getSheetId());
                } else {
                    valuesFromGroupSchedule.subList(6, 12).clear(); // remove from values range A11:S16

                    group.getSchedule().setFirstWeek(new ArrayList<>());
                    group.getSchedule().setSecondWeek(new ArrayList<>());

                    // fill schedule for first and second week
                    for (int dayCells = 0; dayCells < 5 * 4; dayCells += 4) {
                        // dayCells+0 - lesson number, dayCells+1 - lesson for first subgroup, dayCells+2 - lesson for second subgroup, dayCells+3 - empty cell
                        // List of lessons for the day of the week in order
                        ArrayList<Pair<String, String>> dayForFirstWeek = new ArrayList<>();
                        ArrayList<Pair<String, String>> dayForSecondWeek = new ArrayList<>();

                        for (int lesson = 0; lesson < 6; lesson++) {
                            // Normalizing the size of all rows to avoid IndexOutOfBoundsException
                            for (int i = 0; i < 19 - valuesFromGroupSchedule.get(lesson).size(); i++)
                                valuesFromGroupSchedule.get(lesson).add("");
                            for (int i = 0; i < 19 - valuesFromGroupSchedule.get(lesson + 6).size(); i++) {
                                valuesFromGroupSchedule.get(lesson + 6).add("");
                            }

                            dayForFirstWeek.add(new Pair<>(
                                    String.valueOf(valuesFromGroupSchedule.get(lesson).get(dayCells + 1)),
                                    String.valueOf(valuesFromGroupSchedule.get(lesson).get(dayCells + 2))
                            ));

                            // lessons for the second week start after 6 lines in the parse list
                            dayForSecondWeek.add(new Pair<>(
                                    String.valueOf(valuesFromGroupSchedule.get(lesson + 6).get(dayCells + 1)),
                                    String.valueOf(valuesFromGroupSchedule.get(lesson + 6).get(dayCells + 2))
                            ));
                        }

                        group.getSchedule().getFirstWeek().add(dayForFirstWeek);
                        group.getSchedule().getSecondWeek().add(dayForSecondWeek);
                    }
                }
            }  catch (Exception e) {
                logger.error("Error while reading Group Schedule sheet. {}", group, e);
            }

            SheetValidator.scheduleSheetValidator(group);
        }
        logger.info("Successful update of data from Schedule sheet.");
        logger.trace("Group list after update schedule:\n{}", groups);
    }

    public static void updateGroupScheduleSubjectInfo() throws GeneralSecurityException, IOException {

        for (Group group : groups) {
            try {
                // ToDo реєстрація старост через меню бота

                // initialize subject info for group from sheetid
                group.getSchedule().setSubjectListTable(readSheetForRange(group.getSheetId(), "ПРЕДМЕТИ!A3:H30"));
                List<List<Object>> valuesFromGroupSchedule = group.getSchedule().getSubjectListTable();

                if (valuesFromGroupSchedule == null || valuesFromGroupSchedule.isEmpty()) {
                    logger.warn("No data found in Group Schedule sheet. GroupName = {}, SheetID = {}", group.getGroupName(), group.getSheetId());
                } else {
                    group.getSchedule().setSubjectList(new HashMap<>());

                    for (List<Object> subject : valuesFromGroupSchedule) {
                        // Normalizing the size of all rows to avoid IndexOutOfBoundsException
                        int size = subject.size();
                        for (int i = 1; i <= 8 - size; i++) {
                            subject.add("");
                        }

                        if (!String.valueOf(subject.get(2)).trim().equals("") || !String.valueOf(subject.get(3)).trim().equals("")) {
                            group.getSchedule()
                                    .getSubjectList()
                                    .put(String.valueOf(subject.get(0)).trim().equals("") ? null : String.valueOf(subject.get(0)).trim(),
                                            new Subject(
                                                    String.valueOf(subject.get(1)).trim().equals("") ? null : String.valueOf(subject.get(1)).trim(),
                                                    String.valueOf(subject.get(2)).trim().equals("") ? null : String.valueOf(subject.get(2)).trim(),
                                                    String.valueOf(subject.get(3)).trim().equals("") ? null : String.valueOf(subject.get(3)).trim()
                                            )
                                    );
                        } else {
                            group.getSchedule()
                                    .getSubjectList()
                                    .put(String.valueOf(subject.get(0)).trim().equals("") ? null : String.valueOf(subject.get(0)).trim(),
                                            new Subject(
                                                    String.valueOf(subject.get(1)).trim().equals("") ? null : String.valueOf(subject.get(1)).trim(),

                                                    String.valueOf(subject.get(4)).trim().equals("") ? null : String.valueOf(subject.get(4)).trim(),
                                                    String.valueOf(subject.get(5)).trim().equals("") ? null : String.valueOf(subject.get(5)).trim(),

                                                    String.valueOf(subject.get(6)).trim().equals("") ? null : String.valueOf(subject.get(6)).trim(),
                                                    String.valueOf(subject.get(7)).trim().equals("") ? null : String.valueOf(subject.get(7)).trim()
                                            )
                                    );
                        }
                    }
                }
            }  catch (Exception e) {
                logger.error("Error while reading Group Schedule sheet. {}", group, e);
            }
        }
        logger.info("Successful update of data from Subject sheet.");
        logger.trace("Group list after update subject:\n{}", groups);
    }

    public static Group getGroupByName(String groupName) {
        return groups.stream()
                .filter(group -> groupName.equalsIgnoreCase(group.getGroupName()))
                .findAny()
                .orElse(null);
    }
}


