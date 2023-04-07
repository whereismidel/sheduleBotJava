package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.UserCommand;
import com.midel.schedulebott.keyboard.inline.InlineKeyboardAnswer;
import com.midel.schedulebott.student.Student;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@UserCommand
public class RestartCommand extends Command {

    public final static String UNREGISTERED_WARNING_MESSAGE = "Ти ще не зареєструвався.\n"
            + "Для реєстрації використай команду <b>/start</b>";
    public static final String LEADER_WARNING_MESSAGE = "Видалення аккаунту\n"
            +"Ти точно бажаєш перестворити свій аккаунт?\n\n"
            +"На данний момент під твоїм керівництвом знаходиться група <b><u>%s</u></b>.\n"
            +"Видалення аккаунту призведе до видалення групи і всіх пов'язаних з нею даних(в т.ч таблиць).\n\n"
            +"Для видалення відправ <u>%s</u>";
    public static final String WAIT_MESSAGE = "Зачекай декілька секунд, йде процес створення..";

    public static final String STUDENT_WARNING_MESSAGE = "Ти точно бажаєш перестворити свій аккаунт?";

    public RestartCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();
        Student student = StudentController.getStudentById(userId);

        if (student == null){
            sendMessage.sendHTMLMessage(userId, UNREGISTERED_WARNING_MESSAGE);
            return;
        }
        if (student.isLeader() && student.getGroup() != null){
            if (student.getGroup().getSheetId() != null && student.getGroup().getSheetId().equals("Creating..")){
                sendMessage.sendHTMLMessage(userId, WAIT_MESSAGE);
                return;
            }
            sendMessage.replyMessage(userId, String.format(LEADER_WARNING_MESSAGE, student.getGroup().getGroupName(), userId));
        } else {
            sendMessage.sendInlineKeyboard(userId,
                    STUDENT_WARNING_MESSAGE,
                    new Object[][]{
                            {InlineKeyboardAnswer.DELETE_ACCOUNT_YES},
                            {InlineKeyboardAnswer.DELETE_ACCOUNT_NO}
                    },
                    null);
        }

    }
}
