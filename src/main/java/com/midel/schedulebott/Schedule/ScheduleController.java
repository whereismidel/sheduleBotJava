package com.midel.schedulebott.Schedule;

import com.midel.schedulebott.Config.ChatConfig;
import com.midel.schedulebott.Exceptions.MissingMessageExceptions;
import com.midel.schedulebott.Group.Group;
import com.midel.schedulebott.Group.Subject;
import com.midel.schedulebott.SheetAPI.SheetController;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.midel.schedulebott.Config.ChatConfig.sendSchedule;

public class ScheduleController {
    static String[] dayOfWeek = {"Неділя", "Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця", "Субота"};
    static String[] numberUnicode = {"\u0030\u20E3", "\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3", "\u0035\u20E3", "\u0036\u20E3", "\u0037\u20E3", "\u0038\u20E3", "\u0039\u20E3"};
    static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    public static final ScheduleController.ScheduleTime[] notificationSchedule = {
            new ScheduleTime( 8, 0, ChatConfig.scheduleTime,1),
            new ScheduleTime( 9,50, ChatConfig.scheduleTime,2),
            new ScheduleTime(11,40, ChatConfig.scheduleTime,3),
            new ScheduleTime(13,30, ChatConfig.scheduleTime,4),
            new ScheduleTime(15,20, ChatConfig.scheduleTime,5),
            new ScheduleTime(17,10, ChatConfig.scheduleTime,6)
    };

    /**
     * @param weekNumber номер тижня відносно року.
     * @return Val0 - ДеньТижня,
     *         Val1 - НомерРозкладу відповідного еквіваленту.
     */
    public static Pair<DayOfWeek, Integer> getEquivalentForSaturdayLessons(int weekNumber){

        // 34 - 1 субота
        // ДеньТижня/НомерРозкладу // День/Місяць
        // Масив еквівалентів - елемент масиву(день тижня(1 - понеділок, 5 - п'ятниця), номер розкладу(1/2)).
        int[][] weekdayLessonForSaturday = {
                {3,2}, //{1,12}
                {4,2}, //{1,12}
                {5,2},  //{2,12},
                {1,1},  //{5,12},
                {2,1},  //{6,12},
                {3,1},  //{7,12},
                {4,1},  //{8,12},
                {5,1},  //{9,12},
                {1,2},  //{12,12},
                {2,2},  //{13,12},
                {3,2},  //{14,12},
                {4,2},  //{15,12},
                {5,2}   //{16,12}
        };

        DayOfWeek day = DayOfWeek.of(weekdayLessonForSaturday[weekNumber-33][0]);
        int week = weekdayLessonForSaturday[weekNumber-33][1];

        return new Pair<>(day, week);
    }

