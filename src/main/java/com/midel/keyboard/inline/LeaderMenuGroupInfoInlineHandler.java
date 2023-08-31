package com.midel.keyboard.inline;

import com.midel.command.MenuCommand;
import com.midel.config.BotConfig;
import com.midel.group.Group;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class LeaderMenuGroupInfoInlineHandler extends InlineKeyboardHandler {
    private static final String INFO_MESSAGE = "Ідентифікатор групи: <code>%s</code>\n"
            +"Статус відправки розкладу: <b>%s</b>\n"
            +"Статус підключеного каналу: <b>%s</b>\n"
            +"Особиста таблиця(доступ з %s): <a href=\"https://docs.google.com/spreadsheets/d/%s\"><b>натискай</b></a>\n\n"
            +"Щоб студенти могли користуватись ботом в особистих повідомлення поділись ось цим посиланням: <code>t.me/%s?start=%s</code>\n";

    private static final String ERROR_MESSAGE = "Не вдалось отримати актуальну інформацію про групу. Спробуй пізніше.";

    public LeaderMenuGroupInfoInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String userId = message.getChatId().toString();

        int messageId = message.getMessageId();
        sendMessage.deleteMessage(userId, messageId);

        Student student = StudentController.getStudentById(userId);
        Group group;
        if (student != null && student.isLeader() && student.getGroup() != null){
            try {
                group = student.getGroup();
                sendMessage.sendHTMLMessage(
                        userId,
                        String.format(
                                INFO_MESSAGE,
                                group.getGroupName(),
                                group.getSettings().isState()?"Активно":"Вимкнено",
                                group.getChannelId()!=null?"Підключено":"Не підключено",
                                group.getLeaderEmail(), group.getSheetId(),
                                BotConfig.BOT_USERNAME, group.getLeaderId()
                        )
                );

            } catch (Exception e) {
                new MenuCommand(sendMessage, ERROR_MESSAGE).execute(update, userId);
            }
        }
    }
}
