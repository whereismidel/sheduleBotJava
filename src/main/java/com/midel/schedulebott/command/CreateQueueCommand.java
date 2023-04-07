package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.GroupCommand;
import com.midel.schedulebott.config.BotConfig;
import com.midel.schedulebott.keyboard.inline.InlineKeyboardAnswer;
import com.midel.schedulebott.queue.Queue;
import com.midel.schedulebott.queue.QueueController;
import com.midel.schedulebott.queue.QueueRepo;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.LinkedHashMap;

@GroupCommand
public class CreateQueueCommand extends Command {
    public final static String INFO_MESSAGE = "<code>/newQueue заголовок@" + BotConfig.BOT_USERNAME + "</code> - створити чергу для якоїсь події.";

    public final static String QUEUE_FORMAT_MESSAGE =
            "<b>%s</b>\n" +
            "\n" +
            "%s. <a href=\"tg://user?id=%s\">%s</a>\n";

    public static final String ERROR_MESSAGE = "Не вдалось створити чергу, шось пішло не так.. (можливо використані забоорнені символи)";
    public CreateQueueCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        User from = update.getMessage().getFrom();

        if (arguments != null) {
            if (arguments.size() == 0) {
                sendMessage.sendHTMLMessage(chatId, INFO_MESSAGE);
            } else {
                String userName = from.getFirstName() + (from.getLastName() != null? " "+from.getLastName():"");

                Queue queue = new Queue(chatId, null, String.join(" ", arguments), new LinkedHashMap<>(), true);
                QueueController.addUserToQueue(queue, userName, from.getId().toString(), 0);

                queue.setMessageId(
                        sendMessage.sendInlineKeyboard(
                                chatId,
                                String.format(
                                        QUEUE_FORMAT_MESSAGE,
                                        queue.getTitle(),
                                        1,
                                        queue.getUserQueue().get(1).getValue1(),
                                        queue.getUserQueue().get(1).getValue0()
                                ),
                                new Object[][]{
                                        {InlineKeyboardAnswer.JOIN_TO_QUEUE},
                                        {InlineKeyboardAnswer.GO_DOWN_QUEUE},
                                        {InlineKeyboardAnswer.LEAVE_FROM_QUEUE}
                                },
                                null
                        )+""
                );

                if (!queue.getMessageId().equals(-1+"")){
                    queue.setQueueId(queue.getChatId()+"/"+queue.getMessageId());

                    if (!QueueRepo.addQueueToList(queue)){
                        sendMessage.deleteMessage(chatId, Integer.parseInt(queue.getMessageId()));
                        sendMessage.sendTextMessage(chatId, ERROR_MESSAGE);
                    }
                } else {
                    sendMessage.sendTextMessage(chatId, ERROR_MESSAGE);
                }
            }
        } else {
            new NoCommand(sendMessage).execute(update);
        }
    }
}