    public static String getMessageForStartOfNewDay(Group group, ZonedDateTime zdt) throws MissingMessageExceptions {
        if (!sendSchedule || !group.getSettings().isState()){
            throw new MissingMessageExceptions("Missing message to start today. SendSchedule is false or group state is off");
        }

        int weekNumber = zdt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        StringBuilder formatString = new StringBuilder("<strong>" + dayOfWeek[zdt.getDayOfWeek().getValue()] + ", " + zdt.format(DateTimeFormatter.ofPattern("dd.MM")) + " (" + (weekNumber - 33) + " тиждень навчання) </strong>\n");

        formatString
                .append("\n<code>Розклад пар(")
                .append(weekNumber % 2 == 0 ? "II" : "I")
                .append(" тиждень):\n");

        ArrayList<Pair <String, String>> schedule;

        if (!zdt.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            if (weekNumber % 2 != 0) {
                schedule = group.getSchedule().getFirstWeek().get(zdt.getDayOfWeek().getValue() - 1);
            } else {
                schedule = group.getSchedule().getSecondWeek().get(zdt.getDayOfWeek().getValue() - 1);
            }
        } else {

            // 34 - 1 субота
            if (weekNumber - 33 < 0){
                group.getSettings().setDailyNotification(false);
                throw new MissingMessageExceptions("Missing message to start today. If this is an error, fix it. Sunday is < 0");
            }

            Pair<DayOfWeek, Integer> equivalent = ScheduleController.getEquivalentForSaturdayLessons(weekNumber);
            formatString
                    .append("**згідно перенесенню, розклад відповідно - ")
                    .append(dayOfWeek[equivalent.getValue0().getValue()])
                    .append("(")
                    .append(equivalent.getValue1() % 2 == 0 ? "II" : "I")
                    .append(" тиждень)\n");

            if (equivalent.getValue1() % 2 != 0) {
                schedule = group.getSchedule().getFirstWeek().get(equivalent.getValue0().getValue() - 1);
            } else {
                schedule = group.getSchedule().getSecondWeek().get(equivalent.getValue0().getValue() - 1);
            }
        }

        for (int i = 1; i <= 6; i++) {
            String lessonForFirstGroup = schedule.get(i - 1).getValue0();
            String lessonForSecondGroup = schedule.get(i - 1).getValue1();
            /*
                '-' and ('-' or  '') -> no lesson
                'value1' and (('value2' and ('value1' = 'value2')) or  '') -> same lesson
                ('value1' and 'value2') or ('value1' and '-') or ('-' and 'value2') -> different lesson
            */

            if (lessonForFirstGroup.equals("-") &&
                    (lessonForSecondGroup.equals("-") || lessonForSecondGroup.equals(""))){
                formatString.append(i).append(".     —\n\n");
                logger.debug("{} {}", lessonForFirstGroup, lessonForSecondGroup);
            } else if (!lessonForFirstGroup.equals("-") &&
                        ((!lessonForSecondGroup.equals("-") && lessonForFirstGroup.equals(lessonForSecondGroup)) || lessonForSecondGroup.equals(""))) {
                formatString
                        .append(i).append(notificationSchedule[i-1]
                                .timeForReal.format(DateTimeFormatter.ofPattern(". Початок - HH:mm\nІ/ІІ > ")))
                        .append(group.getSchedule()
                                .getSubjectByName(lessonForFirstGroup)
                                .getName()).append("\n\n");
            } else {
                lessonForFirstGroup = lessonForFirstGroup.equals("-") ? "—" : group.getSchedule().getSubjectByName(lessonForFirstGroup).getName();
                lessonForSecondGroup = lessonForSecondGroup.equals("-")  ? "—" : group.getSchedule().getSubjectByName(lessonForSecondGroup).getName();

                formatString.append(i).append(notificationSchedule[i-1].timeForReal.format(DateTimeFormatter.ofPattern(". Початок - HH:mm\n")));
                formatString.append("I   >  ").append(lessonForFirstGroup).append("\n");
                formatString.append("II  >  ").append(lessonForSecondGroup).append("\n\n");
            }

        }
        formatString.append(" </code>");

        return formatString.toString();
    }

