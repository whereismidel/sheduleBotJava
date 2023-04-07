package com.midel.schedulebott.keyboard.inline;

import com.midel.schedulebott.queue.Queue;
import com.midel.schedulebott.queue.QueueController;
import com.midel.schedulebott.queue.QueueRepo;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class QueueGoDownInlineHandler extends InlineKeyboardHandler {
    public static final String ERROR_MESSAGE = "Невідома черга, можливо вона застаріла, або ти намагаєшся прийняти в ній участь з іншого чата";
    public static final String ERROR_CHANGE_MESSAGE = "Не вдалось змінити позицію для <a href=\"tg://user?id=%s\">%s</a>";
    public QueueGoDownInlineHandler(SendMessage sendMessage) {
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
            Pair<String, String> user = new Pair<>(userName, from.getId().toString());

            if (queue.getUserQueue() == null || queue.getUserQueue().isEmpty() || !queue.getUserQueue().get(queue.getUserQueue().size()).equals(user)){
                Integer pos = QueueController.getUserPos(queue, user.getValue1());

                boolean userIsAdd = false;
                if (pos != -1){
                    if (pos < queue.getUserQueue().size()){
                        userIsAdd = QueueController.swapUserPos(queue, pos, pos+1);
                    }
                } else {
                    userIsAdd = QueueController.addUserToQueue(queue, user.getValue0(), user.getValue1(), 0);
                }

                if (userIsAdd) {
                    if (!sendMessage.editMessage(
                            chatId,
                            messageId,
                            queue.toFormatString(),
                            update.getCallbackQuery().getMessage().getReplyMarkup()
                        )
                    ) {
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
                                ) + ""
                        );
                    }
                    if (!queue.getMessageId().equals(-1 + "")) {
                        queue.setQueueId(queue.getChatId() + "/" + queue.getMessageId());

                        if (!QueueRepo.exportQueuesList()) {
                            if (pos < queue.getUserQueue().size()){
                                QueueController.swapUserPos(queue, pos+1, pos);
                            }
                            sendMessage.sendHTMLMessage(chatId, String.format(ERROR_CHANGE_MESSAGE, from.getId(), userName));
                        }
                    }
                }
            }
        } else {
            sendMessage.sendHTMLMessage(chatId, ERROR_MESSAGE);
        }
    }
}
