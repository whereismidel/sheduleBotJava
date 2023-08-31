package com.midel.telegram;

import com.midel.BotInitialization;
import com.midel.keyboard.inline.InlineKeyboardAnswer;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatDescription;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;

public class SendMessage {

    static final Logger logger = LoggerFactory.getLogger(SendMessage.class);
    private static ScheduleBotChannel scheduleBot;

    // ToDo Подумати, якщо відвалиться з'єднання з тг
    public SendMessage(){
        if (scheduleBot==null){
            scheduleBot = BotInitialization.scheduleBot;
        }
    }

    /**
     * <h3>Send message without markup via telegram bot.</h3>
     *
     * @param chatId provided chatId in which messages would be sent.
     * @param text provided message to be sent.
     */
    public void sendTextMessage(String chatId, String text) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            scheduleBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Failed to send TEXT message.\nChatId = {} Message = {}", chatId, text, e);
        }
    }

    // split large messages into several smaller ones
    public void sendLargeTextMessage(String chatId, ArrayList<String> text, String startMessageFrom){
        StringBuilder toSend = new StringBuilder(startMessageFrom);

        for(int i = 0; i < text.size(); i++){
            while (i < text.size() && toSend.length() < 3500){
                if (text.get(i).length() + toSend.length() > 3500) {
                    toSend.append("*Повідомлення для відправки було надто великим*\n");
                } else {
                    toSend.append(text.get(i)).append("\n");
                }
                i++;
            }
            new SendMessage().sendTextMessage(chatId, toSend.toString());

            toSend = new StringBuilder();
        }
    }

    /**
     * <h3>Send message with HTML markup via telegram bot.</h3>
     *
     * @param chatId provided chatId in which messages would be sent.
     * @param text provided message to be sent.
     */
    public int sendHTMLMessage(String chatId, String text) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        sendMessage.enableHtml(true);
        sendMessage.disableWebPagePreview();

        try {
            return scheduleBot.execute(sendMessage).getMessageId(); // Надсилання
        } catch (TelegramApiException e) {
            logger.error("Failed to send HTML message. ChatID = {}", chatId, e);
            return -1;
        }
    }

    /**
     * <h3>Send message with inline button and HTML markup via telegram bot.</h3>
     * <p>Example: sendInlineMessage(chatId, "Text", new Object[][]{{new Pair<>("Старт", "StartEvent"}});</p>
     * <p>Example: sendInlineMessage(chatId, "Text", new Object[][]{{new InlineKeyboardButton()});</p>
     *
     * @param chatId provided chatId in which messages would be sent.
     * @param messageText provided message to be sent.
     * @param inlineKeyboardMarkup provided inline buttons to be attached to the message.
     *
     */
    public long sendInlineKeyboard(String chatId, String messageText, InlineKeyboardMarkup inlineKeyboardMarkup) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        sendMessage.setChatId(chatId);

        sendMessage.enableHtml(true);
        sendMessage.disableWebPagePreview();

        sendMessage.setText(messageText);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            return scheduleBot.execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            logger.error("Failed to send INLINE message. ChatID = {} Buttons = {}", chatId, e);
            return -1;
        }
    }
    public long sendInlineKeyboard(String chatId, String messageText, Object[][] buttons, String userData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(buttons, userData);

        return sendInlineKeyboard(chatId, messageText, inlineKeyboardMarkup);
    }
    /**
     * <h3>Send message with keyboard and HTML markup via telegram bot.</h3>
     * <p>Example: sendClientKeyboard(chatId, "Text", new Object[][]{{new Pair<>("Старт", "StartEvent"}});</p>
     * <p>Example: sendClientKeyboard(chatId, "Text", new Object[][]{{new InlineKeyboardButton()});</p>
     *
     * @param chatId provided chatId in which keyboard would be sent.
     * @param text provided message to be sent with keyboard.
     * @param buttonAndPosition provided array of buttons with positioning.
     * @param oneTimeKeyboard sets the one time keyboard flag
     */
    public void sendClientKeyboard(String chatId, String text, Object[][] buttonAndPosition, boolean oneTimeKeyboard) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(oneTimeKeyboard);

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();

        for (Object[] buttonRow : buttonAndPosition) {
            KeyboardRow row = new KeyboardRow();
            for (Object button : buttonRow) {
                if (button.getClass() == String.class) {
                    row.add((String) button);
                } else if (button.getClass() == KeyboardButton.class) {
                    row.add((KeyboardButton) button);
                }
            }
            keyboard.add(row);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);


        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();

        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableHtml(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            scheduleBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Failed to send client keyboard. ChatID = {}", chatId, e);
        }
    }

    /**
     * <h3>Send message with V2 markup via telegram bot.</h3>
     *
     * @param chatId provided chatId in which messages would be sent.
     * @param text provided message to be sent.
     */
    public void sendMarkupV2Message(String chatId, String text) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        sendMessage.enableMarkdownV2(true);
        sendMessage.disableWebPagePreview();

        try {
            scheduleBot.execute(sendMessage); // Надсилання
        } catch (TelegramApiException e) {
            logger.error("Failed to send MARKUP message. ChatID = {}", chatId, e);
        }
    }

    public void sendTextWithEntitiesMessage(String chatId, String text, List<MessageEntity> messageEntities){
        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setEntities(messageEntities);

        sendMessage.disableWebPagePreview();

        try {
            scheduleBot.execute(sendMessage); // Надсилання
        } catch (TelegramApiException e) {
            logger.error("Failed to send message with entities. ChatID = {}", chatId, e);
        }
    }

    /**
     * <h3>Change description in chat via telegram bot.</h3>
     *
     * @param chatId provided chatId in which description would be change.
     * @param description provided description to be change.
     */
    public void changeDescription(String chatId, String description) {
        try {

            SetChatDescription setDescription = new SetChatDescription();

            setDescription.setChatId(chatId);
            setDescription.setDescription(description);

            scheduleBot.execute(setDescription); // Надсилання
        } catch (Exception e) {
            logger.warn("Failed to change channel description. (Description is the same as above). ChatID = {}", chatId);
        }
    }

    /**
     * <h3>Delete message in chat via telegram bot.</h3>
     *
     * @param chatId provided chatId in which messages would be deleted.
     * @param messageId provided message id to be delete.
     */
    public boolean deleteMessage(String chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);

        try {
            return scheduleBot.execute(deleteMessage); // Видалення
        } catch (TelegramApiRequestException re){
            logger.warn("Failed to delete message. chatId = {}, {}", chatId, re.getMessage());
        } catch (TelegramApiException e) {
            logger.warn("Failed to delete message. Unknown reason", e);
        }
        return false;
    }

    /**
     * <h3>Send message with HTML markup via telegram bot and reply to message.</h3>
     *
     * @param chatId provided chatId in which messages would be sent.
     * @param text provided message to be sent.
     */
    public int replyMessage(String chatId, String text) {

        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        ForceReplyKeyboard force = new ForceReplyKeyboard();
        force.setForceReply(true);
        //force.setSelective(true);
        sendMessage.setReplyMarkup(force);

        sendMessage.enableHtml(true);
        sendMessage.disableWebPagePreview();

        try {
            return scheduleBot.execute(sendMessage).getMessageId(); // Надсилання
        } catch (TelegramApiException e) {
            logger.error("Failed to send reply message. ChatID = {}", chatId, e);
            return -1;
        }
    }

    public boolean editMessage(String chatId, int messageId, String newText, InlineKeyboardMarkup inlineKeyboardMarkup){
        EditMessageText editMessageText = new EditMessageText();

        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
        editMessageText.setParseMode("HTML");

        if (inlineKeyboardMarkup != null){
            editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        }

        try {
            scheduleBot.execute(editMessageText);
            return true;
        } catch (TelegramApiException e) {
            return false;
        }
    }

    public static InlineKeyboardMarkup getInlineKeyboardMarkup(Object[][] buttons, String userData){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        userData = userData != null?"#"+userData:"";
        for (Object[] rows : buttons) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (Object button : rows) {
                if (button != null) {
                    if (button.getClass() == Pair.class) {
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        Pair<?, ?> pair = (Pair<?, ?>) button;
                        inlineKeyboardButton.setText(String.valueOf(pair.getValue0()));
                        inlineKeyboardButton.setCallbackData(pair.getValue1() + userData);

                        keyboardButtonsRow.add(inlineKeyboardButton);
                    } else if (button.getClass() == InlineKeyboardButton.class) {
                        keyboardButtonsRow.add((InlineKeyboardButton) button);
                    } else if (button.getClass() == InlineKeyboardAnswer.class) {
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(((InlineKeyboardAnswer) button).getCallbackText());
                        inlineKeyboardButton.setCallbackData(((InlineKeyboardAnswer) button).getCallbackData() + userData);

                        keyboardButtonsRow.add(inlineKeyboardButton);
                    }
                }
            }
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

}
