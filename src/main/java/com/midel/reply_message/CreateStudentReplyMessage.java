package com.midel.reply_message;

import com.midel.keyboard.inline.CreateStudentInlineHandler;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CreateStudentReplyMessage extends ReplyMessage {
    public CreateStudentReplyMessage(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();
        String groupIdFromMessage = update.getMessage().getText();
        int messageId = update.getMessage().getReplyToMessage().getMessageId();

        Student student = StudentController.getStudentById(userId);

        if (student == null || student.getGroup() == null) {
            new CreateStudentInlineHandler(sendMessage).execute(update, groupIdFromMessage, userId);
        }

        sendMessage.deleteMessage(userId, messageId);

    }
}
