package com.midel.command;

import com.midel.command.annotation.GroupCommand;
import com.midel.command.annotation.UserCommand;
import com.midel.keyboard.inline.InlineKeyboardAnswer;
import com.midel.student.Student;
import com.midel.student.StudentController;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@GroupCommand
@UserCommand
public class SetGroupCommand extends Command {

    public final static String INVALID_ARGUMENT_MESSAGE = "Невірно вказані аргументи або їх кількість. \n"
            + "/setGroup help - для довідки.";
    public final static String INFO_MESSAGE = "`/setGroup` \\- встановити/змінити групу\\.";

    public final static String LEADER_WARNING_MESSAGE = "Ти <b><u>староста</u></b> групи <b>%s</b>.\n\n"
            + "Зміна групи призведе до видалення існуючої(разом зі всіма таблицями).\n"
            + "Рекомендую перед зміною передатити права іншому члену своєї групи через <u>меню старости</u>.\n\n"
            + "Інакше, щоб змінити групу тобі необхідно повторно зареєструватись, команда <b>/restart</b>";

    public final static String STUDENT_CHANGE_WARNING_MESSAGE = "Ти <b><u>студент</u></b> групи <b>%s</b>.\n\n"
            + "Чи бажаєш ти змінити свою групу?";

    public final static String UNREGISTERED_WARNING_MESSAGE = "Ти ще не зареєструвався.\n"
            + "Для реєстрації використай команду <b>/start</b>";

    public static final String SPECIFY_GROUP_MESSAGE = "<b>Вкажіть групу</b>\n"
            +"Вкажи свою групу:\n"
            +"<i>Наприклад:</i>\n"
            +"БІ-144Б\n"
            +"СЗ-312Б(А)\n\n";

    public SetGroupCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();

        if (arguments != null) {
            if (arguments.size() == 1) {
                sendMessage.sendMarkupV2Message(userId, INFO_MESSAGE);
            } else if (arguments.size() != 0){
                sendMessage.sendHTMLMessage(userId, INVALID_ARGUMENT_MESSAGE);
            } else {
                Student student = StudentController.getStudentById(userId);

                if (student == null){
                    sendMessage.sendHTMLMessage(userId, UNREGISTERED_WARNING_MESSAGE);

                } else {
                    if ((update.getMessage().isGroupMessage() || update.getMessage().isSuperGroupMessage()) && student.isLeader() == null){
                        student.setLeader(false);
                    } else if (student.isLeader()) {
                        sendMessage.sendHTMLMessage(userId, String.format(LEADER_WARNING_MESSAGE, student.getGroup().getGroupName()));
                        return;
                    }

                    if (student.getGroup() != null){
                        sendMessage.sendInlineKeyboard(userId,
                                String.format(STUDENT_CHANGE_WARNING_MESSAGE, student.getGroup().getGroupName()),
                                new Object[][]{
                                    {InlineKeyboardAnswer.GROUP_CHANGE_STUDENT_YES},
                                    {InlineKeyboardAnswer.GROUP_CHANGE_STUDENT_NO}
                                },
                                null);
                    } else {
                        sendMessage.replyMessage(userId, SPECIFY_GROUP_MESSAGE);
                    }

                }
            }
        } else {
            new NoCommand(sendMessage).execute(update);
        }
    }
}
