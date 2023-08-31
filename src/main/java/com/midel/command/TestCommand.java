package com.midel.command;

import com.midel.command.annotation.AdminCommand;
import com.midel.keyboard.inline.InlineKeyboardAnswer;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Date;


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
//        sendMessage.sendHTMLMessage(userId, "<a href=\"tg://user?id=5458685173\">test :)</a>");
//
//        SheetAPI.createSpreadsheetFromTemplateAndSharePermission("123", new String[]{"ogure.blin@gmail.com"}, "1SfBXj4pX3eqSTImAxN7mX-HXZ9mTJGBvox7hlE8KjWA");
        String userOwner = "userID";
        String groupOwner = "group";
        InlineKeyboardButton button = new InlineKeyboardButton("Button");
        button.setCallbackData("button");
        sendMessage.sendInlineKeyboard(userId,
                "formatMsg",
                new Object[][]{
                        {button, InlineKeyboardAnswer.ARROW_CURRENT_DAY, InlineKeyboardAnswer.ARROW_NEXT_DAY}
                },
                userOwner + "/" + groupOwner + "/" + new Date().toInstant().toEpochMilli());
    }


}
