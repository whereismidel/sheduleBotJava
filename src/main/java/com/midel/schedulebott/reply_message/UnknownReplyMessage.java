package com.midel.schedulebott.reply_message;

import com.midel.schedulebott.command.UnknownCommand;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnknownReplyMessage extends ReplyMessage {
    public UnknownReplyMessage(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        new UnknownCommand(sendMessage).execute(update);
    }
}
