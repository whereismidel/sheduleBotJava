package com.midel.group;

import com.midel.config.ChatConfig;
import com.midel.config.DBConfig;
import com.midel.exceptions.TooManyDaysException;
import com.midel.telegram.SendMessage;
import com.midel.type.Common;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.midel.google.SheetAPI.readSheetForRange;
import static com.midel.google.SheetAPI.updateValues;

public class GroupRepo {
    public static final Map<String, Group> groups = new HashMap<>();
    static final Logger logger = LoggerFactory.getLogger(GroupRepo.class);

    public static boolean importGroupList(){
        try {
            List<List<Object>> valuesFromGroupList = readSheetForRange(DBConfig.adminPanelInfoSheet, DBConfig.groupsListRange);

            if (valuesFromGroupList == null || valuesFromGroupList.isEmpty()) {
                logger.warn("No data found in \"Налаштування груп\"");
                groups.clear();
            } else {
                for (List<Object> rowGroup : valuesFromGroupList) {
                    String groupName = rowGroup.get(0).toString().equals("null") ? null : rowGroup.get(0).toString();
                    String leaderId = rowGroup.get(1).toString().equals("null") ? null : rowGroup.get(1).toString();
                    String leaderEmail = rowGroup.get(2).toString().equals("null") ? null : rowGroup.get(2).toString();
                    String channelId = rowGroup.get(3).toString().equals("null") ? null : rowGroup.get(3).toString();
                    String sheetId = rowGroup.get(4).toString().equals("null") ? null : rowGroup.get(4).toString();
                    Boolean state = rowGroup.get(5).toString().equals("null") ? null : rowGroup.get(5).toString().trim().equalsIgnoreCase("on");

                    LocalDateTime lastModifiedDate;
                    if (!rowGroup.get(6).toString().equals("null")){
                        lastModifiedDate = LocalDateTime.parse(rowGroup.get(6).toString());
                    } else {
                        lastModifiedDate = ChatConfig.startSemester;
                    }
                    Integer deleteMessage = rowGroup.get(7).toString().equals("null") ? null : Integer.parseInt(rowGroup.get(7).toString());

                    Group group = new Group(groupName, leaderId, leaderEmail, channelId, sheetId, state, lastModifiedDate, deleteMessage);

                    logger.trace("{} - {}", group.getGroupName(), group.getSettings().isState());

                    if (groups.containsKey(group.getGroupName())){
                        groups.get(group.getGroupName()).copy(group);
                    } else {
                        groups.put(group.getGroupName(), group);
                    }
                }
                logger.info("Successful import of the table \"Налаштування груп\"");
                logger.trace("Group info list: \n{}", groups);
            }

            return true;
        } catch (Exception e) {
            logger.error("Failed to import table \"Налаштування груп\"", e);
            return false;
        }


    }

    public static boolean exportGroupList(){
        List<List<Object>> groupToExport = new ArrayList<>();
        for (Group group : groups.values()) {
            groupToExport.add(group.toList());
        }
        groupToExport.add(new ArrayList<>(Collections.nCopies((int)DBConfig.groupEdge - 64, "")));

        if (updateValues(DBConfig.adminPanelInfoSheet, DBConfig.groupsListRange, groupToExport)){
            logger.info("Successful export of table \"Налаштування груп\"");
            return true;
        } else {
            logger.info("Failed to export of table \"Налаштування груп\"");
            return false;
        }
    }

    public static boolean addGroupToList(Group group){
        try {
            if (groups.containsKey(group.getGroupName())){
                return false;
            } else {
                groups.put(group.getGroupName(), group);
                logger.info("Successfully added a group to the group list sheet.");

                if (!GroupRepo.exportGroupList()){
                    throw new Exception();
                }

                return true;
            }
        } catch (Exception e){
            logger.error("Failed to add and update the group list sheet = {}", group, e);
            return false;
        }
    }

