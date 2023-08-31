package com.midel.command;

import com.midel.command.annotation.GroupCommand;
import com.midel.command.annotation.UserCommand;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Unknown {@link Command}.
 */
@UserCommand
@GroupCommand
public class UnknownCommand extends Command {

    public static final String UNKNOWN_MESSAGE = "Не розумію тебе \uD83D\uDE1F, спробуй /help або /start";

    public UnknownCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        sendMessage.sendHTMLMessage(update.getMessage().getChatId().toString(), UNKNOWN_MESSAGE);
    }
}