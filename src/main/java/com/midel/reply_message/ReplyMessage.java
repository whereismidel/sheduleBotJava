package com.midel.reply_message;

import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * ReplyMessage abstract class for handling telegram-bot reply message.
 */
public abstract class ReplyMessage {

        public final SendMessage sendMessage;
        public ReplyMessage(SendMessage sendMessage){
                this.sendMessage = sendMessage;
        }

        /**
         * Main method, which is executing command logic.
         *
         * @param update provided {@link Update} object with all the needed data for command.
         */
        public abstract void execute(Update update);
}
