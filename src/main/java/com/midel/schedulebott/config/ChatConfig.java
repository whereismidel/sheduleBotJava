package com.midel.schedulebott.config;

import com.midel.schedulebott.command.CommandName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatConfig {
    // debug mode
    public static final List<String> ADMINS = Collections.singletonList("787943933");
    public static boolean debug = false;

    /**
     * The first element must always be {@link CommandName} of the command from which the function is called.
     */
    public static ArrayList<Object> debugArray= new ArrayList<>();

    // Global setting if there are fatal errors
    public static boolean sendSchedule = true;

    // за скільки до початку пари повідомляти про неї.
    public static int scheduleTime = 5; // якщо змінювати - слідкувати, щоб крон вираз був кратний по хвилинам.
    public static LocalDateTime startSemester = !debug?
                                                LocalDateTime.of(2022,8,22,0,0):
                                                LocalDateTime.of(2020,1,22,0,0);
    public static LocalDateTime endSemester = !debug?
                                                LocalDateTime.of(2022,11,30,23,59):
                                                LocalDateTime.of(2030,12,30,23,59);
    public static boolean isSaturdayLesson = true;
}
