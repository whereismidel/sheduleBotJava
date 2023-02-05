package com.midel.schedulebott.keyboard.inline;

import com.midel.schedulebott.command.MenuCommand;
import com.midel.schedulebott.command.StartCommand;
import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.student.StudentRepo;
import com.midel.schedulebott.telegram.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Request from {@link StartCommand}
 */
public class CreateStudentInlineHandler extends InlineKeyboardHandler {

    private static final Logger logger = LoggerFactory.getLogger(CreateStudentInlineHandler.class);

    public static final String ADVICE_MESSAGE = "<b>Вітаю, ти зареєструвався.</b>\n\n"
            +"Для перегляду доступних команд обери в меню пункт <u>\"Доступні команди\"</u>.\n"
            +"Там ти зможеш вказати свою групу\n";

    public static final String WARNING_MESSAGE = "Ти вже зареєструвався як студент.\n" +
            "/restart для повторної реєстрації";

    public static final String ERROR_MESSAGE = "Під час реєстрації сталась помилка, спробуй ще раз <pre>/restart</pre> або звернись до " + ChatConfig.creatorUsername;

    public CreateStudentInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String userId = message.getChatId().toString();
        String username = update.getCallbackQuery().getFrom().getUserName();

        int messageId = message.getMessageId();
        sendMessage.deleteMessage(userId, messageId);

        Student student = StudentController.getStudentById(userId);
        boolean successStudentRegistration;

        if (student == null) {
            student = new Student(userId, false);
            successStudentRegistration = StudentRepo.addStudentToList(student);
        } else {
            if (student.isLeader() == null){
                student.setLeader(false);
                successStudentRegistration = StudentRepo.exportStudentList();
            } else {
                sendMessage.sendHTMLMessage(userId, WARNING_MESSAGE);
                return;
            }
        }

        if (successStudentRegistration){
            logger.info("New student registered. StudentId = {}, Username = @{}", userId, username);
            new MenuCommand(sendMessage, ADVICE_MESSAGE).execute(update, userId);
        } else {
            student.setLeader(null);
            logger.warn("Unsuccessful registration of a new student. StudentId = {}, Username = @{}", userId, username);
            new MenuCommand(sendMessage, ERROR_MESSAGE).execute(update, userId);
        }
    }
}