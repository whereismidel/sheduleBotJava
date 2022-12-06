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
        sendMessage.sendClientKeyboard(update.getMessage().getChatId().toString(),
                "Test",
                new String[][]{{"Старт", "Стоп"},{"Тест"}}, false);
    }


}
