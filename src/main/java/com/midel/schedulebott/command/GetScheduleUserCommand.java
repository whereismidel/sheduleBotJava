package com.midel.schedulebott.command;

import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.group.Group;
import com.midel.schedulebott.group.GroupController;
import com.midel.schedulebott.schedule.ScheduleController;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static com.midel.schedulebott.command.CommandName.NO;


/**
 * GetScheduleUser {@link Command}.
 */
public class GetScheduleUserCommand extends Command{

    public final static String INVALID_ARGUMENT_MESSAGE = "Невірно вказані аргументи або їх кількість\\.\n"
            +"`/розклад` \\- для довідки\\.";
    public final static String GROUP_NOT_FOUND_MESSAGE = "Розкладу для групи з такою назвою не знайдено.\n"
            +"Ваш староста ще не створив його або була помилка при введені.";
    public final static String SCHEDULE_NOT_FOUND_MESSAGE = "В обраний день пари не проводяться та/або навчання не почалось/закінчилось.";
    public final static String INFO_MESSAGE = "Для того, щоб отримати розклад на конкретний день введіть\\:\n"
            +"`/розклад` `назва групи` `день\\.місяць`\n"
            + "*Приклад\\:*\n"
            + "`/розклад БІ\\-244Б 27\\.08`\n"
            + "`/розклад БІ\\-244Б\\(А\\) 01\\.10`\n\n"
            + "Якщо ти встановив групу в налаштуваннях, то тобі доступний синтаксис\\:\n"
            + "`/розклад` `день\\.місяць`";

    public final static String GROUP_NOT_SET_MESSAGE = "<b>Вкажіть групу</b>\n"
            +"Для початку вкажи групу, в якій навчаєшся:\n"
            +"<i>Наприклад:</i>\n"
            +"БІ-144Б\n"
            +"СЗ-312Б(А)\n\n"
            +"<i>*Для відповіді відміть це повідомлення(якщо це не сталось автоматично)</i>";

    public final static String STUDENT_NOT_REGISTERED_MESSAGE = "<b>Ти ще не зареєстрований</b>\n"
            +"Введи команду /start\n\n"
            +"<i>Якщо сталась помилка і ти вже реєструвався спробуй /restart або звернись до " + ChatConfig.creatorUsername + "</i>";

    public GetScheduleUserCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();

        if (arguments != null) {
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

                    try {
                        date = arguments.get(1).split("\\.");
                        if (date.length != 2){
                            throw new Exception();
                        }
                        day = Integer.parseInt(date[0]);
                        month = Integer.parseInt(date[1]);
                        zdt = ZonedDateTime.of(LocalDateTime.of(zdt.getYear(), month, day, 5, 0, 0),
                                ZoneId.of("Europe/Kiev"));

                        group = GroupController.getGroupByName(arguments.get(0));
                        if (group == null){
                            sendMessage.sendHTMLMessage(userId, GROUP_NOT_FOUND_MESSAGE);
                            return;
                        }

                        if (zdt.getDayOfWeek().equals(DayOfWeek.SUNDAY) || (zdt.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !ChatConfig.isSaturdayLesson) ||
                                zdt.isBefore(ZonedDateTime.of(ChatConfig.startSemester, ZoneId.of("Europe/Kiev"))) ||
                                zdt.isAfter(ZonedDateTime.of(ChatConfig.endSemester, ZoneId.of("Europe/Kiev")))) {

                            sendMessage.sendHTMLMessage(userId, SCHEDULE_NOT_FOUND_MESSAGE);

                        } else {
                            String formatMsg = ScheduleController.getMessageForStartOfNewDay(group, zdt);
                            sendMessage.sendHTMLMessage(userId, formatMsg);
                        }

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
            sendMessage.sendHTMLMessage(userId, NO.getCommandName());
        }
    }
}