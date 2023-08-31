package com.midel.command;

import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
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

    public void execute(Update update, String chatId) {
        if (update == null){
            update = new Update();
        }

        if (update.getMessage() == null){
            update.setMessage(new Message());
        }

        if (update.getMessage().getChat() == null){
            update.getMessage().setChat(new Chat());
        }

        update.getMessage().getChat().setId(Long.parseLong(chatId));

        execute(update);
    }
}
