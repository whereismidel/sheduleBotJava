package com.midel.schedulebott.command;

import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Command abstract class for handling telegram-bot commands.
 */
public abstract class Command {

    public List<String> arguments = null;
    public final SendMessage sendMessage;
    public Command(SendMessage sendMessage){
        this.sendMessage = sendMessage;
    }
    /**
     * Main method, which is executing command logic.
     *
     * @param update provided {@link Update} object with all the needed data for command.
     */
    public abstract void execute(Update update);
}
