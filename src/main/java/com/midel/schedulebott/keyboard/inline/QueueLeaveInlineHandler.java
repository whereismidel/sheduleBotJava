package com.midel.schedulebott.keyboard.inline;

import com.midel.schedulebott.queue.Queue;
import com.midel.schedulebott.queue.QueueController;
import com.midel.schedulebott.queue.QueueRepo;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class QueueLeaveInlineHandler extends InlineKeyboardHandler {

    public static final String ERROR_MESSAGE = "Невідома черга, можливо вона застаріла, або ти намагаєшся прийняти в ній участь з іншого чата";
    public static final String ERROR_LEAVE_MESSAGE = "Не вдалось вилучити з черги <a href=\"tg://user?id=%s\">%s</a>\nМожлива причина: такого користувача немає в черзі";


    public QueueLeaveInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        User from = update.getCallbackQuery().getFrom();

        Student group = StudentController.getStudentById(chatId);
        Queue queue = QueueController.getQueueById(chatId + "/" + messageId);
        if (queue != null && group != null){
            String userName = from.getFirstName() + (from.getLastName() != null? " "+from.getLastName():"");

            if (QueueController.removeUserFromQueue(queue, from.getId().toString()).getValue0()){
                if (!sendMessage.editMessage(
                        chatId,
                        messageId,
                        queue.toFormatString(),
                        update.getCallbackQuery().getMessage().getReplyMarkup()
                )
                ){
                    queue.setMessageId(
                            sendMessage.sendInlineKeyboard(
                                    chatId,
                                    queue.toFormatString(),
                                    new Object[][]{
                                            {InlineKeyboardAnswer.JOIN_TO_QUEUE},
                                            {InlineKeyboardAnswer.GO_DOWN_QUEUE},
                                            {InlineKeyboardAnswer.LEAVE_FROM_QUEUE}
                                    },
                                    null
                            )+""
                    );
                    //sendMessage.deleteMessage(chatId, messageId);
                }
                if (!queue.getMessageId().equals(-1+"")) {
                    queue.setQueueId(queue.getChatId() + "/" + queue.getMessageId());

                    if (!QueueRepo.exportQueuesList()) {
                        queue.getUserQueue().remove(queue.getUserQueue().size());
                        sendMessage.sendHTMLMessage(chatId, String.format(ERROR_LEAVE_MESSAGE, from.getId(), userName));
                    }
                }
            } else {
                sendMessage.sendHTMLMessage(chatId, String.format(ERROR_LEAVE_MESSAGE, from.getId(), userName));
            }
        } else {
            sendMessage.sendHTMLMessage(chatId, ERROR_MESSAGE);
        }
    }
}
