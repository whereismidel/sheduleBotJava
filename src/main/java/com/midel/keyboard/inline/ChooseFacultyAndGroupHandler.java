package com.midel.keyboard.inline;

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
import com.midel.template.Template;
import com.midel.template.TemplateController;
import com.midel.template.TemplateRepo;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChooseFacultyAndGroupHandler extends InlineKeyboardHandler{

    private static final Logger logger = LoggerFactory.getLogger(ChooseFacultyAndGroupHandler.class);

    public static final String SPECIFY_FACULTY_MESSAGE = "<b>Обери свій факультет:</b>";
    public static final String SPECIFY_YEAR_MESSAGE = "<b>Вкажи курс зі списку:</b>";
    public static final String SPECIFY_GROUP_MESSAGE = "<b>Вкажи групу зі списку:</b>";

    public static final String TEMPLATE_NOT_FOUND_MESSAGE = "<b>На жаль розкладу для цього розділу ще не додано..</b>\n\n" +
            "Але! Ти можеш це виправити, надішли у відповідь на це повідомлення <b>PDF</b> файл з розкладом, який вам надали куратори.\n" +
            "І згодом його буде додано. <b>Я тобі повідомлю</b>, коли це станеться - головне не закривай повідомлення для мене.";

    public ChooseFacultyAndGroupHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getCallbackQuery().getMessage().getChatId().toString();
        int messageId = -1;
        if (update.getCallbackQuery().getMessage().getMessageId() != null){
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        }

        String[] split = callbackData.split("/");

        String stage, faculty = "?", year = "?", group = "?";

        switch (split.length) {
            case 4:{
                group = split[3];
            }
            case 3:{
                year = split[2];
            }
            case 2:{
                faculty = split[1];
            }
            case 1:{
                stage = split[0]; // not_listed, faculty, year, group, create
                break;
            }
            default: {
                sendMessage.deleteMessage(userId, messageId);
                return;
            }
        }


        switch (stage) {
            case "faculty": {
                List<Object[]> facultiesList = TemplateController.getGroupDistributionsByFaculties().keySet().stream()
                        .map(e -> {
                            InlineKeyboardButton button = new InlineKeyboardButton(e);
                            button.setCallbackData("choose_faculty_and_group#year/"+e);
                            return new Object[]{button};
                        })
                        .sorted(Comparator.comparing(buttonArray -> ((InlineKeyboardButton)buttonArray[0]).getText())).collect(Collectors.toList());

                facultiesList.add(new Object[]{new Pair<>("Немає в списку", "choose_faculty_and_group#not_listed")});

                if (facultiesList.size() < 2) {
                    sendMessage.replyMessage(userId, TEMPLATE_NOT_FOUND_MESSAGE);
                } else {
                    sendMessage.sendInlineKeyboard(
                            userId,
                            SPECIFY_FACULTY_MESSAGE,
                            facultiesList.toArray(new Object[0][]),
                            null
                    );
                }
                break;
            }
            case "year": {
                sendMessage.sendInlineKeyboard(
                        userId,
                        SPECIFY_YEAR_MESSAGE,
                        new Object[][] {
                                {new Pair<>("1 курс", "choose_faculty_and_group#group/" + faculty + "/1")},
                                {new Pair<>("2 курс", "choose_faculty_and_group#group/" + faculty + "/2")},
                                {new Pair<>("3 курс", "choose_faculty_and_group#group/" + faculty + "/3")},
                                {new Pair<>("4 курс", "choose_faculty_and_group#group/" + faculty + "/4")},
                                {new Pair<>("Немає в списку", "choose_faculty_and_group#not_listed")}
                        },
                        null
                );
                break;
            }
            case "group": {
                try {
                    Set<Template> groupTemplates = TemplateController.getGroupDistributionsByFaculties().get(faculty);
                    if (groupTemplates != null) {
                        String finalYear = year;
                        String finalFaculty = faculty;
                        List<Object[]> groupList = groupTemplates.stream()
                                .filter(e -> e.getGroupName().split(" ")[1].charAt(0) == finalYear.charAt(0))
                                .map(e -> {
                                    InlineKeyboardButton button = new InlineKeyboardButton(e.getGroupName());
                                    button.setCallbackData("choose_faculty_and_group#create/" + finalFaculty + "/" + finalYear + "/" + e.getGroupName());
                                    return new Object[]{button};
                                })
                                .sorted(Comparator.comparing(buttonArray -> ((InlineKeyboardButton)buttonArray[0]).getText()))
                                .collect(Collectors.toList());

                        groupList.add(new Object[]{new Pair<>("Немає в списку", "choose_faculty_and_group#not_listed")});

                        if (groupList.size() < 2){
                            sendMessage.replyMessage(userId, TEMPLATE_NOT_FOUND_MESSAGE);
                        } else {
                            sendMessage.sendInlineKeyboard(
                                    userId,
                                    SPECIFY_GROUP_MESSAGE,
                                    groupList.toArray(new Object[0][]),
                                    null
                            );
                        }
                    } else {
                        sendMessage.replyMessage(userId, TEMPLATE_NOT_FOUND_MESSAGE);
                    }
                } catch (Exception ignored){
//                    ignored.printStackTrace();
                }

                break;
            }
            case "create": {
                Template template = TemplateRepo.templates.get(group);

                if (template != null) {
                    createOrSetGroup(update, userId, template.getGroupName() + "#" + userId);
                }
                break;
            }
            case "not_listed": {
                sendMessage.replyMessage(userId, TEMPLATE_NOT_FOUND_MESSAGE);
                break;
            }

        }
        sendMessage.deleteMessage(userId, messageId);

//        // Передається керування до CreateAndSetGroupNameReplyMessage
//        sendMessage.replyMessage(userId, SPECIFY_GROUP_MESSAGE);
//        sendMessage.deleteMessage(userId, messageId);
    }

    private void createOrSetGroup(Update update, String userId, String groupName) {

        final String SUCCESSFUL_CREATE_MESSAGE = "Група успішно створена.";
        final String GROUP_ALREADY_EXIST_MESSAGE = "Група з такою назвою вже існує.\n"
                +"Якщо сталась помилка, і саме ти староста цієї групи звернись до " + ChatConfig.creatorUsername;
        final String ALREADY_HAVE_GROUP_MESSAGE = "За тобою вже закріплена група.\n"
                +"Якщо сталась помилка спробуй <code>/restart</code> або звернись до " + ChatConfig.creatorUsername;
        final String ONLY_LEADER_OPTION_MESSAGE = "Ця функція доступна тільки старості.";
        final String FAILED_CREATE_MESSAGE = "Під час створення сталась непередбачувана помилка.\n"
                +"Спробуй пізніше або звернись до " + ChatConfig.creatorUsername;

//        String userId = update.getMessage().getChatId().toString();
//        int messageId = update.getMessage().getReplyToMessage().getMessageId();
//        String userId = update.getMessage().getChatId().toString();
//        int messageId = update.getMessage().getReplyToMessage().getMessageId();

        try {
            Student student = StudentController.getStudentById(userId);

//            String groupName = update.getMessage().getText().toUpperCase();

            if (student != null) {
                if (student.isLeader()) {

                    Group group;

                    if (GroupController.getGroupByName(groupName) == null) {
                        if (GroupController.getGroupByLeader(userId) == null) {

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

                                new MenuCommand(sendMessage, SUCCESSFUL_CREATE_MESSAGE).execute(update, userId);

                                new LeaderMenuReplyKeyboardCommand(sendMessage).execute(update, userId);
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
                }
            } else {
                sendMessage.sendHTMLMessage(userId, ONLY_LEADER_OPTION_MESSAGE);
            }

        } catch (Exception e){
            new MenuCommand(sendMessage, FAILED_CREATE_MESSAGE).execute(update);
            logger.error("Failed to set group. UserId = {}", userId, e);
        }
    }
}