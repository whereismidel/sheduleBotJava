package com.midel.schedulebott.TelegramBot;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatDescription;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;

public class SendMessage extends ScheduleBotChannel {

    static final Logger logger = LoggerFactory.getLogger(SendMessage.class);

    /**
     * Sending a "text" message to the user
     */
    public int sendTextMessage(String chatId, String text) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            return execute(message).getMessageId(); // Отправка
        } catch (TelegramApiException e) {
            logger.error("Failed to send TEXT message.", e);
            return -1;
        }
    }

    /**
     * buttons - args is a just String as title or button as object.
     * example: sendInlineMessage(update, "Text", new Object[][]{{new Pair<>("Отменить заказ", "CancelOrder#"}});
     * example: sendInlineMessage(update, "Text", new Object[][]{{new InlineKeyboardButton()});
     */
    public int sendInlineMessages(String chatId, String messageText, Object[][] buttons) {

        org.telegram.telegrambots.meta.api.methods.send.SendMessage message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (Object[] rows : buttons) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (Object button : rows) {
                if (button != null) {
                    if (button.getClass() == Pair.class) {
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        Pair<?, ?> pair = (Pair<?, ?>) button;
                        inlineKeyboardButton.setText(String.valueOf(pair.getValue0()));
                        inlineKeyboardButton.setCallbackData(String.valueOf(pair.getValue1()));

                        keyboardButtonsRow.add(inlineKeyboardButton);
                    } else if (button.getClass() == InlineKeyboardButton.class) {
                        keyboardButtonsRow.add((InlineKeyboardButton) button);
                    }
                }
            }
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);

        message.setText(messageText);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            return execute(message).getMessageId();
        } catch (TelegramApiException e) {
            logger.error("Failed to send INLINE message. ChatID = {} Buttons = {}", chatId, buttons, e);
        }
        return -1;
    }

    /**
     * Sending a "text" message with html markup to the user
     */
    public int sendHTMLMessage(String chatId, String text) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        message.enableHtml(true);
        message.disableWebPagePreview();
        try {
            return execute(message).getMessageId(); // Надсилання
        } catch (TelegramApiException e) {
            logger.error("Failed to send HTML message. ChatID = {}", chatId, e);
            return -1;
        }
    }

    public void sendMarkupV2Message(String chatId, String text) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        message.enableMarkdownV2(true);
        message.disableWebPagePreview();
        try {
            execute(message); // Надсилання
        } catch (TelegramApiException e) {
            logger.error("Failed to send MARKUP message. ChatID = {}", chatId, e);
        }
    }

    public void changeDescription(String chatId, String description) {
        SetChatDescription setDescription = new SetChatDescription();

        setDescription.setChatId(chatId);
        setDescription.setDescription(description);

        try {
            execute(setDescription); // Надсилання
        } catch (TelegramApiException e) {
            logger.warn("Failed to change channel description. (Description is the same as above). ChatID = {}", chatId);
        }
    }

    public void deleteMessage(String chatId, int messageId){
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);

        try {
            execute(deleteMessage); // Видалення
        } catch (TelegramApiRequestException re){
            logger.warn("Failed to delete message. chatId = {}, {}", chatId, re.getMessage());
        } catch (TelegramApiException e) {
            logger.warn("Failed to delete message. Unknown reason", e);
        }
    }

}
