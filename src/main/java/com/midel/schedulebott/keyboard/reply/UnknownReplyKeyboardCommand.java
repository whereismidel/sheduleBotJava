package com.midel.schedulebott.keyboard.reply;

import com.midel.schedulebott.command.UnknownCommand;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnknownReplyKeyboardCommand extends ReplyKeyboardCommand {
    public UnknownReplyKeyboardCommand(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        new UnknownCommand(sendMessage).execute(update);
    }
}