    /**
     *
     * @param group група, з якої отримується інформація
     * @param zdt дата для перевірки
     * @return String - текст повідомлення.
     *         Object[][] - інлайн кнопки, за відсутності - null
     * @throws MissingMessageExceptions якщо не потрібно нічого надсилати.
     */
    public static Pair<String, Object[][]> getMessageForCurrentLesson(Group group, ZonedDateTime zdt) throws MissingMessageExceptions {
        for (ScheduleTime time : notificationSchedule){
            if (zdt.getHour() == time.timeNotification.getHour() &&
                    zdt.getMinute() == time.timeNotification.getMinute() &&
                    group.getSettings().isDailyNotification() &&
                    group.getSettings().isState() &&
                    sendSchedule){

                int weekNumber = zdt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

                Pair<Pair<Subject, Subject>, Boolean> currentSubject;
                Subject firstGroupSubject;
                Subject secondGroupSubject;

                if (zdt.getDayOfWeek() == DayOfWeek.SATURDAY) {

                    // 34 - 1 субота
                    if (weekNumber-33 < 0){
                        group.getSettings().setDailyNotification(false);
                        throw new MissingMessageExceptions("");
                    } else {

                        Pair<DayOfWeek, Integer> equivalent = ScheduleController.getEquivalentForSaturdayLessons(weekNumber);
                        currentSubject = group.getSchedule().getSubjectByNumber(equivalent.getValue0(), equivalent.getValue1(), time.numberOfLesson);
                    }

                } else {
                    currentSubject = group.getSchedule().getSubjectByNumber(zdt.getDayOfWeek(), weekNumber, time.numberOfLesson);
                }


                String formatString = "";

                if (currentSubject.getValue0() == null){

                    if (currentSubject.getValue1()){
                        group.getSettings().setDailyNotification(false);
                    }

                    throw new MissingMessageExceptions("");

                } else {
                    firstGroupSubject = currentSubject.getValue0().getValue0();
                    secondGroupSubject = currentSubject.getValue0().getValue1();

                    InlineKeyboardButton firstButton = new InlineKeyboardButton();
                    InlineKeyboardButton secondButton = new InlineKeyboardButton();
                    formatString += numberUnicode[time.numberOfLesson] + " пара за розкладом.";
                    if (firstGroupSubject != secondGroupSubject){
                        formatString += "\nПочаток в <b><i>"+ time.timeForReal.format(DateTimeFormatter.ofPattern("HH:mm")) + "</i></b>";
                        formatString += "\n\n<u>I підгрупа:</u>\n";
                        if (firstGroupSubject == null) {
                            formatString += "Пара відсутня.\n";
                        } else {
                            formatString += "<b>" + firstGroupSubject.getName() + "</b>\n";
                            if (firstGroupSubject.getNoteForFirstGroupAndGeneralLesson() != null) {
                                // if note doesnt contains "permanent" then remove this note from table
                                // else remove word "permanent"
                                if (!firstGroupSubject.getNoteForFirstGroupAndGeneralLesson().contains("PERMANENT")){
                                    group.getSchedule().getSubjectListTable()
                                            .stream()
                                            .filter(subject->subject.stream().anyMatch(cell-> cell.equals(firstGroupSubject.getName())))
                                            .collect(Collectors.toList()).get(0).set(5, "");

                                    // update table Subject with new values
                                    try {
                                        new SheetController().updateValues(group.getSheetId(), "ПРЕДМЕТИ!A3:H30", group.getSchedule().getSubjectListTable());
                                        logger.info("Updated Subject Sheet for {} in {}", group.getGroupName(), firstGroupSubject);
                                    } catch (IOException | GeneralSecurityException e) {
                                        logger.error("Failed to update Subject sheet data. SheetId = {}", group.getSheetId());
                                    }
                                }

                                formatString += firstGroupSubject.getNoteForFirstGroupAndGeneralLesson().replace("PERMANENT", "").trim() + "\n";
                            }

                            firstButton.setUrl(firstGroupSubject.getLinkForFirstGroupAndGeneralLesson());
                        }

                        formatString += "\n<u>II підгрупа:</u>\n";
                        if (secondGroupSubject == null){
                            formatString += "Пара відсутня.\n";
                        } else {
                            formatString += "<b>" + secondGroupSubject.getName() + "</b>\n";
                            if (secondGroupSubject.getNoteForSecondGroup() != null) {
                                // if note doesnt contains "permanent" then remove this note from table
                                // else remove word "permanent"
                                if (!secondGroupSubject.getNoteForSecondGroup().contains("PERMANENT")){
                                    group.getSchedule().getSubjectListTable()
                                            .stream()
                                            .filter(subject->subject.stream().anyMatch(cell-> cell.equals(secondGroupSubject.getName())))
                                            .collect(Collectors.toList()).get(0).set(7, "");

                                    // update table Subject with new values
                                    try {
                                        new SheetController().updateValues(group.getSheetId(), "ПРЕДМЕТИ!A3:H30", group.getSchedule().getSubjectListTable());
                                    } catch (IOException | GeneralSecurityException e) {
                                        logger.error("Failed to update Subject sheet data. SheetId = {}", group.getSheetId());
                                    }
                                }

                                formatString += secondGroupSubject.getNoteForSecondGroup().replace("PERMANENT", "").trim() + "\n";
                            }

                            secondButton.setUrl(secondGroupSubject.getLinkForSecondGroup());
                        }

                        if (currentSubject.getValue1()){
                            formatString += "\nЦе остання пара на сьогодні.";
                            formatString += "\nГарного дня \uD83C\uDF1A";

                            group.getSettings().setDailyNotification(false);
                        }

                        firstButton.setText("I підгрупа");
                        firstButton.setCallbackData("I");

                        secondButton.setText("II підгрупа");
                        secondButton.setCallbackData("II");

                        // Якщо посилання на кнопці відсутнє - кнопка не буде додана до повідомлення
                        if (firstButton.getUrl() == null && secondButton.getUrl() == null){
                            return new Pair<>(formatString, null);
                        } else {
                            if (firstButton.getUrl() != null && secondButton.getUrl() != null){
                                return new Pair<>(formatString, new Object[][]{{firstButton},{secondButton}});
                            } else {
                                if (firstButton.getUrl() == null) {
                                    return new Pair<>(formatString, new Object[][]{{secondButton}});
                                } else {
                                    return new Pair<>(formatString, new Object[][]{{firstButton}});
                                }
                            }
                        }

                    } else {
                        formatString += " <u>(спільна пара)</u>";
                        formatString += "\nПочаток в <b><i>"+ time.timeForReal.format(DateTimeFormatter.ofPattern("HH:mm")) + "</i></b>";
                        formatString += "\n\n<b>" + firstGroupSubject.getName() + "</b>\n";

                        if (firstGroupSubject.getNoteForFirstGroupAndGeneralLesson() != null){
                            if (!firstGroupSubject.getNoteForFirstGroupAndGeneralLesson().contains("PERMANENT")){
                                group.getSchedule().getSubjectListTable()
                                        .stream()
                                        .filter(subject->subject.stream().anyMatch(cell-> cell.equals(firstGroupSubject.getName())))
                                        .collect(Collectors.toList()).get(0).set(3, "");

                                // update table Subject with new values
                                try {
                                    new SheetController().updateValues(group.getSheetId(), "ПРЕДМЕТИ!A3:H30", group.getSchedule().getSubjectListTable());
                                } catch (IOException | GeneralSecurityException e) {
                                    logger.error("Failed to update Subject sheet data. SheetId = {}", group.getSheetId());
                                }
                            }

                            formatString += firstGroupSubject.getNoteForFirstGroupAndGeneralLesson().replace("PERMANENT", "").trim() + "\n";
                        }

                        if (currentSubject.getValue1()){
                            formatString += "\nЦе остання пара на сьогодні.";
                            formatString += "\nГарного дня \uD83C\uDF1A";

                            group.getSettings().setDailyNotification(false);
                        }

                        InlineKeyboardButton inbutton = new InlineKeyboardButton();
                        inbutton.setText("Перейти в Meet");

                        if (firstGroupSubject.getLinkForFirstGroupAndGeneralLesson() == null){
                            return new Pair<>(formatString, null);
                        } else {
                            inbutton.setUrl(firstGroupSubject.getLinkForFirstGroupAndGeneralLesson());
                            return new Pair<>(formatString, new Object[][]{{inbutton}});
                        }
                    }
                }
            }
        }
        throw new MissingMessageExceptions("");
    }

    public static class ScheduleTime{
        LocalTime timeNotification;
        LocalTime timeForReal;
        int numberOfLesson;

        ScheduleTime(int hourForReal, int minuteForReal, int minutesAhead, int numberOfLesson){
            this.timeForReal = LocalTime.of(hourForReal, minuteForReal);
            this.timeNotification = timeForReal.minusMinutes(minutesAhead);
            this.numberOfLesson = numberOfLesson;
        }
    }

}