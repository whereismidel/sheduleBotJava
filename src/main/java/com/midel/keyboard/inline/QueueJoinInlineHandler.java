package com.midel.keyboard.inline;

import com.midel.queue.Queue;
import com.midel.queue.QueueController;
import com.midel.queue.QueueRepo;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class QueueJoinInlineHandler extends InlineKeyboardHandler {

    public static final String ERROR_MESSAGE = "Невідома черга, можливо вона застаріла, або ти намагаєшся прийняти в ній участь з іншого чата";
    public static final String ERROR_JOINING_MESSAGE = "Не вдалось додати до черги <a href=\"tg://user?id=%s\">%s</a>\nМожлива причина: такий користувач вже є в цій черзі";
    public QueueJoinInlineHandler(SendMessage sendMessage) {
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

            if (QueueController.addUserToQueue(
                    queue,
                    userName,
                    from.getId().toString(),
                    update.hasMessage()?Integer.parseInt(update.getMessage().getText()) : 0)
            ){
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
                }
                if (!queue.getMessageId().equals(-1+"")) {
                    queue.setQueueId(queue.getChatId() + "/" + queue.getMessageId());

                    if (!QueueRepo.exportQueuesList()) {
                        queue.getUserQueue().remove(queue.getUserQueue().size());
                        sendMessage.sendHTMLMessage(chatId, String.format(ERROR_JOINING_MESSAGE, from.getId(), userName));
                    }
                }
            } else {
                sendMessage.sendHTMLMessage(chatId, String.format(ERROR_JOINING_MESSAGE, from.getId(), userName));
            }
        } else {
            sendMessage.sendHTMLMessage(chatId, ERROR_MESSAGE);
        }
    }
}
