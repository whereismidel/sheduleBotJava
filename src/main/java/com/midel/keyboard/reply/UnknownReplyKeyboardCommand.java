package com.midel.keyboard.reply;

import com.midel.command.UnknownCommand;
import com.midel.telegram.SendMessage;
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
