package com.midel.keyboard.inline;

import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DeleteMessageInlineHandler extends InlineKeyboardHandler {
    public DeleteMessageInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String userId = message.getChatId().toString();
        int messageId = message.getMessageId();

        sendMessage.deleteMessage(userId, messageId);
    }
}
