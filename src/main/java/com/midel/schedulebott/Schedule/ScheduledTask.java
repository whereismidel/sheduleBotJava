package com.midel.schedulebott.Schedule;

import com.midel.schedulebott.Config.ChatConfig;
import com.midel.schedulebott.Exceptions.MissingMessageExceptions;
import com.midel.schedulebott.Group.Group;
import com.midel.schedulebott.Group.GroupController;
import com.midel.schedulebott.TelegramBot.SendMessage;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.midel.schedulebott.Config.ChatConfig.debug;
import static com.midel.schedulebott.Config.ChatConfig.debugArray;


@Component
@EnableAsync
public class ScheduledTask {
    static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Scheduled(cron = "0 30 17 ? * MON,TUE,WED,THU,FRI,SUN", zone = "Europe/Kiev")
    public void alertForTomorrow() {

        ZonedDateTime currentZonedDate = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));
        currentZonedDate = currentZonedDate.plusDays(1);

        for(Group group : GroupController.groups) {
            try {
                group.getSettings().setDailyNotification(true);
                String message = "<b><u>ЗАВТРА</u></b> " + ScheduleController.getMessageForStartOfNewDay(group, currentZonedDate);
                new SendMessage().sendHTMLMessage(group.getChannelId(), message);

            } catch (Exception e) {
                logger.error("Failed to send schedule for tomorrow. {}", group, e);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(1500);
            } catch (InterruptedException e) {
                logger.warn("Failed to set delay in schedule for tomorrow.", e);
            }
        }
    }

    @Scheduled(cron = "0 35 7 ? * MON,TUE,WED,THU,FRI,SAT", zone = "Europe/Kiev")
    public void updateAndStartOfNewDay() {

        ZonedDateTime currentZonedDate = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));

        for(Group group : GroupController.groups) {
            int lastMessage;

            group.getSettings().setDailyNotification(true);

            // DEBUG FUNCTIONS
            if (debugArray != null && debug && debugArray.size() == 1) {
                group.getSettings().setDailyNotification(true);
                currentZonedDate = currentZonedDate.plusDays(debugArray.get(0));
                debugArray = null;
            }
            // END OF DEBUG FUNCTIONS

            try {
                String message = ScheduleController.getMessageForStartOfNewDay(group, currentZonedDate);
                lastMessage = new SendMessage().sendHTMLMessage(group.getChannelId(), message);

                // Deleting a message with a schedule for tomorrow.
                new SendMessage().deleteMessage(group.getChannelId(), lastMessage - 1);

                logger.info("Successful submission of the full schedule for the day. GroupName = {}", group.getGroupName());
            } catch (MissingMessageExceptions me){
                logger.warn(me.getMessage());
            } catch (Exception e) {
                logger.error("Failed to send schedule for today. {}", group, e);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(1500);
            } catch (InterruptedException e) {
                logger.warn("Failed to set delay in schedule for tomorrow.", e);
            }
        }
    }

    @Scheduled(cron = "0 0/5 7-19 ? * MON,TUE,WED,THU,FRI,SAT", zone = "Europe/Kiev")
    //@Scheduled(cron = "0/5 0/1 7-19 ? * MON,TUE,WED,THU,FRI,SAT", zone = "Europe/Kiev")
    public void checkAvailabilityOfLessonsEveryDay() {

        ZonedDateTime currentZonedDate = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));

        for(Group group : GroupController.groups) {
            // DEBUG FUNCTIONS
            if (debugArray != null && debug && debugArray.size() == 3) {
                group.getSettings().setDailyNotification(true);

                int scheduleNumber = currentZonedDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) % 2 == 0 ? 1 : 0;

                currentZonedDate = currentZonedDate.minusDays((currentZonedDate.getDayOfWeek().getValue()) + (7 * scheduleNumber));
                currentZonedDate = currentZonedDate.minusHours(currentZonedDate.getHour());
                currentZonedDate = currentZonedDate.minusMinutes(currentZonedDate.getMinute());
                currentZonedDate = currentZonedDate.minusSeconds(currentZonedDate.getSecond());
                currentZonedDate = currentZonedDate.minusNanos(currentZonedDate.getNano());

                int lesson = 8 * 60 + (debugArray.get(2) - 1) * 110 - ChatConfig.scheduleTime;
                currentZonedDate = currentZonedDate.plusDays(debugArray.get(0) + 7L * (debugArray.get(1) - 1));
                currentZonedDate = currentZonedDate.plusHours(lesson / 60);
                currentZonedDate = currentZonedDate.plusMinutes(lesson % 60);

                if (Objects.equals(GroupController.groups.get(GroupController.groups.size() - 1).getGroupName(), group.getGroupName())) {
                    debugArray = null;
                }
            }
            // END OF DEBUG FUNCTIONS

            try {
                Pair<String, Object[][]> message = ScheduleController.getMessageForCurrentLesson(group, currentZonedDate);

                if (message.getValue1() == null) {
                    new SendMessage().sendHTMLMessage(group.getChannelId(), message.getValue0());
                } else {
                     new SendMessage().sendInlineMessages(group.getChannelId(), message.getValue0(), message.getValue1());
                }

                logger.debug("Successful submission of the schedule. GroupName = {}", group.getGroupName());
            }  catch (MissingMessageExceptions e) {
                logger.debug("No lesson to send according to the schedule.");
            } catch (Exception ee){
                logger.warn("Failed to send message according to the schedule.");
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                logger.warn("Failed to set delay for sending scheduled lessons.", e);
            }
        }
    }

    @Scheduled(cron = "0 0 7 ? * *", zone = "Europe/Kiev")
    public void initAndUpdateGroupListFromSheet(){
        try {
            GroupController.updateGroupList();
            GroupController.updateGroupSchedule();
            GroupController.updateGroupScheduleSubjectInfo();

            logger.info("Successful initiation of data from tables.");
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Failed to initialize data from tables.", e);
        }
    }

    @Scheduled(cron = "0 10/2 7-19 ? * MON,TUE,WED,THU,FRI,SAT", zone = "Europe/Kiev")
    public void updateGroupInfo(){
        try {
            GroupController.updateGroupSchedule();
            GroupController.updateGroupScheduleSubjectInfo();
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Failed to update data from tables Schedule and Subject.", e);
        }
    }

}