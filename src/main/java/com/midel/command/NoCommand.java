package com.midel.command;

import com.midel.command.annotation.UserCommand;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * No {@link Command}.
 */
@UserCommand
public class NoCommand extends Command {

    public static final String NO_MESSAGE = "В цьому житті я вмію виконувати лише команди\uD83D\uDE2D\uD83D\uDE2D\uD83D\uDE2D"
                                            +"\nСпробуй /help";

    public NoCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        sendMessage.sendHTMLMessage(update.getMessage().getChatId().toString(), NO_MESSAGE);
    }
}