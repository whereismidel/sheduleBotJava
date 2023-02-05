package com.midel.schedulebott.command;

import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Random;

/**
 * <p>FloodCommand {@link Command}.</p>
 * <p>Handling the response in {@link StudentController}</p>
 */
public class FloodCommand extends Command {

    public static final String[] FLOOD_MESSAGE = new String[]{
            "Ти дуже часто пишеш, мені таке не подобається\uD83D\uDE1F",
            "Прошу, перестань так часто писати повідомлення\uD83D\uDE21",
            "Ти дійсно хочеш, щоб я зламався? Не пиши так часто\uD83D\uDE30"
    };

    public FloodCommand(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        sendMessage.sendHTMLMessage(update.getMessage().getChatId().toString(), FLOOD_MESSAGE[new Random().nextInt(FLOOD_MESSAGE.length)]);
    }
}
