package com.midel.keyboard.inline;

import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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

    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
    }
}
