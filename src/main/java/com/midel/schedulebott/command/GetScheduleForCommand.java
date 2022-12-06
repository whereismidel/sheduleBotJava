package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.AdminCommand;
import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.schedule.ScheduledTask;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static com.midel.schedulebott.command.CommandName.GET_SCHEDULE_FOR;
import static com.midel.schedulebott.command.CommandName.NO;
import static com.midel.schedulebott.config.ChatConfig.debug;

/**
 * GetScheduleFor {@link Command}.
 */
@AdminCommand
public class GetScheduleForCommand extends Command{

    public final static String INVALID_ARGUMENT_MESSAGE = "Невірно вказані аргументи або їх кількість або виключений режим відкладки. \n"
            +"/scheduleFor - для довідки.";
    public final static String INFO_MESSAGE = "`/scheduleFor` `день` \\- відобразити розклад сьогодні\\+`день`\\.";

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
                ChatConfig.debugArray = new ArrayList<>();
                ChatConfig.debugArray.add(GET_SCHEDULE_FOR);

                ChatConfig.debugArray.add(arguments.get(0).toUpperCase()); // назва групи
                ChatConfig.debugArray.add(arguments.get(1).toLowerCase()); // день(число)

                new ScheduledTask().updateAndStartOfNewDay();
            }
        } else {
            sendMessage.sendHTMLMessage(userId, NO.getCommandName());
        }
    }
}
