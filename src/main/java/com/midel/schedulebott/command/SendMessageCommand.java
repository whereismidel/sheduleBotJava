package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.AdminCommand;
import com.midel.schedulebott.group.Group;
import com.midel.schedulebott.telegram.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.TimeUnit;

import static com.midel.schedulebott.command.CommandName.NO;
import static com.midel.schedulebott.group.GroupController.getGroupByName;
import static com.midel.schedulebott.group.GroupRepo.groups;

/**
 * SendMessage {@link Command}.
 */
@AdminCommand
public class SendMessageCommand extends Command{

    static final Logger logger = LoggerFactory.getLogger(SendMessageCommand.class);

    public final static String INVALID_ARGUMENT_MESSAGE = "Невірно вказані аргументи або їх кількість\\.\n"
            +"`/sendMessage` \\- для довідки\\.";
    public final static String INVALID_REPLY_MESSAGE = "Не відмічено повідомлення для відправки\\.\n"
            +"`/sendMessage` \\- для довідки\\.";
    public final static String INVALID_IDENTIFIER_MESSAGE = "Невірно вказано ідентифікатор\\.\n"
            +"`/sendMessage` \\- для довідки\\.";
    public final static String INFO_MESSAGE = "Відправка відміченного повідомлення\\(replyMessage\\) у відповідні чати\\:\n"
            +"`/sendMessage` `ідентифікатор отримувача`\n\n"
            +"Ідентифікатори\\: \n"
            +" ALL\\_CHANNEL \\- відправка по ВСІМ каналам\\.\n"
            +" ALL\\_ADMINS \\- відправка всім адмінам\\.\n"
            +" ADMIN\\=\\{group\\_name\\} \\- відправка адміну відповідної групи\\.\n"
            +" CHANNEL\\=\\{group\\_name\\} \\- відправка на канал відповідної групи\\.";

    public final static String GROUP_NOT_FOUND_MESSAGE = "Групи з такою назвою не знайдено.";

    public SendMessageCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        String userId = update.getMessage().getChat().getId().toString();
        Message replyMessage = update.getMessage().getReplyToMessage();

        if (arguments != null) {
            if (arguments.size() == 0) {
                sendMessage.sendMarkupV2Message(userId, INFO_MESSAGE);
            } else if (replyMessage == null){
                sendMessage.sendMarkupV2Message(userId, INVALID_REPLY_MESSAGE);
            } else if (arguments.size() != 1){
                sendMessage.sendMarkupV2Message(userId, INVALID_ARGUMENT_MESSAGE);
            } else{
                String identifier = arguments.get(0);

                switch (identifier){
                    case "ALL_CHANNEL":{
                        for(Group group : groups.values()){
                            try {
                                sendMessage.sendTextWithEntitiesMessage(group.getChannelId(), replyMessage.getText(), replyMessage.getEntities());
                            } catch (Exception e){
                                logger.error("Failed to send message to {} CHANNEL via '/sendMessage' command.", group.getGroupName(), e);
                            }

                            try {
                                TimeUnit.MILLISECONDS.sleep(500);
                            } catch (InterruptedException e) {
                                logger.warn("Failed to set delay for sending for '/sendMessage' command.", e);
                            }
                        }
                        break;
                    }
                    case "ALL_ADMINS":{
                        for(Group group : groups.values()){
                            try {
                                sendMessage.sendTextWithEntitiesMessage(group.getLeaderId(), replyMessage.getText(), replyMessage.getEntities());
                            } catch (Exception e){
                                logger.error("Failed to send message to {} ADMIN via '/sendMessage' command.", group.getGroupName(), e);
                            }

                            try {
                                TimeUnit.MILLISECONDS.sleep(500);
                            } catch (InterruptedException e) {
                                logger.warn("Failed to set delay for sending for '/sendMessage' command.", e);
                            }

                        }
                        break;
                    }
                    default:{

                        if (identifier.contains("ADMIN=")){
                            Group group = getGroupByName(identifier.substring("ADMIN=".length()));
                            if (group == null){
                                sendMessage.sendHTMLMessage(userId, GROUP_NOT_FOUND_MESSAGE);
                                return;
                            }
                            try {
                                sendMessage.sendTextWithEntitiesMessage(group.getLeaderId(),
                                                                        replyMessage.getText(),
                                                                        replyMessage.getEntities());
                            } catch (Exception e){
                                logger.error("Failed to send message to {} ADMIN via '/sendMessage' command.", group.getGroupName(), e);
                            }
                        } else if (arguments.get(0).contains("CHANNEL=")){
                            Group group = getGroupByName(identifier.substring("CHANNEL=".length()));
                            if (group == null){
                                sendMessage.sendHTMLMessage(userId, GROUP_NOT_FOUND_MESSAGE);
                                return;
                            }
                            try {
                                sendMessage.sendTextWithEntitiesMessage(group.getChannelId(),
                                                                        replyMessage.getText(),
                                                                        replyMessage.getEntities());
                            } catch (Exception e){
                                logger.error("Failed to send message to {} CHANNEL via '/sendMessage' command.", group.getGroupName(), e);
                            }
                        } else {
                            sendMessage.sendMarkupV2Message(userId, INVALID_IDENTIFIER_MESSAGE);
                        }
                    }
                }
            }
        } else {
            sendMessage.sendHTMLMessage(userId, NO.getCommandName());
        }
    }
}