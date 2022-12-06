package com.midel.schedulebott.command;

import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.group.Group;
import com.midel.schedulebott.group.GroupController;
import com.midel.schedulebott.schedule.ScheduleController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
            + "`/розклад БІ\\-244Б\\(А\\) 01\\.10`";

    // ToDo добавити можливість вказувати лише дату, а групу вибирати для юзера в окремому меню і закріплювати
    public GetScheduleUserCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();

        if (arguments != null) {
            if (arguments.size() == 0) {
                sendMessage.sendMarkupV2Message(userId, INFO_MESSAGE);
            } else if (arguments.size() != 2){
                sendMessage.sendMarkupV2Message(userId, INVALID_ARGUMENT_MESSAGE);
            } else{
                String[] date;
                int day, month;
                ZonedDateTime zdt = ZonedDateTime.now();
                Group group;
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
            }
        } else {
            sendMessage.sendHTMLMessage(userId, NO.getCommandName());
        }
    }
}