    public static boolean removeGroupFromList(Group group){
        try {
            if (!groups.containsKey(group.getGroupName())){
                return false;
            } else {
                groups.remove(group.getGroupName());
                exportGroupList();

                logger.info("Successfully removed a group from the group list sheet. Group = {}", group);
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to remove group = {}", group, e);
            return false;
        }
    }

    public static boolean importGroupSubjects(Group group){
        try {
            if (group.getSheetId() == null){
                logger.info("Table not yet created. groupName = {}", group.getGroupName());
                return true;
            }
            // initialize subject info for group from sheetId
            List<List<Object>> valuesFromGroupSchedule = readSheetForRange(group.getSheetId(), DBConfig.subjectTableRange);
            group.getSchedule().setSubjectListTable(valuesFromGroupSchedule);

            if (valuesFromGroupSchedule == null || valuesFromGroupSchedule.isEmpty()) {
                logger.warn("No data found in \"ПРЕДМЕТИ\". groupName = {}, sheetID = {}", group.getGroupName(), group.getSheetId());
                group.getSchedule().getSubjectListWithLink().clear();
                if (group.getSettings().isState() || group.getSettings().isValid()) {
                    new SendMessage().sendTextMessage(group.getLeaderId(), "Таблиця \"ПРЕДМЕТИ\" не містить жодного предмету. Першим ділом заповни її.\n");
                }
            } else {
                HashMap<String, SubjectFullInfo> subjectList = group.getSchedule().getSubjectInfo();

                // An up-to-date list of subject to compare and remove subject that are no longer on the subjectList
                Set<String> subjectNameActual = new HashSet<>();

                for (List<Object> subject : valuesFromGroupSchedule) {
                    // Normalizing the size of all rows to avoid IndexOutOfBoundsException
                    int size = subject.size();
                    for (int i = 1; i <= ((int)DBConfig.subjectEdge - 64) - size; i++) {
                        subject.add("");
                    }

                    String subjectName;
                    SubjectFullInfo subjectObj;
                    if (!String.valueOf(subject.get(2)).trim().isEmpty() || !String.valueOf(subject.get(3)).trim().isEmpty() ||
                            !String.valueOf(subject.get(4)).trim().isEmpty() || !String.valueOf(subject.get(5)).trim().isEmpty()) {
                        // Common Subject -> first group = second group
                        subjectName = String.valueOf(subject.get(0)).trim().isEmpty() ? null : String.valueOf(subject.get(0)).trim();

                        subjectObj = new SubjectFullInfo(
                                String.valueOf(subject.get(1)).trim().isEmpty() ? null : String.valueOf(subject.get(1)).trim(),
                                true,

                                String.valueOf(subject.get(2)).trim().isEmpty() ? null : String.valueOf(subject.get(2)).trim(),
                                String.valueOf(subject.get(3)).trim().isEmpty() ? null : String.valueOf(subject.get(3)).trim(),
                                String.valueOf(subject.get(4)).trim().isEmpty() ? null : String.valueOf(subject.get(4)).trim(),
                                String.valueOf(subject.get(5)).trim().isEmpty()
                                        || String.valueOf(subject.get(5)).trim().equals("not defined")? null : String.valueOf(subject.get(5)).trim(),

                                String.valueOf(subject.get(2)).trim().isEmpty() ? null : String.valueOf(subject.get(2)).trim(),
                                String.valueOf(subject.get(3)).trim().isEmpty() ? null : String.valueOf(subject.get(3)).trim(),
                                String.valueOf(subject.get(4)).trim().isEmpty() ? null : String.valueOf(subject.get(4)).trim(),
                                String.valueOf(subject.get(5)).trim().isEmpty()
                                        || String.valueOf(subject.get(5)).trim().equals("not defined")? null : String.valueOf(subject.get(5)).trim()
                        );
                    } else {
                        // Personal Subject for subgroups
                        subjectName = String.valueOf(subject.get(0)).trim().isEmpty() ? null : String.valueOf(subject.get(0)).trim();
                        subjectObj = new SubjectFullInfo(
                                String.valueOf(subject.get(1)).trim().isEmpty() ? null : String.valueOf(subject.get(1)).trim(),
                                false,

                                String.valueOf(subject.get(6)).trim().isEmpty() ? null : String.valueOf(subject.get(6)).trim(),
                                String.valueOf(subject.get(7)).trim().isEmpty() ? null : String.valueOf(subject.get(7)).trim(),
                                String.valueOf(subject.get(8)).trim().isEmpty() ? null : String.valueOf(subject.get(8)).trim(),
                                String.valueOf(subject.get(9)).trim().isEmpty()
                                        || String.valueOf(subject.get(9)).trim().equals("not defined")? null : String.valueOf(subject.get(9)).trim(),

                                String.valueOf(subject.get(10)).trim().isEmpty() ? null : String.valueOf(subject.get(10)).trim(),
                                String.valueOf(subject.get(11)).trim().isEmpty() ? null : String.valueOf(subject.get(11)).trim(),
                                String.valueOf(subject.get(12)).trim().isEmpty() ? null : String.valueOf(subject.get(12)).trim(),
                                String.valueOf(subject.get(13)).trim().isEmpty()
                                        || String.valueOf(subject.get(13)).trim().equals("not defined")? null : String.valueOf(subject.get(13)).trim()
                        );
                    }

                    if (subjectList.containsKey(subjectName)){
                        subjectList.get(subjectName).copy(subjectObj);
                        subjectNameActual.add(subjectName);
                    } else {
                        subjectList.put(subjectName, subjectObj);
                        subjectNameActual.add(subjectName);
                    }
                }

                // Removing irrelevant subject
                if (subjectNameActual.isEmpty()){
                    subjectList.clear();
                } else {
                    if (!subjectList.isEmpty()) {
                        for (String string : Stream.concat(
                                subjectList.keySet().stream().filter(c -> !subjectNameActual.contains(c)),
                                subjectNameActual.stream().filter(c -> !subjectList.containsKey(c))
                        ).collect(Collectors.toList())) {
                            subjectList.remove(string);
                        }
                    }
                }

                group.getSchedule().setSubjectListWithLink(new HashMap<>());
                for(String subjectName : subjectList.keySet()){
                    SubjectFullInfo subjectInfo = subjectList.get(subjectName);
                    if (subjectInfo.isCommon()){
                        group.getSchedule().getSubjectListWithLink().put(subjectName, new Common<>(subjectInfo.getLinkForFirstGroupAndGeneralLesson()));
                    } else {
                        group.getSchedule().getSubjectListWithLink().put(subjectName, new com.midel.type.Pair<>(subjectInfo.getLinkForFirstGroupAndGeneralLesson(), subjectInfo.getLinkForSecondGroup()));
                    }
                }
//                System.out.println(group.getSchedule().getSubjectListWithLink());

                logger.info("Successful importing of the table \"ПРЕДМЕТИ\". groupName = {}, sheetID = {}", group.getGroupName(), group.getSheetId());
//                System.out.println(group.getSchedule().getSubjectInfo());
                Pair<Boolean, ArrayList<String>> validate = GroupTablesValidator.scheduleInfoSheetValidator(group);

                if (!validate.getValue0() && (group.getSettings().isState() || group.getSettings().isValid())) {

                    if (group.getSettings().isState()) {
                        group.getSettings().setState(false);
                        exportGroupList();
                    }
                    group.getSettings().setValid(false);

                    ArrayList<String> warnings = validate.getValue1();
                    new SendMessage().sendLargeTextMessage(group.getLeaderId(), warnings, "При перевірці владки \"ПРЕДМЕТИ\" виникли проблеми.\n");
                }

            }

            return true;
        }  catch (Exception e) {
            logger.error("Failed to import table \"ПРЕДМЕТИ\". {}", group, e);
            return false;
        }
    }

    public static boolean importGroupSchedule(Group group){
        try {
            if (group.getSheetId() == null){
                return true;
            }

            List<List<Object>> valuesFromGroupSchedule = readSheetForRange(group.getSheetId(), DBConfig.scheduleTableRange);

            if (valuesFromGroupSchedule == null || valuesFromGroupSchedule.isEmpty()) {
                logger.warn("No data found in \"РОЗКЛАД\". groupName = {}, sheetID = {}", group.getGroupName(), group.getSheetId());
            } else {
                valuesFromGroupSchedule.subList(6, 12).clear(); // remove from values range A11:W16



                Week firstWeek = new Week();
                Week secondWeek = new Week();

                // fill schedule for first and second week
                for (int dayCells = 0; dayCells < (ChatConfig.isSaturdayLesson?6:5) * 4; dayCells += 4) {
                    /*
                        dayCells+0 - lesson number,
                        dayCells+1 - lesson for first subgroup,
                        dayCells+2 - lesson for second subgroup,
                        dayCells+3 - empty cell
                    */
                    // List of lessons for the day of the week in order
                    Day dayForFirstWeek = new Day(new ArrayList<>());
                    Day dayForSecondWeek = new Day(new ArrayList<>());

                    for (int lesson = 0; lesson < 6; lesson++) {
                        // Normalizing the size of all rows to avoid IndexOutOfBoundsException
                        for (int i = 0; i < ((int)DBConfig.dayScheduleEdge - 64) - valuesFromGroupSchedule.get(lesson).size(); i++)
                            valuesFromGroupSchedule.get(lesson).add("");
                        for (int i = 0; i < ((int)DBConfig.dayScheduleEdge - 64) - valuesFromGroupSchedule.get(lesson + 6).size(); i++) {
                            valuesFromGroupSchedule.get(lesson + 6).add("");
                        }
                        // lessons of the first week start from line 0 to 5 in the parsing list
                        String lessonForFirstGroup = String.valueOf(valuesFromGroupSchedule.get(lesson).get(dayCells + 1)).trim();
                        String lessonForSecondGroup = String.valueOf(valuesFromGroupSchedule.get(lesson).get(dayCells + 2)).trim();
                        dayForFirstWeek.addLesson(Subject.getLesson(lessonForFirstGroup, lessonForSecondGroup, group.getSchedule().getSubjectInfo()));

                        // lessons for the second week start from line 6 to 11 in the parsing list
                        lessonForFirstGroup = String.valueOf(valuesFromGroupSchedule.get(lesson + 6).get(dayCells + 1)).trim();
                        lessonForSecondGroup = String.valueOf(valuesFromGroupSchedule.get(lesson + 6).get(dayCells + 2)).trim();
                        dayForSecondWeek.addLesson(Subject.getLesson(lessonForFirstGroup, lessonForSecondGroup, group.getSchedule().getSubjectInfo()));
                    }

                    try {
                        System.out.println(dayForFirstWeek);
                        firstWeek.setDay(dayForFirstWeek, dayCells / 4);
                    } catch (TooManyDaysException tooManyDaysException){
                        logger.error("{} group = {}", tooManyDaysException.getMessage(), group);
                        firstWeek.setInvalid(true);
                        break;
                    }
                    try {
                        System.out.println(dayForSecondWeek);
                        secondWeek.setDay(dayForSecondWeek, dayCells / 4);
                    } catch (TooManyDaysException tooManyDaysException){
                        logger.error("{} group = {}", tooManyDaysException.getMessage(), group);
                        secondWeek.setInvalid(true);
                        break;
                    }
                }
                group.getSchedule().setWeeks(Arrays.asList(firstWeek, secondWeek));
                //System.out.println(group.getSchedule().getWeeks());

                logger.info("Successful importing of the table \"РОЗКЛАД\". groupName = {}, sheetID = {}", group.getGroupName(), group.getSheetId());
                
                // If validation is called by the user and the list is empty,
                // then the method for generating a list of subject based on the schedule table is called.
                if ((group.getSchedule().getSubjectInfo() == null || group.getSchedule().getSubjectInfo().isEmpty())
                        && (group.getSettings().isState() || group.getSettings().isValid()))
                {
                    if (GroupTablesValidator.isEmptyScheduleSheet(group)) {
                        if (group.getSettings().isState()) {
                            group.getSettings().setState(false);
                            exportGroupList();
                        }
                        group.getSettings().setValid(false);

                        new SendMessage().sendTextMessage(group.getLeaderId(), "Таблиця \"РОЗКЛАД\" порожня, заповни її на основі предметів з вкладки \"ПРЕДМЕТИ\".");
                    } else {
                        Pair<Boolean, String> result = GroupTablesValidator.generateSubjectInfoSheet(group);

                        new SendMessage().sendTextMessage(group.getLeaderId(), result.getValue1());

                        if (result.getValue0()) {
                            group.getSettings().setValid(false);
                        }
                    }
                }

                Pair<Boolean, ArrayList<String>> validate = GroupTablesValidator.scheduleSheetValidator(group);

                if (!validate.getValue0() && (group.getSettings().isState() || group.getSettings().isValid())) {
                    if (group.getSettings().isState()) {
                        group.getSettings().setState(false);
                        exportGroupList();
                    }
                    group.getSettings().setValid(false);

                    ArrayList<String> warnings = validate.getValue1();
                    new SendMessage().sendLargeTextMessage(group.getLeaderId(), warnings, "При перевірці владки \"РОЗКЛАД\" виникли проблеми.\n");
                }
            }


            return true;
        }  catch (Exception e) {
            logger.error("Failed to import table \"РОЗКЛАД\". {}", group, e);
            return false;
        }
    }

    public static boolean exportGroupSubjects(Group group, List<List<Object>> toExport){
        try {
            updateValues(group.getSheetId(), DBConfig.subjectTableRange, toExport);
            logger.info("Successful export Subject sheet. GroupName = {}, SheetID = {}", group.getGroupName(), group.getSheetId());
            return true;
        } catch (Exception e) {
            logger.error("Error while exporting Subject sheet. {}", group, e);
            return false;
        }
    }

    public static boolean importAllScheduleTables(){
        try {
            for (Group group : groups.values()) {

                if (group.getSheetId() == null) {
                    logger.info("Table not yet created. groupName = {}", group.getGroupName());
                    continue;
                }

                boolean successfulImport = false;

                for (int i = 0; i < 3; i++) {
                    successfulImport = importGroupSubjects(group);

                    if (!successfulImport) {
                        TimeUnit.SECONDS.sleep(10);
                    } else {
                        break;
                    }
                }
                if (!successfulImport) {
                    return false;
                }

                successfulImport = false;
                for (int i = 0; i < 3; i++) {
                    successfulImport = importGroupSchedule(group);

                    if (!successfulImport) {
                        TimeUnit.SECONDS.sleep(10);
                    } else {
                        break;
                    }
                }

                if (!successfulImport) {
                    return false;
                }

            }

            return true;
        } catch (InterruptedException e){
            logger.error("Wait error while retrying table import request.", e);
            return false;
        }
    }
}
