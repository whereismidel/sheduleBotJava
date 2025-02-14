package com.midel.schedule;

import com.midel.config.ChatConfig;
import com.midel.exceptions.MissingMessageException;
import com.midel.exceptions.TooManyDaysException;
import com.midel.group.Day;
import com.midel.group.Group;
import com.midel.group.Subject;
import com.midel.type.Common;
import com.midel.type.Pair;
import com.midel.type.Tuple;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Arrays;

public class FullScheduleNotifyMessage {

    private final ZonedDateTime dateTime;
    private final Group group;
    private final boolean isTomorrow;
    private final long startWeekNumber;

    private final int weekNumber;

    public static final String[] dayOfWeek = {"Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця", "Субота", "Неділя"};
    public static final ScheduleTime[] notificationSchedule = {
            new ScheduleTime( 8, 0, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,1),
            new ScheduleTime( 9,50, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,2),
            new ScheduleTime(11,40, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,3),
            new ScheduleTime(13,30, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,4),
            new ScheduleTime(15,20, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,5),
            new ScheduleTime(17,10, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,6)
    };

    private static final String titlePart = "<strong>%s, %s (%s тиждень навчання)</strong>\n\n";
    private static final String tomorrowTitlePart = "<b><u>ЗАВТРА</u></b> <strong>%s, %s (%s тиждень навчання)</strong>\n\n";
    private static final String lessonsPart = "<code>Розклад пар(%s тиждень):\n%s</code>";
    public static final String numLessonPart = "%s. %s - %s\n";
    public static final String missingSingleLessonPart = "%s.     —\n";
    public static final String titleLessonPart = "%-3s > %s\n";
    public static final String missingSubgroupLessonPart = "%-3s >  —\n";

    public static final String missingFullSchedule = "<code>У розкладі пар немає</code> \uD83C\uDF1A";

    public FullScheduleNotifyMessage(ZonedDateTime dateTime, Group group, long startWeekNumber, boolean isTomorrow, boolean isGlobal){
        this.dateTime = dateTime;
        this.group = group;
        this.startWeekNumber = startWeekNumber;
        this.isTomorrow = isTomorrow;

        this.weekNumber = dateTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + ChatConfig.globalCorrelation;
    }

    public String getMessage() throws MissingMessageException {

        if ((dateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !ChatConfig.isSaturdayLesson) || dateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            throw new MissingMessageException("No lessons on weekends.");
        }

        if (dateTime.isBefore(ChatConfig.startSemester.atZone(ZoneId.of("Europe/Kiev"))) || dateTime.isAfter(ChatConfig.endSemester.atZone(ZoneId.of("Europe/Kiev")))){
            throw new MissingMessageException("Study ended/didn't start");
        }

        StringBuilder notificationMessage= new StringBuilder(300);

        notificationMessage.append(
            String.format(
                    isTomorrow? tomorrowTitlePart : titlePart,
                    dayOfWeek[(dateTime.getDayOfWeek().getValue()+6) % 7],
                    dateTime.format(DateTimeFormatter.ofPattern("dd.MM")),
                    weekNumber - startWeekNumber
            )
        );

        Day day;

        try {
             day = group.getSchedule().getWeeks().get((weekNumber+ChatConfig.startWeek-1) % 2).getDay(dateTime.getDayOfWeek());
        } catch (TooManyDaysException e) {
            throw new MissingMessageException("Missing message for " + dateTime.getDayOfWeek());
        }

        StringBuilder listOfLesson = new StringBuilder();
        boolean onlyEmptyLesson = true;
        for(int i = 0; i < day.getNumberOfLessons(); i++){
            Tuple<Subject> lesson = day.getLessonByNum(i);

            if (lesson instanceof Common){
                if (((Common<Subject>) lesson).get() == null){
                    listOfLesson.append(
                            String.format(
                                    missingSingleLessonPart,
                                    i+1
                            )
                    ).append("\n");
                } else {
                    onlyEmptyLesson = false;
                    listOfLesson.append(
                            String.format(
                                    numLessonPart,
                                    i+1,
                                    notificationSchedule[i].getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    notificationSchedule[i].getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                            )
                    );

                    listOfLesson.append(
                            String.format(
                                    titleLessonPart,
                                    "I/II",
                                    ((Common<Subject>) lesson).get().getTitleForMessage()
                            )
                    ).append("\n");
                }
            } else {
                onlyEmptyLesson = false;
                listOfLesson.append(
                        String.format(
                                numLessonPart,
                                i+1,
                                notificationSchedule[i].getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                notificationSchedule[i].getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                );

                String subGroupNum = "I";

                for(Subject subject : Arrays.asList(((Pair<Subject>) lesson).get(0), ((Pair<Subject>) lesson).get(1))) {
                    if (subject == null){
                        listOfLesson.append(String.format(missingSubgroupLessonPart, subGroupNum));
                    } else {
                        listOfLesson.append(String.format(titleLessonPart, subGroupNum, " " + subject.getTitleForMessage()));
                    }
                    subGroupNum += "I";
                }
                listOfLesson.append("\n");
            }
        }

        if (onlyEmptyLesson){
            notificationMessage.append(missingFullSchedule);
        } else {
            notificationMessage.append(String.format(lessonsPart, (weekNumber+ChatConfig.startWeek-1) % 2 == 0?"I" : "II",listOfLesson));
        }

        return notificationMessage.toString();
    }
}
