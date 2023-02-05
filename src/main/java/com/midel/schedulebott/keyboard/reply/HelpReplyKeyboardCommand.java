package com.midel.schedulebott.keyboard.reply;

import com.midel.schedulebott.command.HelpCommand;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpReplyKeyboardCommand extends ReplyKeyboardCommand{

    public HelpReplyKeyboardCommand(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        new HelpCommand(sendMessage).execute(update);
    }
}
