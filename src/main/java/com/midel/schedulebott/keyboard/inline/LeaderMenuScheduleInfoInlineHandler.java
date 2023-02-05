package com.midel.schedulebott.keyboard.inline;

import com.midel.schedulebott.command.MenuCommand;
import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.group.Group;
import com.midel.schedulebott.schedule.ScheduleController;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;

public class LeaderMenuScheduleInfoInlineHandler extends InlineKeyboardHandler {
    private static final String INFO_MESSAGE = "Статус відправки розкладу: <b>%s</b>\n"
            +"Відправка розкладу: <b>за %s хвилин до початку</b>\n"
            +"Відправка розкладу на початку дня: <b>7:35-7:40</b>\n"
            +"Відправка розкладу на наступний день: <b>17:30-17:35</b>\n"
            +"Віправка розкладу відповідно до пар по наступному графіку:\n"
            +"<pre>1 пара - %s</pre>\n"
            +"<pre>2 пара - %s</pre>\n"
            +"<pre>3 пара - %s</pre>\n"
            +"<pre>4 пара - %s</pre>\n"
            +"<pre>5 пара - %s</pre>\n"
            +"<pre>6 пара - %s</pre>\n\n"
            +"<i>*Можливість редагування цієї інформації з'явиться згодом</i>"; // ToDO редагування відправки по розкладу

    private static final String ERROR_MESSAGE = "Не вдалось отримати актуальну інформацію про розклад. Спробуй пізніше.";

    public LeaderMenuScheduleInfoInlineHandler(SendMessage sendMessage) {
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
                        String.format(INFO_MESSAGE,
                                group.getSettings().isState()?"Активно":"Вимкнено",
                                ChatConfig.scheduleTime,
                                ScheduleController.notificationSchedule[0].getTimeForReal().format(DateTimeFormatter.ofPattern("HH:mm")),
                                ScheduleController.notificationSchedule[1].getTimeForReal().format(DateTimeFormatter.ofPattern("HH:mm")),
                                ScheduleController.notificationSchedule[2].getTimeForReal().format(DateTimeFormatter.ofPattern("HH:mm")),
                                ScheduleController.notificationSchedule[3].getTimeForReal().format(DateTimeFormatter.ofPattern("HH:mm")),
                                ScheduleController.notificationSchedule[4].getTimeForReal().format(DateTimeFormatter.ofPattern("HH:mm")),
                                ScheduleController.notificationSchedule[5].getTimeForReal().format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                );

            } catch (Exception e) {
                new MenuCommand(sendMessage, ERROR_MESSAGE).execute(update, userId);
            }
        }
    }

}
