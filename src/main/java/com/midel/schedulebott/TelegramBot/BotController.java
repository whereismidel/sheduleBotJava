package com.midel.schedulebott.TelegramBot;

import com.midel.schedulebott.Config.ChatConfig;
import com.midel.schedulebott.Exceptions.MissingMessageExceptions;
import com.midel.schedulebott.Group.Group;
import com.midel.schedulebott.Group.GroupController;
import com.midel.schedulebott.Schedule.ScheduleController;
import com.midel.schedulebott.Schedule.ScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;


@Controller
public class BotController {

    static final Logger logger = LoggerFactory.getLogger(BotController.class);

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "<center><strong>БОТ ПРАЦЮЄ</strong></center>";
    }

    static class Command {
        private String command = "";
        private final ArrayList<String> args = new ArrayList<>();

        Command(String text) {
            String[] split = text.split(" ");

            if (split[0].charAt(0) == '/'){
                this.command = split[0];
            }

            this.args.addAll(Arrays.asList(split).subList(1, split.length));
        }

        public String getCommand() {
            return command;
        }

        public ArrayList<String> getArgs() {
            return args;
        }

        public boolean isCommand() {
            return !command.equals("");
        }

        @Override
        public String toString() {
            return "Command{" +
                    "command='" + command + '\'' +
                    ", args=" + args +
                    '}';
        }
    }

    public static void returnAnswer(Update update){
        SendMessage sendMsg = new SendMessage();
        Message message;
        String text;
        Command command;

        if (update.hasMessage()) {
            message = update.getMessage();

            if (message.hasText()){

                text = message.getText();

                if (message.isGroupMessage() || message.isSuperGroupMessage()){
                    if (text.contains("@NeKlown_bot")){
                        text = text.replace("@NeKlown_bot", "");
                    } else {
                        return;
                    }
                }

                command = new Command(text);

            } else {
                return;
            }
        } else {
            return;
        }

        if (command.isCommand()) {
            if (update.getMessage().getChatId().equals(ChatConfig.ADMIN_ID)) {
                switch (command.getCommand()) {
                    // Ввімкнути/вимкнути розсилку розкладу
                    case "/switchschedule": {
                        if (ChatConfig.sendSchedule) {
                            ChatConfig.sendSchedule = false;
                            sendMsg.sendHTMLMessage(ChatConfig.getAdminId(), "Schedule <b>will NOT</b> be sent.");
                        } else {
                            ChatConfig.sendSchedule = true;
                            sendMsg.sendHTMLMessage(ChatConfig.getAdminId(), "Schedule <b>will</b> be sent.");
                        }
                        break;
                    }

                    // Ввімкнути/вимкнути розсилку розкладу
                    case "/switchdebug": {
                        if (ChatConfig.debug) {
                            ChatConfig.debug = false;
                            sendMsg.sendHTMLMessage(ChatConfig.getAdminId(), "DEBUG mode <b>OFF</b>.");
                        } else {
                            ChatConfig.debug = true;
                            sendMsg.sendHTMLMessage(ChatConfig.getAdminId(), "DEBUG mode <b>ON</b>.");
                        }
                        break;
                    }

                    // Перевірка розкладу від конкретно вказаних агрументів /getLesson деньТижня номерРозкладу номерПари
                    case "/getLesson": {
                        if (ChatConfig.debug) {
                            switch (command.getArgs().size()) {
                                case 0: {
                                    sendMsg.sendTextMessage(ChatConfig.getAdminId(), "Too few arguments specified. /help");
                                    for(int j = 1; j<=2; j++){
                                        for(int i = 1; i<=6; i++){
                                            for(int k = 1; k<=6; k++){
                                                ChatConfig.debugArray = new ArrayList<>();
                                                ChatConfig.debugArray.add(i);
                                                ChatConfig.debugArray.add(j);
                                                ChatConfig.debugArray.add(k);

                                                new ScheduledTask().checkAvailabilityOfLessonsEveryDay();
                                            }
                                        }
                                    }
                                    break;
                                }
                                case 1: {
                                    ChatConfig.debugArray = new ArrayList<>();
                                    ChatConfig.debugArray.add(Integer.parseInt(command.args.get(0)));
                                    new ScheduledTask().updateAndStartOfNewDay(); // ToDo убрати нахуй
                                    break;
                                }
                                case 3: {
                                    ChatConfig.debugArray = new ArrayList<>();
                                    ChatConfig.debugArray.add(Integer.parseInt(command.args.get(0)));
                                    ChatConfig.debugArray.add(Integer.parseInt(command.args.get(1)));
                                    ChatConfig.debugArray.add(Integer.parseInt(command.args.get(2)));

                                    new ScheduledTask().checkAvailabilityOfLessonsEveryDay(); // ToDo так само убрати
                                    break;
                                }
                            }

                        } else {
                            sendMsg.sendTextMessage(ChatConfig.getAdminId(), "Only in debug mode. /switchdebug");
                        }
                        break;
                    }

                    // Надіслати повідомлення через команду
                    // Відмітити повідомлення з нтмл розміткою без агрументів - перевірка форматування.
                    // Відмітити з аргументами /sendmessage ButtonText Link - надіслати на канал з форматуванням і кнопкою з посиланням
   /*                 case "/sendmessage": {
                        if (command.getArgs().size() == 2) {

                            InlineKeyboardButton ib = new InlineKeyboardButton();
                            ib.setUrl(command.args.get(1));
                            ib.setText(command.args.get(0).replaceAll("/", " "));

                            sendMsg.sendInlineMessages(ChatConfig.messageRecipientId, message.getReplyToMessage().getText(), new Object[][]{{ib}});
                            //sendMsg.sendInlineMessages(ChatConfig.getChannelId(), message.getReplyToMessage().getText(), new Object[][]{{ib}});

                        } else if (message.getReplyToMessage() != null) {
                            sendMsg.sendHTMLMessage(ChatConfig.getAdminId(), message.getReplyToMessage().getText());
                            sendMsg.sendHTMLMessage(ChatConfig.getAdminId(), "/sendmessage ButtonText Link");
                        } else {
                            sendMsg.sendTextMessage(ChatConfig.getAdminId(), "\u0030\u20E3 \u0031\u20E3 \u0032\u20E3 \u0033\u20E3 \u0034\u20E3 \u0035\u20E3 \u0036\u20E3 \u0037\u20E3 \u0038\u20E3 \u0039\u20E3 пара за розкладом:\n" +

                                    "\n" +
                                    "<u>I підгрупа:</u>\n" +
                                    "<b>ТЕКСТ</b>\n" +
                                    "\n" +
                                    "<u>II підгрупа:</u>\n" +
                                    "<b>ТЕКСТ</b>\n" +
                                    "\n" +
                                    "<b><u>Посилання на Google Meet нижче..(якщо не переходить - значить відсутнє посилання)</u></b>");

                            sendMsg.sendTextMessage(ChatConfig.getAdminId(), "\u0030\u20E3 \u0031\u20E3 \u0032\u20E3 \u0033\u20E3 \u0034\u20E3 \u0035\u20E3 \u0036\u20E3 \u0037\u20E3 \u0038\u20E3 \u0039\u20E3 пара за розкладом <u>(спільна пара):</u>\n" +
                                    "<b>ТЕКСТ</b>");
                        }
                        break;
                    }*/
                    case "/help": {
                        String str = "* \nList of commands: *\n";
                        str += "`/switchchat` `          \\- змінити чат\\(канал/особисті\\) \n`";
                        str += "`/switchschedule` `      \\- ввімкнути/вимкнути надсилання розкладу \n`";
                        str += "`/switchdebug` `         \\- ввімкнути/вимкнути режим відладки \n`";
                        str += "`/settings` `            \\- поточні налаштування \n`";
                        str += "`/getLesson` ` \\{\\}        \\- \\(debug\\) перевірити розклад на день (день + {}) \n`";
                        str += "`/getLesson` ` \\{\\} \\{\\} \\{\\}  \\- \\(debug\\) перевірити розклад на конкретну пару (деньТижня, номерТижня, номерПари)\n`";
                        str += "`/sendmessage` `         \\- отримати шаблони по відправці повідомлень \n`";
                        str += "`/sendmessage` `         \\- відображає відмічене повідомлення в форматованому вигляді \n`";
                        str += "`/sendmessage` ` \\{\\} \\{\\}   \\- надсилає відмічене повідомлення з html форматуванням з кнопкою \n`";

                        sendMsg.sendMarkupV2Message(ChatConfig.getAdminId(), str);

                        break;
                    }
                    default: {
                        sendMsg.sendTextMessage(ChatConfig.getAdminId(), "I don't know this command.");

                    }

                }
            }
            switch (command.getCommand()) {
                case "/start":
                case "/help": {
                    String str = "* \nСписок команд: *\n";
                    str += "`/розклад` ` \\- отримати розклад на конкретний день \n`";


                    sendMsg.sendMarkupV2Message(update.getMessage().getChatId().toString(), str);

                    break;
                }
                case "/розклад": {
                    if (command.getArgs().size() == 2) {
                        String[] date;
                        int day, month;
                        ZonedDateTime zdt;
                        Group group;
                        try {
                            date = command.getArgs().get(1).split("\\.");
                            if (date.length > 2){
                                throw new Exception();
                            }
                            day = Integer.parseInt(date[0]);
                            month = Integer.parseInt(date[1]);
                            zdt = ZonedDateTime.of(2022, month, day, 5, 0, 0, 0, ZoneId.of("Europe/Kiev"));
                        } catch (Exception e) {
                            sendMsg.sendHTMLMessage(update.getMessage().getChatId().toString(), "Помилка при введені команди, спробуй /розклад");
                            return;
                        }

                        try {
                            group = GroupController.getGroupByName(command.getArgs().get(0));
                            if (group == null){
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            sendMsg.sendHTMLMessage(update.getMessage().getChatId().toString(), "Групи з таким іменем не існує.");
                            return;
                        }


                        if (zdt.getDayOfWeek().equals(DayOfWeek.SUNDAY) ||
                                (zdt.isBefore(ZonedDateTime.of(2022, 8, 22, 0, 0, 0, 0, ZoneId.of("Europe/Kiev")))) ||
                                (zdt.isAfter(ZonedDateTime.of(2022, 11, 30, 23, 59, 0, 0, ZoneId.of("Europe/Kiev"))))) {
                            sendMsg.sendHTMLMessage(update.getMessage().getChatId().toString(), "В обраний день пари не проводяться та/або навчання не почалось/закінчилось.");
                        } else {
                            try {
                                String formatMsg = ScheduleController.getMessageForStartOfNewDay(group, zdt);
                                new SendMessage().sendHTMLMessage(update.getMessage().getChatId().toString(), formatMsg);
                            } catch (MissingMessageExceptions e) {
                                new SendMessage().sendTextMessage(ChatConfig.getAdminId(), e.getMessage());
                                logger.warn("Error when sending message for command /розклад. {}", command);
                            } catch (Exception ee){
                                logger.error("Error when sending message for command /розклад. {}", command, ee);
                            }
                        }
                    } else {
                        String formatMsg = "Для того, щоб отримати розклад на конкретний день введіть\\:\n";
                        formatMsg += "`/розклад` `назва групи` `день\\.місяць`\n";
                        formatMsg += "*Приклад\\:*\n";
                        formatMsg += "`/розклад БІ-244Б 27\\.08`";

                        sendMsg.sendMarkupV2Message(update.getMessage().getChatId().toString(), formatMsg);
                    }
                    break;
                }

                default: {
                    sendMsg.sendHTMLMessage(update.getMessage().getChatId().toString(), "Мене такому не навчили\uD83D\uDE4A\nСпробуй /help");
                }

            }
        } else {
            sendMsg.sendHTMLMessage(update.getMessage().getChatId().toString(), "В цьому житті я вмію виконувати лише команди\uD83D\uDE2D\uD83D\uDE2D\uD83D\uDE2D\nСпробуй /help");
        }
    }
}
