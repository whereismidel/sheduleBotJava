package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.AdminCommand;
import com.midel.schedulebott.reply_message.DeleteStudentReplyMessage;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.midel.schedulebott.command.CommandName.NO;
import static com.midel.schedulebott.config.ChatConfig.debug;

/**
 * <p>DeleteStudent {@link Command}.</p>
 * <p>Handling the response in {@link DeleteStudentReplyMessage}</p>
 */
@AdminCommand
public class DeleteStudentCommand extends Command {

    public final static String INVALID_ARGUMENT_MESSAGE = "Невірно вказані аргументи або їх кількість або виключений режим відладки. \n"
            +"/deleteStudent - для довідки.";
    public final static String INFO_MESSAGE = "`/deleteStudent` `userId` \\- видалити користувача user\\_id\\.";

    public static final String CONFIRM_DELETE_MESSAGE = "Видалення користувача\n\n"
            +"Ви точно бажаєте видалити <a href=\"tg://user?id=%s\">КОРИСТУВАЧА</a>?\n"
            +"Для підтвердження напишіть <pre>%s</pre>";
    public DeleteStudentCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChat().getId().toString();

        if (arguments != null) {
            if (arguments.size() == 0) {
                sendMessage.sendMarkupV2Message(userId, INFO_MESSAGE);
            } else if (arguments.size() != 1 || !debug){
                sendMessage.sendHTMLMessage(userId, INVALID_ARGUMENT_MESSAGE);
            } else {

                sendMessage.replyMessage(userId, String.format(CONFIRM_DELETE_MESSAGE, arguments.get(0), arguments.get(0)));
            }
        } else {
            sendMessage.sendHTMLMessage(userId, NO.getCommandName());
        }
    }
}
