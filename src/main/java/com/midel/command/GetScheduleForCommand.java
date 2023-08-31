package com.midel.command;

import com.midel.command.annotation.AdminCommand;
import com.midel.exceptions.MissingMessageException;
import com.midel.group.Group;
import com.midel.group.GroupController;
import com.midel.schedule.ScheduleController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.midel.command.CommandName.NO;
import static com.midel.config.ChatConfig.debug;

/**
 * GetScheduleFor {@link Command}.
 */
@AdminCommand
public class GetScheduleForCommand extends Command{

    public final static String INVALID_ARGUMENT_MESSAGE = "Невірно вказані аргументи або їх кількість або виключений режим відкладки. \n"
            +"/scheduleFor - для довідки.";
    public final static String INFO_MESSAGE = "`/scheduleFor` `назва групи` `день` \\- відобразити розклад сьогодні\\+`день`\\.";

    public GetScheduleForCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChat().getId().toString();


        if (arguments != null) {
            if (arguments.size() == 0) {
                    sendMessage.sendMarkupV2Message(userId, INFO_MESSAGE);
            } else if (arguments.size() != 2 || !debug){
                sendMessage.sendHTMLMessage(userId, INVALID_ARGUMENT_MESSAGE);
            } else{
                Group group = GroupController.getGroupByName(arguments.get(0));
                if (group != null) {
                    ZonedDateTime currentZonedDate = ZonedDateTime.now(ZoneId.of("Europe/Kiev")).plusDays(Integer.parseInt(arguments.get(1)));
                    try {
                        String formatMsg = ScheduleController.getMessageForStartOfNewDay(group, currentZonedDate, false, false);
                        sendMessage.sendHTMLMessage(userId, formatMsg);
                    } catch (MissingMessageException e) {
                        System.out.println(e);
                    }
                }else {
                    sendMessage.sendHTMLMessage(userId, INVALID_ARGUMENT_MESSAGE);
                }
            }
        } else {
            sendMessage.sendHTMLMessage(userId, NO.getCommandName());
        }
    }
}
