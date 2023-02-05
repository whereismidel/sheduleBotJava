package com.midel.schedulebott.reply_message;

import com.midel.schedulebott.command.DeleteStudentCommand;
import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.student.StudentRepo;
import com.midel.schedulebott.telegram.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Request from {@link DeleteStudentCommand}
 */
public class DeleteStudentReplyMessage extends ReplyMessage {
    private static final Logger logger = LoggerFactory.getLogger(DeleteStudentReplyMessage.class);
    private static final String SUCCESSFUL_DELETE_MESSAGE = "Студент успішно видалений.";
    private static final String FAILED_DELETE_MESSAGE = "Не вдалось видалити студента. Можливо такого не існує.";
    public static final String CANCEL_DELETE_MESSAGE = "Не вірно введено ключове слово.";
    public DeleteStudentReplyMessage(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        String replyText = update.getMessage().getReplyToMessage().getText();

        if (ChatConfig.ADMINS.contains(userId)){

            Pattern pattern = Pattern.compile("(?<=напишіть )[0-9]+");
            Matcher matcher = pattern.matcher(replyText);

            if (matcher.find()) {
                if (replyText.substring(matcher.start(), matcher.end()).equals(text)){
                    Student student = StudentController.getStudentById(text);
                    if (student != null && StudentRepo.deleteStudentFromList(student)) {
                        sendMessage.sendHTMLMessage(userId, SUCCESSFUL_DELETE_MESSAGE);
                        logger.info("The student has been removed. studentId = {}", userId);
                    } else {
                        sendMessage.sendHTMLMessage(userId, FAILED_DELETE_MESSAGE);
                        logger.warn("Failed to delete student. studentId = {}", userId);
                    }
                } else {
                    sendMessage.sendHTMLMessage(userId, CANCEL_DELETE_MESSAGE);
                    sendMessage.deleteMessage(userId, update.getMessage().getReplyToMessage().getMessageId());
                }
            }
        }
    }
}
