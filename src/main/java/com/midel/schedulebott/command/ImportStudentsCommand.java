package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.AdminCommand;
import com.midel.schedulebott.student.StudentRepo;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.midel.schedulebott.config.ChatConfig.debug;

@AdminCommand
public class ImportStudentsCommand extends Command {
    public final static String DEBUG_OFF_MESSAGE = "Виключений режим відладки.";
    public final static String SUCCESSFUL_MESSAGE = "Таблиця \"Користувачі\" успішно імпортована";
    public final static String FAIL_MESSAGE = "Таблиця \"Користувачі\" успішно імпортована";
    public ImportStudentsCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChat().getId().toString();

        if (!debug){
            sendMessage.sendHTMLMessage(userId, DEBUG_OFF_MESSAGE);
        } else {
            if (StudentRepo.importStudentList()){
                sendMessage.sendHTMLMessage(userId, SUCCESSFUL_MESSAGE);
            } else {
                sendMessage.sendHTMLMessage(userId, FAIL_MESSAGE);
            }
        }
    }
}
