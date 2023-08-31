package com.midel.reply_message;

import com.midel.command.MenuCommand;
import com.midel.config.ChatConfig;
import com.midel.config.DBConfig;
import com.midel.google.SheetAPI;
import com.midel.group.Group;
import com.midel.group.GroupController;
import com.midel.group.GroupRepo;
import com.midel.keyboard.reply.LeaderMenuReplyKeyboardCommand;
import com.midel.telegram.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Request from {@link LeaderMenuReplyKeyboardCommand}
 */
public class CreateSheetAndShareEmailReplyMessage extends ReplyMessage {

    private static final Logger logger = LoggerFactory.getLogger(CreateSheetAndShareEmailReplyMessage.class);
    private static final String UNKNOWN_EMAIL_MESSAGE = "<b>Залишилась пошта</b>\n\n"
            +"Я ж просив перевірити декілька разів \uD83D\uDE21\n\n"
            +"Пошта повинна бути в форматі: <code>1234567@stud.nau.edu.ua</code>" +
            "<i>Для відповіді відміть це повідомлення</i>";

    public static final String WAIT_MESSAGE = "Зачекай декілька секунд, йде процес створення..";
    public static final String ERROR_MESSAGE = "Ой, щось пішло не так, спробуй ще раз через меню старости.\n"
            +"Ти завжди можеш почати все спочатку <pre>/restart</pre>\n"
            +"Або звернутись до " + ChatConfig.creatorUsername;

    public static final String SUCCESSFUL_MESSAGE = "Чудово, твоя особиста таблиця створена.\n\n"
            +"Ось посилання(доступ з %s): <a href=\"%s\"><b>ТИЦЬ</b></a>\n\n"
            +"Заповни таблюці згідно з інструкцій, які вказані в самій таблиці.\n"
            +"Обов'язково дотримуйся правил заповнення, "
            +"<a href=\"https://docs.google.com/spreadsheets/d/1t4lSypq-8c07Y_96sSI40emyvjgkFbUSK528-1YZp-k\">для прикладу можеш використати ось цю таблицю</a>\n"
            +"Для <b>активації</b> розкладу в \"Меню старости\" потрібно <b>ввімкнути відправку розкладу</b>.\n\n"
            +"Також тобі відкрився повний доступ до \"Меню старости\". Ознайомся з ним";

    public CreateSheetAndShareEmailReplyMessage(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChatId().toString();
        String emailFromUserText = update.getMessage().getText();
        int messageId = update.getMessage().getReplyToMessage().getMessageId();

        Group group = GroupController.getGroupByLeader(userId);

        if (group != null && (group.getSheetId() == null || group.getSheetId().equals(""))){

            Pattern pattern = Pattern.compile("^[a-z0-9](\\.?[a-z0-9]){5,}@stud\\.nau\\.edu\\.ua$");
            Matcher matcher = pattern.matcher(emailFromUserText);
            Runnable task = () -> {
                if (matcher.matches()) {
                    try {
                        sendMessage.sendHTMLMessage(userId, WAIT_MESSAGE);

                        group.setSheetId("Creating..");

                        String sheetId = SheetAPI.createSpreadsheetFromTemplateAndSharePermission(
                                group.getGroupName(),
                                new String[]{DBConfig.mainAccount, emailFromUserText},
                                DBConfig.templateSheet
                        );
                        if (sheetId == null) {
                            throw new Exception();
                        }
                        group.setSheetId(sheetId);
                        group.setLeaderEmail(emailFromUserText);
                        group.getSettings().setLastRequestToTable(LocalDateTime.now(ZoneId.of("Europe/Kiev")));

                        if (!GroupRepo.exportGroupList()) {
                            throw new Exception();
                        }
                        logger.info("Table has been created and attached to the group. groupName = {}, sheetId = {}", group.getGroupName(), group.getSheetId());
                        new MenuCommand(sendMessage,
                                String.format(SUCCESSFUL_MESSAGE, emailFromUserText, "https://docs.google.com/spreadsheets/d/" + sheetId)).execute(update);

                    } catch (Exception e) {
                        group.setSheetId(null);
                        group.setLeaderEmail(null);

                        logger.warn("Failed to add table to group. groupName = {}", group.getGroupName(), e);
                        new MenuCommand(sendMessage, ERROR_MESSAGE).execute(update);
                    }
                } else {
                    sendMessage.replyMessage(userId, UNKNOWN_EMAIL_MESSAGE);
                }
            };
            Thread thread = new Thread(task);
            thread.start();

            sendMessage.deleteMessage(userId, messageId);
        }
    }
}
