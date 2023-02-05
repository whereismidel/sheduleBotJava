package com.midel.schedulebott.keyboard.inline;

import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ChangeGroupInlineHandler extends InlineKeyboardHandler{
    public static final String SPECIFY_GROUP_MESSAGE = "<b>Вкажіть групу</b>\n"
            +"Щоб змінити групу, вкажи її:\n"
            +"<i>Наприклад:</i>\n"
            +"БІ-144Б\n"
            +"СЗ-312Б(А)\n\n";
    public ChangeGroupInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getCallbackQuery().getMessage().getChatId().toString();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        // Передається керування до CreateAndSetGroupNameReplyMessage
        sendMessage.replyMessage(userId, SPECIFY_GROUP_MESSAGE);
        sendMessage.deleteMessage(userId, messageId);
    }
}
