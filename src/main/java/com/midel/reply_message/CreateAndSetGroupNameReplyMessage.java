package com.midel.reply_message;

import com.midel.command.MenuCommand;
import com.midel.config.ChatConfig;
import com.midel.group.Group;
import com.midel.group.GroupController;
import com.midel.group.GroupRepo;
import com.midel.keyboard.reply.LeaderMenuReplyKeyboardCommand;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.student.StudentRepo;
import com.midel.telegram.SendMessage;
import com.midel.template.TemplateRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;

/**
 * Request from {@link LeaderMenuReplyKeyboardCommand}
 */
public class CreateAndSetGroupNameReplyMessage extends ReplyMessage {

    private static final Logger logger = LoggerFactory.getLogger(CreateAndSetGroupNameReplyMessage.class);
    public static final String GROUP_NOT_FOUND_MESSAGE = "<b>Вкажіть групу</b>\n"
            +"Групи з такою назвою не існує.\n"
            +"Можливо ти помилився при введені або твій староста ще не створив її.";

    public static final String GROUP_NOT_CORRECT_MESSAGE = "<b>Вкажіть групу</b>\n"
            +"Назва групи не повинна містити пробілів, а також будь-яких символів окрім: '-', '(', ')'.\n"
            +"Обмеження на довжину назви - 10 символів";
    public static final String GROUP_ALREADY_EXIST_MESSAGE = "Група з такою назвою вже існує.\n"
            +"Якщо сталась помилка, і саме ти староста цієї групи звернись до " + ChatConfig.creatorUsername;
    public static final String ALREADY_HAVE_GROUP_MESSAGE = "За тобою вже закріплена група.\n"
            +"Якщо сталась помилка спробуй <code>/restart</code> або звернись до " + ChatConfig.creatorUsername;
    public static final String SUCCESSFUL_CREATE_MESSAGE = "Група успішно створена.";
    public static final String SUCCESSFUL_INSERTED_MESSAGE = "Група успішно встановлена. \n"
            + "Тепер ти можеш користуватись командою <code>/sch</code> без вказання групи.";
    public static final String FAILED_CREATE_MESSAGE = "Під час створення сталась непередбачувана помилка.\n"
            +"Спробуй пізніше або звернись до " + ChatConfig.creatorUsername;
    public CreateAndSetGroupNameReplyMessage(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();
        int messageId = update.getMessage().getReplyToMessage().getMessageId();

        try {
            Student student = StudentController.getStudentById(userId);

            String groupName = update.getMessage().getText().toUpperCase();

            if (student != null) {
                if (student.isLeader()) {

                    Group group;

                    if (GroupController.getGroupByName(groupName) == null) {
                        if (GroupController.getGroupByLeader(userId) == null) {

                            if (groupName.length() > 10 || !groupName.matches("^[A-ZА-ЯІЇЄ\\-()0-9]*$")){
                                sendMessage.sendInlineKeyboard(
                                        userId,
                                        "Вкажіть групу",
                                        TemplateRepo.templates.keySet().stream()
                                                .map(e -> {
                                                    InlineKeyboardButton button = new InlineKeyboardButton(e);
                                                    button.setCallbackData(e);
                                                    return new Object[]{button};
                                                })
                                                .toArray(Object[][]::new),
                                        null
                                );
                                sendMessage.replyMessage(userId, GROUP_NOT_CORRECT_MESSAGE);
                                return;
                            }

                            group = new Group(groupName, userId, null, null, null, false, ChatConfig.startSemester, null);
                            if (GroupRepo.addGroupToList(group)) {
                                logger.info("Create a new group. groupName = {}, leaderId = {}", group.getGroupName(), group.getLeaderId());
                                group = GroupController.getGroupByLeader(userId);

                                student.setGroup(group);

                                logger.info("Added a new student to the group. groupName = {}, studentId = {}", student.getGroup().getGroupName(), student.getGroup().getLeaderId());
                                if (!StudentRepo.exportStudentList()){
                                    logger.warn("Failed to add new student to group. groupName = {}, studentId = {}", student.getGroup().getGroupName(), student.getGroup().getLeaderId());
                                    throw new Exception();
                                }

                                new MenuCommand(sendMessage, SUCCESSFUL_CREATE_MESSAGE).execute(update);

                                new LeaderMenuReplyKeyboardCommand(sendMessage).execute(update);
                            } else {
                                logger.warn("Failed to create a new group. groupName = {}, leaderId = {}", group.getGroupName(), group.getLeaderId());
                                throw new Exception();
                            }
                        } else {
                            new MenuCommand(sendMessage, ALREADY_HAVE_GROUP_MESSAGE).execute(update);
                        }
                    } else {
                        new MenuCommand(sendMessage, GROUP_ALREADY_EXIST_MESSAGE).execute(update);
                    }
                } else {
                    Group group = GroupController.getGroupByName(groupName);
                    if (group != null) {
                        student.setGroup(group);

                        logger.info("Added a new student to the group. groupName = {}, studentId = {}", group.getGroupName(), group.getLeaderId());
                        if (!StudentRepo.exportStudentList()){
                            logger.warn("Failed to add new student to group. groupName = {}, studentId = {}", group.getGroupName(), group.getLeaderId());
                            throw new Exception();
                        }

                        new MenuCommand(sendMessage, SUCCESSFUL_INSERTED_MESSAGE).execute(update);
                    } else {
                        ArrayList<String> groupNameMatches = GroupController.getMatchesByGroupName(groupName);
                        if (groupNameMatches != null){
                            sendMessage.replyMessage(userId, GROUP_NOT_FOUND_MESSAGE +
                                    "\n\nЗнайдені можливі співпадіння:\n" +
                                    String.join("\n", groupNameMatches));
                        } else {
                            sendMessage.replyMessage(userId, GROUP_NOT_FOUND_MESSAGE);
                        }
                    }
                }
            }

            update.getMessage().setReplyToMessage(null);
            sendMessage.deleteMessage(userId, messageId);

        } catch (Exception e){
            new MenuCommand(sendMessage, FAILED_CREATE_MESSAGE).execute(update);
            sendMessage.deleteMessage(userId, messageId);
            logger.error("Failed to set group. UserId = {}", userId, e);
        }
    }
}