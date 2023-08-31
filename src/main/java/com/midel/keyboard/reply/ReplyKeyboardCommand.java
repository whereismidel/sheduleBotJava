package com.midel.keyboard.reply;

import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * ReplyKeyboardCommand abstract class for handling telegram-bot reply keyboard button.
 */
public abstract class ReplyKeyboardCommand {

    public final SendMessage sendMessage;
    public ReplyKeyboardCommand(SendMessage sendMessage){
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
