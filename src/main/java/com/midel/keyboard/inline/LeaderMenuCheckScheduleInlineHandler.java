package com.midel.keyboard.inline;

import com.midel.command.MenuCommand;
import com.midel.config.ChatConfig;
import com.midel.group.Group;
import com.midel.group.GroupRepo;
import com.midel.keyboard.reply.LeaderMenuReplyKeyboardCommand;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class LeaderMenuCheckScheduleInlineHandler extends InlineKeyboardHandler {

    public static final String SUCCESSFUL_TEST_MESSAGE = "Таблиці перевірені, помилок не знайдено, синтаксичні помилки не перевірялись. \n"
            +"Тепер ти можеш ввімкнути розсилку розкладу в меню старости.\n\n"
            +"Якщо виникнуть проблеми або питання по роботі - " + ChatConfig.creatorUsername;
    public static final String ERROR_TEST_MESSAGE = "Невдалось пройти перевірку таблиці, виправ всі помилки та виконай поради і спробуй ще раз.";
    public static final String COOLDOWN_MESSAGE = "Не можна так часто робити перевірку, зачекай %s сек. і повтори спробу.";

    public LeaderMenuCheckScheduleInlineHandler(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getCallbackQuery().getMessage().getChatId().toString();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        Student student = StudentController.getStudentById(userId);

        if (student != null && student.isLeader() && student.getGroup() != null){
            try {
                Group group = student.getGroup();

                int minimalCooldown = 30;
                long cooldown = Duration.between(group.getSettings().getLastRequestToTable(), LocalDateTime.now(ZoneId.of("Europe/Kiev"))).getSeconds();

                if (cooldown < minimalCooldown && !student.getId().equals(ChatConfig.ADMINS.stream().findFirst().orElse("-1"))){
                    new MenuCommand(sendMessage, String.format(COOLDOWN_MESSAGE, minimalCooldown - cooldown)).execute(update, userId);
                    sendMessage.deleteMessage(userId, messageId);
                    return;
                }

                // Після імпорту стан перевірки залежить від прапору valid
                // valid    true - таблиця коректна
                //          false - помилки в таблиці
                group.getSettings().setValid(true); // Для пропуску в оператор з оповіщенням користувача.
                GroupRepo.importGroupSubjects(group);
                boolean subjectTableIsValid = group.getSettings().isValid();

                group.getSettings().setValid(true); // Для пропуску в оператор з оповіщенням користувача.
                GroupRepo.importGroupSchedule(group);
                boolean scheduleTableIsValid = group.getSettings().isValid();


                if (scheduleTableIsValid && subjectTableIsValid){
                    if (!group.getSettings().isState()){
                        sendMessage.sendHTMLMessage(userId, SUCCESSFUL_TEST_MESSAGE);
                    }
                    new LeaderMenuReplyKeyboardCommand(sendMessage).execute(update, userId);
                } else {
                    new MenuCommand(sendMessage, ERROR_TEST_MESSAGE).execute(update, userId);
                }

                // Update "last request date" information for use on next import
                group.getSettings().setLastRequestToTable(LocalDateTime.now(ZoneId.of("Europe/Kiev")));
                GroupRepo.exportGroupList();

                group.getSettings().setValid(false);
            } catch (Exception e) {
                new MenuCommand(sendMessage, ERROR_TEST_MESSAGE).execute(update, userId);
            }
        }

        sendMessage.deleteMessage(userId, messageId);
    }
}
