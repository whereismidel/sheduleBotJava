package com.midel.command;

import com.midel.command.annotation.GroupCommand;
import com.midel.command.annotation.UserCommand;
import com.midel.config.ChatConfig;
import com.midel.exceptions.MissingMessageException;
import com.midel.group.Group;
import com.midel.group.GroupController;
import com.midel.keyboard.inline.InlineKeyboardAnswer;
import com.midel.schedule.ScheduleController;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


/**
 * GetScheduleUser {@link Command}.
 */

@GroupCommand
@UserCommand
public class GetScheduleUserCommand extends Command{

    public final static String INVALID_ARGUMENT_MESSAGE = "Невірно вказані аргументи або їх кількість(можливо розклад не пройшов перевірку через меню)\\.\n"
            +"`/sch` \\- для довідки\\.";
    public final static String GROUP_NOT_FOUND_MESSAGE = "Розкладу для групи з такою назвою не знайдено.\n"
            +"Ваш староста ще не створив його або була помилка при введені.";
    public final static String SCHEDULE_NOT_FOUND_MESSAGE = "Розкладу на цей день немає, оскільки навчання не почалось/закінчилось.";
    private static final String MISSING_MESSAGE = "<strong>%s, %s</strong>\n" +
            "\n<pre>В цей день пари не проводяться або закінчився навчальний процес.</pre>";

    public final static String INFO_MESSAGE = "Для того, щоб отримати розклад на конкретний день введіть\\:\n"
//            +"`/sch` `назва групи` `день\\.місяць`\n"
//            + "*Приклад\\:*\n"
//            + "`/sch БІ\\-244Б 27\\.08`\n"
//            + "`/sch БІ\\-244Б\\(А\\) 01\\.10`\n\n"
//            + "Якщо ти встановив групу в налаштуваннях, то тобі доступний синтаксис\\:\n"
            + "`/sch` `день\\.місяць`";

//    public final static String GROUP_NOT_SET_MESSAGE = "<b>Потрібна прив'язка до групи.</b>\n"
//            +"Для початку вкажи групу, в якій навчаєшся:\n"
//            +"<i>Наприклад:</i>\n"
//            +"БІ-144Б\n"
//            +"СЗ-312Б(А)\n\n"
//            +"<i>*Для відповіді відміть це повідомлення(якщо це не сталось автоматично)</i>";

    public static final String GROUP_NOT_SET_MESSAGE = "<b>Група не вказана</b>\n" +
            "Для того щоб користуватись ботом і переглядати розклад своєї групи запитай у старости код для приєднання, або у відповідь до цього повідомлення надішли ідентифікатор групи.";


    public final static String STUDENT_NOT_REGISTERED_MESSAGE = "<b>Ти ще не зареєстрований</b>\n"
            +"Введи команду /start\n\n"
            +"<i>Якщо сталась помилка і ти вже реєструвався спробуй /restart або звернись до " + ChatConfig.creatorUsername + "</i>";

    public GetScheduleUserCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();

        if (arguments != null && arguments.size() <= 1) {
            ZonedDateTime zdt = ZonedDateTime.now();
            Group group;
            switch (arguments.size()){
                case 0:{
                    sendMessage.sendMarkupV2Message(userId, INFO_MESSAGE);
                    break;
                }
                case 1:{
                    Student student = StudentController.getStudentById(userId);
                    if (student != null) {
                        if (student.getGroup() != null){
                            arguments = Arrays.asList(student.getGroup().getGroupName(), arguments.get(0));
                            // go to case 2
                        } else {
                            sendMessage.replyMessage(userId, GROUP_NOT_SET_MESSAGE);
                            break;
                        }
                    } else {
                        sendMessage.sendHTMLMessage(userId, STUDENT_NOT_REGISTERED_MESSAGE);
                        break;
                    }
                }
                case 2:{
                    String[] date;
                    int day, month;

                    group = GroupController.getGroupByName(arguments.get(0).replace("/", " "));
                    try {
                        if (group == null){
                            sendMessage.sendHTMLMessage(userId, GROUP_NOT_FOUND_MESSAGE);
                            return;
                        }

                        date = arguments.get(1).split("\\.");
                        if (date.length != 2){
                            throw new Exception();
                        }

                        day = Integer.parseInt(date[0]);
                        month = Integer.parseInt(date[1]);
                        zdt = ZonedDateTime.of(LocalDateTime.of(zdt.getYear(), month, day, 5, 0, 0),
                                ZoneId.of("Europe/Kiev"));

                        if (zdt.isBefore(ZonedDateTime.of(ChatConfig.startSemester, ZoneId.of("Europe/Kiev"))) ||
                                zdt.isAfter(ZonedDateTime.of(ChatConfig.endSemester, ZoneId.of("Europe/Kiev")))){
                            sendMessage.sendTextMessage(userId, SCHEDULE_NOT_FOUND_MESSAGE);
                            return;
                        }

                        if (zdt.getDayOfWeek().equals(DayOfWeek.SUNDAY) || (zdt.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !ChatConfig.isSaturdayLesson)) {
                            sendMessage.sendInlineKeyboard(userId,
                                    String.format(MISSING_MESSAGE, ScheduleController.dayOfWeek[zdt.getDayOfWeek().getValue() % 7], zdt.format(DateTimeFormatter.ofPattern("dd.MM"))),
                                    new Object[][]{
                                            {InlineKeyboardAnswer.ARROW_PREVIOUS_DAY, InlineKeyboardAnswer.ARROW_CURRENT_DAY, InlineKeyboardAnswer.ARROW_NEXT_DAY}
                                    },
                                    update.getMessage().getFrom().getId() + "/" + group.getGroupName().replace("#", "=") + "/" + zdt.toEpochSecond());
                        } else {
                            String formatMsg = ScheduleController.getMessageForStartOfNewDay(group, zdt, false, false);
                            sendMessage.sendInlineKeyboard(userId,
                                    formatMsg,
                                    new Object[][]{
                                            {InlineKeyboardAnswer.ARROW_PREVIOUS_DAY, InlineKeyboardAnswer.ARROW_CURRENT_DAY, InlineKeyboardAnswer.ARROW_NEXT_DAY}
                                    },
                                    update.getMessage().getFrom().getId() + "/" + group.getGroupName().replace("#", "=") + "/" + zdt.toEpochSecond());
                        }

                    } catch (MissingMessageException mm) {
                        sendMessage.sendInlineKeyboard(userId,
                                String.format(MISSING_MESSAGE, ScheduleController.dayOfWeek[zdt.getDayOfWeek().getValue() % 7], zdt.format(DateTimeFormatter.ofPattern("dd.MM"))),
                                new Object[][]{
                                        {InlineKeyboardAnswer.ARROW_PREVIOUS_DAY, InlineKeyboardAnswer.ARROW_CURRENT_DAY, InlineKeyboardAnswer.ARROW_NEXT_DAY}
                                },
                                update.getMessage().getFrom().getId() + "/" + group.getGroupName().replace("#", "=") + "/" + zdt.toEpochSecond());
                    } catch (Exception e) {
                        sendMessage.sendMarkupV2Message(userId, INVALID_ARGUMENT_MESSAGE);
                    }
                    break;
                }
                default:{
                    sendMessage.sendMarkupV2Message(userId, INVALID_ARGUMENT_MESSAGE);
                    break;
                }
            }

        } else {
            new NoCommand(sendMessage).execute(update);
        }
    }
}