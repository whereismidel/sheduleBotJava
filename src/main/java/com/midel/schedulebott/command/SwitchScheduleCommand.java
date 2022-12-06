package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.AdminCommand;
import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * SwitchSchedule {@link Command}.
 */
@AdminCommand
public class SwitchScheduleCommand extends Command {

    public final static String SWITCH_MESSAGE_ON = "Розсилка розкладу <b>включена</b>.";
    public final static String SWITCH_MESSAGE_OFF = "Розсилка розкладу <b>виключена</b>.";

    public SwitchScheduleCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChat().getId().toString();

        if(ChatConfig.debug){
            ChatConfig.sendSchedule = false;
            sendMessage.sendHTMLMessage(userId, SWITCH_MESSAGE_OFF);
        } else {
            ChatConfig.sendSchedule = true;
            sendMessage.sendHTMLMessage(userId, SWITCH_MESSAGE_ON);
        }
    }
}
