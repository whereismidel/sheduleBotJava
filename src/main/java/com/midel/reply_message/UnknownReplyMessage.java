package com.midel.reply_message;

import com.midel.command.UnknownCommand;
import com.midel.telegram.SendMessage;
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
