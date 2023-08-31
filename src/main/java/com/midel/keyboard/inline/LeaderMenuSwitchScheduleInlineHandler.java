package com.midel.keyboard.inline;

import com.midel.command.MenuCommand;
import com.midel.group.Group;
import com.midel.group.GroupRepo;
import com.midel.keyboard.reply.LeaderMenuReplyKeyboardCommand;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


public class LeaderMenuSwitchScheduleInlineHandler extends InlineKeyboardHandler {

    public static final String ERROR_TEST_MESSAGE = "Невдалось змінити стан відправки розкладу. Можливо є помилки у твоїй таблиці.";
    public static final String SUCCESS_ON_MESSAGE = "Стан відправки розкладу змінено. Розклад <b>буде надсилатись</b> по графіку на канал.";
    public static final String SUCCESS_OFF_MESSAGE = "Стан відправки розкладу змінено. Розклад <b><u>не</u> буде надсилатись</b> по графіку на канал.";
    public LeaderMenuSwitchScheduleInlineHandler(SendMessage sendMessage) {
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

                // If state is false
                if (group != null && group.getSheetId() != null && group.getChannelId() != null){
                    boolean reservedState = group.getSettings().isState();

                    if (!group.getSettings().isState()) {
                        group.getSettings().setState(true);
                        new LeaderMenuCheckScheduleInlineHandler(sendMessage).execute(update);

                        if (group.getSettings().isState()) {
                            sendMessage.sendHTMLMessage(userId, SUCCESS_ON_MESSAGE);
                        } else {
                            sendMessage.sendHTMLMessage(userId, ERROR_TEST_MESSAGE);
                        }
                    } else {
                        group.getSettings().setState(false);
                        new LeaderMenuReplyKeyboardCommand(sendMessage).execute(update, userId);
                        sendMessage.sendHTMLMessage(userId, SUCCESS_OFF_MESSAGE);
                    }

                    if (reservedState != group.getSettings().isState() && !GroupRepo.exportGroupList()){
                        group.getSettings().setState(reservedState);
                        sendMessage.sendHTMLMessage(userId, ERROR_TEST_MESSAGE);
                    }
                }
            } catch (Exception e) {
                new MenuCommand(sendMessage, ERROR_TEST_MESSAGE).execute(update, userId);
            }
        }
    }
}
