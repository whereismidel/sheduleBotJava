package com.midel.command;

import com.midel.command.annotation.UserCommand;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@UserCommand
public class MenuCommand extends Command {

    public static String MENU_MESSAGE = "\u2B07 <b>Головне меню</b> \u2B07";

    public MenuCommand(SendMessage sendMessage) {
        super(sendMessage);
        MENU_MESSAGE = "\u2B07 <b>Головне меню</b> \u2B07";
    }

    public MenuCommand(SendMessage sendMessage, String text) {
        super(sendMessage);
        MENU_MESSAGE = text;
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();

        execute(update, userId);
    }

    public void execute(Update update, String userId) {
        Student student = StudentController.getStudentById(userId);

        if (student != null){
            if (student.isLeader()){
                sendMessage.sendClientKeyboard(userId,
                        MENU_MESSAGE,
                        new String[][]{{"\uD83D\uDD25Доступні команди\uD83D\uDD25"}, {"\uD83D\uDD25Меню старости\uD83D\uDD25"}}, false);
            } else {
                sendMessage.sendClientKeyboard(userId,
                        MENU_MESSAGE,
                        new String[][]{{"\uD83D\uDD25Доступні команди\uD83D\uDD25"}}, false);
            }
        } else {
            new StartCommand(sendMessage).execute(update);
        }
    }
}
