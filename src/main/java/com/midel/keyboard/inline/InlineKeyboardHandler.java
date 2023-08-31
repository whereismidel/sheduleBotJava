package com.midel.keyboard.inline;

import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

/**
 * InlineKeyboardHandler abstract class for handling telegram-bot inline query answer.
 */
public abstract class InlineKeyboardHandler {


    public final SendMessage sendMessage;
    protected String callbackData;
    public InlineKeyboardHandler(SendMessage sendMessage){
        this.sendMessage = sendMessage;
    }

    /**
     * Main method, which is executing command logic.
     *
     * @param update provided {@link Update} object with all the needed data for command.
     */
    public abstract void execute(Update update);

    public void execute(Update update, String callbackData, String chatId) {
        if (update == null){
            update = new Update();
        }

        if (update.getCallbackQuery() == null){
            update.setCallbackQuery(new CallbackQuery());
        }

        if (update.getCallbackQuery().getMessage() == null){
            if (update.hasMessage()){
                update.getCallbackQuery().setMessage(update.getMessage());
            } else {
                update.getCallbackQuery().setMessage(new Message());
            }
        }

        if (update.getCallbackQuery().getFrom() == null) {
            if (update.getMessage().getFrom() != null){
                update.getCallbackQuery().setFrom(update.getMessage().getFrom());
            } else {
                update.getCallbackQuery().setFrom(new User());
            }
        }

        if (update.getCallbackQuery().getMessage().getChat() == null){
            update.getCallbackQuery().getMessage().setChat(new Chat());
        }

        this.callbackData = callbackData;
        update.getCallbackQuery().getMessage().getChat().setId(Long.parseLong(chatId));

        execute(update);
    }

    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
    }
}
