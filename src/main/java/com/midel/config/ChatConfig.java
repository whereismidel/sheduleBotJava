package com.midel.config;

import com.midel.command.CommandName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.WEEKS;

public class ChatConfig {
    // debug mode
    public static final List<String> ADMINS = Collections.singletonList("787943933");
    public static final String creatorUsername = "@Midell";
    public static boolean debug = false;

    /**
     * The first element must always be {@link CommandName} of the command from which the function is called.
     */
    public static ArrayList<Object> debugArray= new ArrayList<>();

    // Global setting if there are fatal errors
    public static boolean sendSchedule = true;

    // за скільки до початку пари повідомляти про неї. ToDO
    public static final int scheduleTime = 5; // якщо змінювати - слідкувати, щоб крон вираз був кратний по хвилинам.


    // Рік навчання
    public static final int year = 2024;
    // Дата початку семестру
    public static final LocalDateTime startSemester = LocalDateTime.of(year,9,2,0,0, 1, 1);
    // Дата кінця семестру
    public static final LocalDateTime endSemester = LocalDateTime.of(year,11,3,23,59, 59, 1);
    public static final int lessonDurabilityInMinute = 95;
    // Номер тижня від початку року, з якого почався семестр
    public static final long startWeekNumber =  WEEKS.between(LocalDateTime.of(year,1,1,0,0), startSemester);
    // Чи потрібні пари по суботам
    public static final boolean isSaturdayLesson = true;
    public static int startWeek = 1;
}
