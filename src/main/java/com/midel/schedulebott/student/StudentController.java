package com.midel.schedulebott.student;

import com.midel.schedulebott.config.ChatConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class StudentController {

    public static Student getStudentById(String userId) {

        for(Student student : StudentRepo.students.values()){
            if (student.getId().equals(userId)){
                return student;
            }
        }
        return null;
    }

    public static boolean isFlood(String userId){
        Student student = getStudentById(userId);

        if (student == null){
            student = new Student(userId, null);
            StudentRepo.addStudentToList(student);
            return false;
        } else {
            if (student.getLastMessageTime() != null){

                int minimalCooldown = 1;
                long cooldown = Duration.between(student.getLastMessageTime(), LocalDateTime.now(ZoneId.of("Europe/Kiev"))).getSeconds();

                if (cooldown < minimalCooldown && !student.getId().equals(ChatConfig.ADMINS.stream().findFirst().orElse("-1"))){
                    student.setLastMessageTime(LocalDateTime.now(ZoneId.of("Europe/Kiev")));
                    return true;
                } else {
                    student.setLastMessageTime(LocalDateTime.now(ZoneId.of("Europe/Kiev")));
                    return false;
                }

            } else {
                student.setLastMessageTime(LocalDateTime.now(ZoneId.of("Europe/Kiev")));
                return false;
            }
        }
    }
}
