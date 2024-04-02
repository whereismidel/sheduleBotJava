package com.midel.keyboard.inline;

import com.midel.command.MenuCommand;
import com.midel.command.StartCommand;
import com.midel.config.ChatConfig;
import com.midel.group.Group;
import com.midel.group.GroupController;
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
public class CreateStudentInlineHandler extends InlineKeyboardHandler {

    private static final Logger logger = LoggerFactory.getLogger(CreateStudentInlineHandler.class);

    public static final String ADVICE_MESSAGE = "<b>Вітаю, ти зареєструвався(-лась) як студент групи <u>%s</u>.</b>\n\n"
            +"Для перегляду доступних команд обери в меню пункт <u>\"Доступні команди\"</u>.\n";

    public static final String WARNING_MESSAGE = "Ти вже зареєструвався(-лась) як студент.\n" +
            "/restart для повторної реєстрації";

    public static final String NEED_GROUP_LINK_MESSAGE = "<b>Група не вказана</b>\n" +
            "Для того щоб користуватись ботом і переглядати розклад своєї групи запитай у старости код для приєднання, або у відповідь до цього повідомлення надішли ідентифікатор групи.";

    public static final String ERROR_MESSAGE = "Під час реєстрації сталась помилка, спробуй ще раз <pre>/restart</pre> або звернись до " + ChatConfig.creatorUsername;
    public static final String GROUP_NOT_FOUND_MESSAGE = "<b>Група не вказана</b>\n" +
            "Групи з таким ідентифікатором не знайдено.";

    public CreateStudentInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String userId = message.getChatId().toString();
        String username = update.getCallbackQuery().getFrom().getUserName();

        sendMessage.deleteMessage(userId, message.getMessageId());

        Student student = StudentController.getStudentById(userId);

        if (callbackData == null) {
            if (student == null || student.getGroup() == null) {
                sendMessage.replyMessage(userId, NEED_GROUP_LINK_MESSAGE);
            }
            return;
        } else {

        }

        Group group = GroupController.getGroupByLeader(callbackData);

        if (group != null && username != null){

            boolean successStudentRegistration;

            if (student == null) {
                student = new Student(userId, false);
                student.setGroup(group);
                successStudentRegistration = StudentRepo.addStudentToList(student);
            } else {
                if (student.getGroup() == null && (student.isLeader() == null || !student.isLeader())){
                    student.setLeader(false);
                    student.setGroup(group);
                    successStudentRegistration = StudentRepo.exportStudentList();
                } else {
                    if (!student.isLeader()){
                        sendMessage.sendHTMLMessage(userId, WARNING_MESSAGE);
                    }
                    return;
                }
            }

            if (successStudentRegistration){
                logger.info("New student registered. StudentId = {}, Username = @{}, Group = {}", userId, username, group.getGroupName());
                new MenuCommand(sendMessage, String.format(ADVICE_MESSAGE,group.getGroupName().split("#")[0])).execute(update, userId);
            } else {
                student.setLeader(null);
                logger.warn("Unsuccessful registration of a new student. StudentId = {}, Username = @{}, Group = {}", userId, username, group.getGroupName());
                new MenuCommand(sendMessage, ERROR_MESSAGE).execute(update, userId);
            }

        } else {
            sendMessage.replyMessage(userId, GROUP_NOT_FOUND_MESSAGE);
        }
    }
}