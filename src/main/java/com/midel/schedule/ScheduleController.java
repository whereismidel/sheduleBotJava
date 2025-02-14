package com.midel.schedule;

import com.midel.config.ChatConfig;
import com.midel.exceptions.MissingMessageException;
import com.midel.exceptions.TooManyDaysException;
import com.midel.group.Day;
import com.midel.group.Group;
import com.midel.group.Subject;
import com.midel.type.Tuple;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;

public class ScheduleController {
    public static final String[] dayOfWeek = {"Неділя", "Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця", "Субота"};
    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);
    public static final ScheduleTime[] notificationSchedule = {
            new ScheduleTime( 8, 0, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,1),
            new ScheduleTime( 9,50, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,2),
            new ScheduleTime(11,40, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,3),
            new ScheduleTime(13,30, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,4),
            new ScheduleTime(15,20, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,5),
            new ScheduleTime(17,10, ChatConfig.lessonDurabilityInMinute, ChatConfig.scheduleTime,6)
    };

    public static String getMessageForStartOfNewDay(Group group, ZonedDateTime dateTime, boolean isGlobalCall, boolean isTomorrow) throws MissingMessageException {

        if (ChatConfig.sendSchedule){
            if (group != null && (group.getSettings().isState() || !isGlobalCall) && group.getSheetId() != null){
                return new FullScheduleNotifyMessage(dateTime, group, ChatConfig.startWeekNumber, isTomorrow, isGlobalCall).getMessage();
            } else {
                throw new MissingMessageException("Missing message to start today. Unknown group or state is off.");
            }
        } else {
            throw new MissingMessageException("Missing message to start today. SendSchedule is off.");
        }
    }

    /**
     *
     * @param group група, з якої отримується інформація
     * @param dateTime дата для перевірки
     * @param isGlobalCall прапор, який позначає, що виклик може призвести до змін в базі(false - змін не буде)
     * @return String - текст повідомлення.
     *         Object[][] - інлайн кнопки, за відсутності - null
     * @throws MissingMessageException якщо не потрібно нічого надсилати.
     */
    public static Pair<String, InlineKeyboardMarkup> getMessageForCurrentLesson(Group group, ZonedDateTime dateTime, boolean isGlobalCall) throws MissingMessageException {
        if (!ChatConfig.sendSchedule) {
            throw new MissingMessageException("SendSchedule has state off");
        }

        for (ScheduleTime time : notificationSchedule) {
            if (dateTime.getHour() == time.timeNotification.getHour() && dateTime.getMinute() == time.timeNotification.getMinute()) {
                if (group.getSettings().isDailyNotification() && group.getSettings().isState()) {

                    int weekNumber = dateTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + ChatConfig.globalCorrelation;

                    Day day;
                    try {
                        day = group.getSchedule().getWeeks().get((weekNumber + 1) % 2).getDay(dateTime.getDayOfWeek());
                    } catch (TooManyDaysException e) {
                        throw new MissingMessageException("No lesson to send according to the schedule. " + e);
                    }

                    Tuple<Subject> currentSubject = day.getLessonByNum(time.numberOfLesson - 1);

                    boolean isLastLesson = day.getLastLessonNum() == time.numberOfLesson;
                    if (time.numberOfLesson > day.getLastLessonNum()) {
                        group.getSettings().setDailyNotification(false);
                        throw new MissingMessageException("Current lesson after last lesson");
                    }

                    LessonNotifyMessage notifyMessage = new LessonNotifyMessage(dateTime, time, group, currentSubject, isLastLesson, isGlobalCall);

                    return new Pair<>(notifyMessage.getMessage(), notifyMessage.getButtons());
                }
            }
        }

        throw new MissingMessageException("No lesson to send according to the schedule. " + group.getGroupName());
    }
}