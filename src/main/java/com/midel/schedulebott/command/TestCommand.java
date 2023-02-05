package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.AdminCommand;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


/**
 * Test any feature {@link Command}.
 */
@AdminCommand
public class TestCommand extends Command {

    public TestCommand(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();
        sendMessage.sendHTMLMessage(userId, "<a href=\"tg://user?id=5458685173\">test :)</a>");
    }


}
