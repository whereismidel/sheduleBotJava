package com.midel.schedulebott.command;

import com.midel.schedulebott.keyboard.inline.InlineKeyboardAnswer;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Start {@link Command}.
 */
public class StartCommand extends Command {

    public final static String START_MESSAGE = "Привіт, якщо ти староста - зареєструйся.";

    public StartCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();
        Student student = StudentController.getStudentById(userId);

        if (student == null || student.isLeader() == null){
            sendMessage.sendInlineKeyboard(userId,
                    START_MESSAGE,
                    new Object[][]{
                            {InlineKeyboardAnswer.IM_LEADER},
                            {InlineKeyboardAnswer.IM_NOT_LEADER}
                    });
        } else {
            new MenuCommand(sendMessage).execute(update);
        }
    }
}
