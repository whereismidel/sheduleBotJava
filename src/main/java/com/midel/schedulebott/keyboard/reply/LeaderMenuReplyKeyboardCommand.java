package com.midel.schedulebott.keyboard.reply;

import com.midel.schedulebott.config.BotConfig;
import com.midel.schedulebott.group.Group;
import com.midel.schedulebott.group.GroupController;
import com.midel.schedulebott.keyboard.inline.InlineKeyboardAnswer;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class LeaderMenuReplyKeyboardCommand extends ReplyKeyboardCommand {

    public static final String UNKNOWN_GROUP_MESSAGE = "<b>Вкажіть групу</b>\n"
                                                +"Для початку напиши групу, в якій виконуєш обов'язки старости:\n"
                                                +"<i>Наприклад:</i>\n"
                                                +"БІ-144Б\n"
                                                +"СЗ-312Б(А)\n\n"
                                                +"<b>Назва групи не повинна містити пробілів, а також будь-яких символів окрім: \'-\', \'(\', \')\'.</b>\n"
                                                +"<i>*Для відповіді відміть це повідомлення(якщо це не сталось автоматично)</i>";

    public static final String UNKNOWN_CHANNEL_MESSAGE = "Наступним кроком <u>тобі</u> необхідно створити <b>новий</b> канал.\n\n"
            +"... -> Новий канал -> Назва: %s Розклад-> Приватний канал\n\n"
            +"Додати цього бота @%s та надати йому права адміністратора, а саме:\n"
            +">Дозвіл на зміну інформації про канал\n"
            +">Дозвіл надсилати повідомлення\n"
            +">Дозвіл змінювати повідомлення\n"
            +">Дозвіл на видалення повідомлень\n\n" +
            "Якщо бот вже є на вашому каналі - видаліть і додайте заново, або змініть йому будь-яке з прав(але так щоб всі умови вище зберігались)";

    private static final String UNKNOWN_EMAIL_MESSAGE = "<b>Залишилась пошта</b>\n" +
            "Вкажи свою пошту в домені @stud.nau.edu.ua\n\n" +
            "Це потрібно для того, щоб я надав доступ до твоєї особистої таблиці, яку потрібно буде заповнити розкладом.\n\n" +
            "<i>*Перевір декілька разів пошту, щоб потім не довелось всі попередні етапи проходити заново \uD83D\uDE10</i>" +
            "<i>Для відповіді відміть це повідомлення</i>";

    public static final String WAIT_MESSAGE = "Зачекай декілька секунд, йде процес створення..";
    public static String LEADER_MENU_MESSAGE = "Твоя панель керування розкладом:";

    public LeaderMenuReplyKeyboardCommand(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();

        execute(update, userId);
    }

    public void execute(Update update, String userId) {
        Student student = StudentController.getStudentById(userId);
        Group group;

        if (student != null && student.isLeader()){
            if (student.getGroup() == null){
                sendMessage.replyMessage(userId, UNKNOWN_GROUP_MESSAGE);
                return;
            } else {
                group = GroupController.getGroupByLeader(userId);
                if (group == null) {
                    sendMessage.replyMessage(userId, UNKNOWN_GROUP_MESSAGE);
                    return;
                }
            }

            if (group.getChannelId() == null){
                sendMessage.sendHTMLMessage(userId,
                        String.format(UNKNOWN_CHANNEL_MESSAGE, group.getGroupName(), BotConfig.BOT_USERNAME));
                return;
            }
            if (group.getSheetId() == null){
                sendMessage.replyMessage(userId, UNKNOWN_EMAIL_MESSAGE);
                return;
            }

            if (group.getSheetId().equals("Creating..")){
                sendMessage.sendHTMLMessage(userId, WAIT_MESSAGE);
                return;
            }
            InlineKeyboardAnswer state = group.getSettings().isState()?
                        InlineKeyboardAnswer.LEADER_MENU_OFF_SCHEDULE : InlineKeyboardAnswer.LEADER_MENU_ON_SCHEDULE;

            sendMessage.sendInlineKeyboard(userId,
                    LEADER_MENU_MESSAGE,
                    new Object[][]{
                            {state},
                            {InlineKeyboardAnswer.LEADER_MENU_CHECK_SCHEDULE},
                           // {InlineKeyboardAnswer.LEADER_MENU_TEST_SCHEDULE}, // ToDO
                           // {InlineKeyboardAnswer.LEADER_MENU_CHANGE_LEADER} // ToDO
                            {InlineKeyboardAnswer.LEADER_MENU_GROUP_INFO},
                            {InlineKeyboardAnswer.LEADER_MENU_SCHEDULE_INFO}
                    });

        } else {
            new UnknownReplyKeyboardCommand(sendMessage).execute(update);
        }
    }
}
