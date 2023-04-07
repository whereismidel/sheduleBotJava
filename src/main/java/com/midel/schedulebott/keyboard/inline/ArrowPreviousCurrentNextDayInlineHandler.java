package com.midel.schedulebott.keyboard.inline;

import com.midel.schedulebott.exceptions.MissingMessageException;
import com.midel.schedulebott.group.Group;
import com.midel.schedulebott.group.GroupController;
import com.midel.schedulebott.schedule.ScheduleController;
import com.midel.schedulebott.student.StudentController;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ArrowPreviousCurrentNextDayInlineHandler extends InlineKeyboardHandler {

    private final int dayPosition;

    public ArrowPreviousCurrentNextDayInlineHandler(SendMessage sendMessage, int dayPosition) {
        super(sendMessage);
        this.dayPosition = dayPosition;
    }
    private static final String MISSING_MESSAGE = "<strong>%s, %s</strong>\n" +
            "\n<pre>В цей день пари не проводяться або закінчився навчальний процес.</pre>";

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String chatId = message.getChatId().toString();

        if (StudentController.isFlood(update.getCallbackQuery().getFrom().getId().toString())){
            return;
        }

        int messageId = message.getMessageId();
        String[] splitData = callbackData.replace("//", "/").split("/");
        String userOwner = splitData[0];
        String groupOwner = splitData[1];

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(splitData[2])), ZoneId.of("Europe/Kiev")).plusDays(dayPosition);
        if (dayPosition == 0){
            ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));
            if (zonedDateTimeNow.getDayOfYear() != zonedDateTime.getDayOfYear()){
                zonedDateTime = zonedDateTimeNow;
            } else {
                return;
            }
        }

        if (update.getCallbackQuery().getFrom().getId().toString().equals(userOwner)) {

            Group group = GroupController.getGroupByName(groupOwner.replace(" ", "/"));

            if (group != null) {
                try {
                    String formatMsg = ScheduleController.getMessageForStartOfNewDay(group, zonedDateTime);
                    if (!sendMessage.editMessage(chatId, messageId,
                            formatMsg,
                            sendMessage.getInlineKeyboardMarkup(new Object[][]{
                                    {InlineKeyboardAnswer.ARROW_PREVIOUS_DAY, InlineKeyboardAnswer.ARROW_CURRENT_DAY, InlineKeyboardAnswer.ARROW_NEXT_DAY}
                            },
                    splitData[0] + "/" + splitData[1] + "/" + zonedDateTime.toEpochSecond()))){
                        sendMessage.deleteMessage(chatId, messageId);
                        sendMessage.sendInlineKeyboard(chatId,
                                formatMsg,
                                new Object[][]{
                                        {InlineKeyboardAnswer.ARROW_PREVIOUS_DAY, InlineKeyboardAnswer.ARROW_CURRENT_DAY, InlineKeyboardAnswer.ARROW_NEXT_DAY}
                                },
                        splitData[0] + "/" + splitData[1] + "/" + zonedDateTime.toEpochSecond());
                    }

                } catch (MissingMessageException mm){

                    sendMessage.editMessage(chatId, messageId,
                            String.format(MISSING_MESSAGE, ScheduleController.dayOfWeek[zonedDateTime.getDayOfWeek().getValue() % 7], zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM"))),
                            sendMessage.getInlineKeyboardMarkup(new Object[][]{
                                    {InlineKeyboardAnswer.ARROW_PREVIOUS_DAY, InlineKeyboardAnswer.ARROW_CURRENT_DAY, InlineKeyboardAnswer.ARROW_NEXT_DAY}
                            },
                    splitData[0] + "/" + splitData[1] + "/" + zonedDateTime.toEpochSecond())
                    );
                } catch (Exception ignored){}
            }
        }
    }
}
