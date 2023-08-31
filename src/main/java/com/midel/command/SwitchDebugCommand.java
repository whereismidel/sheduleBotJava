package com.midel.command;

import com.midel.command.annotation.AdminCommand;
import com.midel.config.ChatConfig;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * SwitchDebug {@link Command}.
 */
@AdminCommand
public class SwitchDebugCommand extends Command{

    public final static String SWITCH_MESSAGE_ON = "Відладка бота <b>включена</b>.";
    public final static String SWITCH_MESSAGE_OFF = "Відладка бота <b>виключена</b>.";

    public SwitchDebugCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChat().getId().toString();

        if(ChatConfig.debug){
            ChatConfig.debug = false;
            sendMessage.sendHTMLMessage(userId, SWITCH_MESSAGE_OFF);
        } else {
            ChatConfig.debug = true;
            sendMessage.sendHTMLMessage(userId, SWITCH_MESSAGE_ON);
        }
    }
}