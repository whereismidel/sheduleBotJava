package com.midel.command;

import com.midel.command.annotation.UserCommand;
import com.midel.keyboard.inline.CreateStudentInlineHandler;
import com.midel.keyboard.inline.InlineKeyboardAnswer;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Start {@link Command}.
 */
@UserCommand
public class StartCommand extends Command {

    public final static String START_MESSAGE = "Привіт, якщо ти староста - зареєструйся.";

    public StartCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();
        Student student = StudentController.getStudentById(userId);


        if (arguments != null && arguments.size() == 1) {
            new CreateStudentInlineHandler(sendMessage).execute(update, arguments.get(0), userId);
        } else {
            if (student == null || student.isLeader() == null) {
                sendMessage.sendInlineKeyboard(userId,
                        START_MESSAGE,
                        new Object[][]{
                                {InlineKeyboardAnswer.IM_LEADER},
                                {InlineKeyboardAnswer.IM_NOT_LEADER}
                        },
                        null);
            } else {
                new MenuCommand(sendMessage).execute(update);
            }
        }
    }
}
