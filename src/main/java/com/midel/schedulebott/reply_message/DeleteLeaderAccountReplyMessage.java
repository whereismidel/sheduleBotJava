package com.midel.schedulebott.reply_message;

import com.midel.schedulebott.keyboard.inline.InlineKeyboardAnswer;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DeleteLeaderAccountReplyMessage extends ReplyMessage {
    public static final String WARNING_MESSAGE = "Ти точно бажаєш видалити свій аккаунт?\n"
            +"Останній раз питаю \uD83D\uDE0C";

    public static final String FAILED_MESSAGE = "Неправильно введений код.\n"
            +"Аккаунт видалено не буде \uD83D\uDE0C";

    public DeleteLeaderAccountReplyMessage(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String text = update.getMessage().getText();
        String userId = update.getMessage().getChatId().toString();
        int messageId = update.getMessage().getReplyToMessage().getMessageId();

        if (text.equals(userId)) {
            sendMessage.sendInlineKeyboard(userId,
                    WARNING_MESSAGE,
                    new Object[][]{
                            {InlineKeyboardAnswer.DELETE_ACCOUNT_YES},
                            {InlineKeyboardAnswer.DELETE_ACCOUNT_NO}
                    },
                    null);
        } else {
            sendMessage.sendHTMLMessage(userId, FAILED_MESSAGE);
        }

        sendMessage.deleteMessage(userId, messageId);
    }
}
