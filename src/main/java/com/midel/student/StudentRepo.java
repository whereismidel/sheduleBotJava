package com.midel.student;

import com.midel.config.DBConfig;
import com.midel.google.SheetAPI;
import com.midel.group.Group;
import com.midel.group.GroupController;
import com.midel.group.GroupRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class StudentRepo {
    public static final Map<String, Student> students = new HashMap<>();
    static final Logger logger = LoggerFactory.getLogger(StudentRepo.class);

    public static boolean importStudentList() {
        try {
            List<List<Object>> valuesFromStudents = SheetAPI.readSheetForRange(DBConfig.adminPanelInfoSheet, DBConfig.studentsListRange);

            if (valuesFromStudents == null || valuesFromStudents.isEmpty()) {
                logger.warn("No data found in \"Користувачі\"");
                students.clear();
            } else {
                for (List<Object> rowStudent : valuesFromStudents) {
                    Student student = new Student(
                            rowStudent.get(0).toString(),
                            rowStudent.get(1).toString().equals("null") ? null : rowStudent.get(1).toString().equals("yes"),
                            GroupController.getGroupByName(rowStudent.get(2).toString()),
                            LocalDateTime.now(ZoneId.of("Europe/Kiev")).minusMinutes(5)
                    );

                    logger.trace("{} - {} - {}", student.getId(), student.isLeader()==null?null:student.isLeader()?"leader":"student", student.getGroup());

                    if (students.containsKey(student.getId())){
                        students.get(student.getId()).copy(student);
                    } else {
                        students.put(student.getId(), student);
                    }
                }
                logger.info("Successful importing of the table \"Користувачі\"");
                logger.trace("Student list: \n{}", students);
            }


            return true;
        } catch (Exception e) {
            logger.error("Failed to import table \"Користувачі\"", e);
            return false;
        }
    }

    public static boolean exportStudentList(){

            List<List<Object>> studentToExport = new ArrayList<>();
            for (Student stud : students.values()) {
                studentToExport.add(stud.toList());
            }
            studentToExport.add(new ArrayList<>(Collections.nCopies((int) DBConfig.studentsEdge - 64, "")));

            if (SheetAPI.updateValues(DBConfig.adminPanelInfoSheet, DBConfig.studentsListRange, studentToExport)){
                logger.info("Successful export of the table \"Користувачі\".");
                return true;
            } else {
                logger.error("Error when exporting table \"Користувачі\".");
                return false;
            }

    }

    public static boolean addStudentToList(Student student) {
        try {
            if (students.containsKey(student.getId())){
                return false;
            } else {
                students.put(student.getId(), student);
                logger.info("Successfully added a student to the table \"Користувачі\".");

                if (!StudentRepo.exportStudentList()){
                    throw new Exception();
                }

                return true;
            }
        } catch (Exception e){
            logger.error("Failed to add and update the table \"Користувачі\" = {}", student, e);
            return false;
        }
    }

    public static boolean deleteStudentFromList(Student student){
        try {
            if (!students.containsKey(student.getId())){
                return false;
            } else {
                if (student.isLeader() && student.getGroup() != null) {
                    Group group = student.getGroup();

                    for (Student stud : students.values()) {
                        if (stud.getGroup() != null && stud.getGroup().getGroupName().equals(group.getGroupName())) {
                            stud.setGroup(null);
                        }
                    }

                    if (!GroupRepo.removeGroupFromList(group)){
                        logger.error("Failed to remove student = {}", student);
                        return false;
                    }
                }

                students.remove(student.getId());
                if (!StudentRepo.exportStudentList()){
                    throw new Exception();
                }

                logger.info("Successfully removed a student from the table \"Користувачі\". Student = {}", student);
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to remove student = {}", student, e);
            return false;
        }
    }
}
