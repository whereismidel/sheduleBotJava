package com.midel.keyboard.inline;

import com.midel.command.MenuCommand;
import com.midel.command.StartCommand;
import com.midel.config.ChatConfig;
import com.midel.keyboard.reply.LeaderMenuReplyKeyboardCommand;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.student.StudentRepo;
import com.midel.telegram.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Request from {@link StartCommand}
 */
public class CreateLeaderInlineHandler extends InlineKeyboardHandler {

    private static final Logger logger = LoggerFactory.getLogger(CreateLeaderInlineHandler.class);

    public static final String ADVICE_MESSAGE = "<b>Вітаю, ти зареєструвався(-лась) як староста.</b>";
    public static final String WARNING_MESSAGE = "Ти вже зареєструвався(-лась) як староста.\n" +
                                                    "/restart для повторної реєстрації";

    public static final String ERROR_MESSAGE = "Під час реєстрації сталась помилка, спробуй ще раз <pre>/restart</pre> або звернись до " + ChatConfig.creatorUsername;

    public CreateLeaderInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String userId = message.getChatId().toString();
        String username = update.getCallbackQuery().getFrom().getUserName();

        int messageId = message.getMessageId();
        sendMessage.deleteMessage(userId, messageId);

        // ToDo Виправити валідацію
//        if (!ChatConfig.ADMINS.contains(userId)){
//            sendMessage.sendHTMLMessage(userId,"Можливість створення нових користувачів тимчасово обмежена \uD83D\uDE1E\nПерепрошую за незручності.");
//            return;
//        }

        Student student = StudentController.getStudentById(userId);
        boolean successLeaderRegistration;
        if (student == null) {
            student = new Student(userId, true);
            successLeaderRegistration = StudentRepo.addStudentToList(student);
        } else {
            if (student.isLeader() == null) {
                student.setLeader(true);
                successLeaderRegistration = StudentRepo.exportStudentList();
            } else {
                if (student.isLeader()) {
                    sendMessage.sendHTMLMessage(userId, WARNING_MESSAGE);
                }
                return;
            }
        }

        if (successLeaderRegistration){
            logger.info("New leader registered. LeaderId = {}, Username = @{}", userId, username);
            sendMessage.sendHTMLMessage(userId, ADVICE_MESSAGE);
            new LeaderMenuReplyKeyboardCommand(sendMessage).execute(update, userId);
        } else {
            student.setLeader(null);
            logger.warn("Unsuccessful registration of a new leader. LeaderId = {}, Username = @{}", userId, username);
            new MenuCommand(sendMessage, ERROR_MESSAGE).execute(update, userId);
        }
    }
}
