package com.midel.schedule;

import com.midel.exceptions.MissingMessageException;
import com.midel.group.Group;
import com.midel.group.SubGroup;
import com.midel.group.Subject;
import com.midel.telegram.SendMessage;
import com.midel.type.Common;
import com.midel.type.Pair;
import com.midel.type.Tuple;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LessonNotifyMessage {
    private final ZonedDateTime dateTime;
    private final ScheduleTime subjectTime;
    private final Group group;

    private Subject commonSubject;
    private Subject firstSubGroup;
    private Subject secondSubGroup;
    private final boolean isLastLesson;
    private final boolean isCommonLesson;
    private final boolean hasLesson;
    private final boolean isGlobalMessage;

    private boolean showLecture;
    private boolean showAuditory;
    private boolean showTimeBorderOfLesson;
    private boolean showAbsenceLesson;


    private static final String titlePart = "%s пара за розкладом. %s\n\n"; // "Номер пари" пара за розкладом. "чи спільна"
    private static final String timeBorderPart = "\u23F3 Триватиме з <b><i>%s</i></b> по <b><i>%s</i></b>\n\n";

    private static final String[] lastLessonPart = new String[]{
            "Це була остання пара на сьогодні, більше не турбую \uD83D\uDE22",
            "На сьогодні в мене все \uD83D\uDC4B",
            "Досить вчитись, це остання пара \uD83D\uDC40",
            "Можна видихнути з полегшенням це остання пара \uD83D\uDC7D"
    };
    private static final String absencePairPart = "Немає пари.\n\n";
    private static final String subgroupPairPart =
            "<u>I підгрупа:</u>\n" +
            "%s" + // pairInfoPart
            "<u>II підгрупа:</u>\n" +
            "%s"; // pairInfoPart
    private static final String pairInfoPart =
            "<b>%s</b>\n" + // Назва пари
                    "%s" + // Примітка до пари (якщо вказана)
                    "%s\n";   // Викладач | аудиторія (якщо вказані)

    public static final String[] dayOfWeek = {"Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця", "Субота", "Неділя"};
    public static final String[] numberUnicode = {"\u0030\u20E3", "\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3", "\u0035\u20E3", "\u0036\u20E3", "\u0037\u20E3", "\u0038\u20E3", "\u0039\u20E3"};

    public static final String commonLinkMessage = "Перейти за посиланням";
    public static final String subgroupLinkMessage = "%s підгрупа";

    public LessonNotifyMessage(ZonedDateTime dateTime,
                               ScheduleTime subjectTime,
                               Group group,
                               Tuple<Subject> currentSubject,
                               boolean isLastLesson,
                               boolean isGlobalMessage)
    {

        this.dateTime = dateTime;
        this.subjectTime = subjectTime;
        this.group = group;

        this.isLastLesson = isLastLesson;
        this.isGlobalMessage = isGlobalMessage;
        this.hasLesson = !(currentSubject instanceof Common && ((Common<Subject>) currentSubject).get() == null);

        if (this.hasLesson){
            if (currentSubject instanceof Common){
                this.isCommonLesson = true;
                this.commonSubject = ((Common<Subject>) currentSubject).get();
            } else {
                this.isCommonLesson = false;

                if (currentSubject instanceof Pair){
                    this.firstSubGroup = ((Pair<Subject>) currentSubject).get(0);
                    this.secondSubGroup = ((Pair<Subject>) currentSubject).get(1);

                }
            }
        } else {
            this.isCommonLesson = true;
        }

        this.showLecture = group.getSettings().getShowLecture();
        this.showAuditory = group.getSettings().getShowAuditory();
        this.showTimeBorderOfLesson = group.getSettings().getShowTimeBorder();
        this.showAbsenceLesson = group.getSettings().getShowAbsenceLesson();
    }

    public String getMessage() throws MissingMessageException {
        if (dateTime.getDayOfWeek() == DayOfWeek.SATURDAY
                || dateTime.getDayOfWeek() == DayOfWeek.SUNDAY){
            // ToDo внести сюди зміни якщо є заняття по вихідним
            throw new MissingMessageException("Сьогодні вихідний.");
        }

        StringBuilder notificationMessage = new StringBuilder(150);

        // Заголовок - номер пари та чи спільна пара
        notificationMessage.append(
                String.format(titlePart,
                        numberUnicode[subjectTime.numberOfLesson],
                        isCommonLesson ?"(спільна)":"")
        );

        if (!hasLesson){
            // Якщо пар для підгруп нема - кидаю виключення
            if (isLastLesson) {
                if (isGlobalMessage){
                    group.getSettings().setDailyNotification(false);
                }

                if (showAbsenceLesson){
                    notificationMessage
                            .append(absencePairPart)
                            .append(lastLessonPart[new Random().nextInt(lastLessonPart.length)]);
                    return notificationMessage.toString();
                } else {
                    throw new MissingMessageException("Пара відсутня і пар більше нема.");
                }
            } else {
                if (showAbsenceLesson){
                    notificationMessage.append(absencePairPart);
                    return notificationMessage.toString();
                } else {
                    throw new MissingMessageException("Пара відсутня.");
                }
            }
        }

        // Відображення початку і кінця пари
        if (showTimeBorderOfLesson){
            notificationMessage.append(
                    String.format(
                            timeBorderPart,
                            subjectTime.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            subjectTime.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            );
        }

        // Відображення предмету/предметів
        List<String> subgroupPairMessage = new ArrayList<>();
        for (Subject subject : isCommonLesson ?
                                Collections.singletonList(commonSubject) : Arrays.asList(firstSubGroup, secondSubGroup))
        {
            if (subject != null) {

                String note = "";
                if (subject.getNote() != null){
                    note = subject.removeNoteIfNotPermanent(subject.getNote(), isGlobalMessage, group, isCommonLesson ?SubGroup.COMMON:subgroupPairMessage.size() == 0?SubGroup.FIRST_GROUP:SubGroup.SECOND_GROUP);
                    if (!subject.getNote().isPermanent()) {
                        subject.setNote(null);
                    }

//                    note = subject.getNotes().stream()
//                            .map(n -> subject.removeNoteIfNotPermanent(n, isGlobalMessage, group, isCommonLesson ?SubGroup.COMMON:subgroupPairMessage.size() == 0?SubGroup.FIRST_GROUP:SubGroup.SECOND_GROUP))
//                            .collect(Collectors.joining(""));
                    note = "\u26A1 " + note + "\n";
                }

                String lector = !showLecture || subject.getLector() == null ? "" : "\u2708 " + subject.getLector() + "\n";
                String auditory = !showAuditory || subject.getAuditory() == null ? "" : "\uD83C\uDFE2 " + subject.getAuditory() + "\n";
                subgroupPairMessage.add(
                        String.format(
                                pairInfoPart,
                                subject.getTitleForMessage(),
                                note,
                                lector + auditory
                        )
                );
            } else {
                subgroupPairMessage.add(absencePairPart);
            }
        }

        if (subgroupPairMessage.size() == 2){
            notificationMessage.append(
                String.format(
                        subgroupPairPart,
                        subgroupPairMessage.get(0),
                        subgroupPairMessage.get(1)
                )
            );
        } else {
            notificationMessage.append(subgroupPairMessage.get(0));
        }

        if (isLastLesson){
            notificationMessage
                    .append(lastLessonPart[new Random().nextInt(lastLessonPart.length)]);

            if (isGlobalMessage){
                group.getSettings().setDailyNotification(false);
            }
        }

        return notificationMessage.toString();
    }

    public InlineKeyboardMarkup getButtons(){
        if (isCommonLesson){
            if (!hasLesson){
                return null;
            } else {
                String link = group.getSchedule().getLinkForSubgroup(commonSubject.getKeyName(), SubGroup.COMMON);
                if (link == null){
                    return null;
                }

                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(getFormattedLink(commonLinkMessage, link));
                button.setCallbackData("common link " + group.getGroupName());
                button.setUrl(link);

                return SendMessage.getInlineKeyboardMarkup(new Object[][]{{button}}, null);
            }
        } else {
            ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();
            StringBuilder groupNum = new StringBuilder("I");
            for(String link : Arrays.asList(
                    group.getSchedule().getLinkForSubgroup(firstSubGroup == null?"":firstSubGroup.getKeyName(), SubGroup.FIRST_GROUP),
                    group.getSchedule().getLinkForSubgroup(secondSubGroup == null?"":secondSubGroup.getKeyName(), SubGroup.SECOND_GROUP)
                    ))
            {
                if (link != null){
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(getFormattedLink(groupNum + " підгрупа", link));
                    button.setCallbackData(groupNum + " link " + group.getGroupName());
                    button.setUrl(link);

                    buttons.add(button);
                }
                groupNum.append("I");
            }
            if (buttons.size() == 1){
                return SendMessage.getInlineKeyboardMarkup(new Object[][]{{buttons.get(0)}}, null);
            } else if (buttons.size() == 2){
                return SendMessage.getInlineKeyboardMarkup(new Object[][]{{buttons.get(0)}, {buttons.get(1)}}, null);
            } else {
                return null;
            }

        }
    }
    private String getFormattedLink(String startWith, String link){
        String linkMessage;
        if (link.contains("meet"))
            linkMessage = startWith + "(Meet)";
        else if (link.contains("classroom"))
            linkMessage = startWith + "(Classroom)";
        else
            linkMessage = startWith;

        return linkMessage;
    }
    public LessonNotifyMessage setShowLecture(boolean showLecture) {
        this.showLecture = showLecture;
        return this;
    }

    public LessonNotifyMessage setShowAuditory(boolean showAuditory) {
        this.showAuditory = showAuditory;
        return this;
    }

    public LessonNotifyMessage setShowTimeBorderOfLesson(boolean showTimeBorderOfLesson) {
        this.showTimeBorderOfLesson = showTimeBorderOfLesson;
        return this;
    }

    public LessonNotifyMessage setShowAbsenceLesson(boolean showAbsenceLesson) {
        this.showAbsenceLesson = showAbsenceLesson;
        return this;
    }
}
