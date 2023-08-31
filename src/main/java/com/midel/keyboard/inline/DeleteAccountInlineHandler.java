package com.midel.keyboard.inline;

import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.student.StudentRepo;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DeleteAccountInlineHandler extends InlineKeyboardHandler {

    public static final String SUCCESSFUL_DELETE_MESSAGE = "Твій акаунт успішно видалено. Зареєструйся заново. /start";
    public static final String FAILED_DELETE_MESSAGE = "Сталася помилка, твій акаунт не видалено. Спробуй ще раз.";
    public DeleteAccountInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String userId = message.getChatId().toString();
        int messageId = message.getMessageId();

        Student student = StudentController.getStudentById(userId);

        if (student != null && StudentRepo.deleteStudentFromList(student)) {
            sendMessage.sendHTMLMessage(userId, SUCCESSFUL_DELETE_MESSAGE);
        } else {
            sendMessage.sendHTMLMessage(userId, FAILED_DELETE_MESSAGE);
        }

        sendMessage.deleteMessage(userId, messageId);
    }
}
