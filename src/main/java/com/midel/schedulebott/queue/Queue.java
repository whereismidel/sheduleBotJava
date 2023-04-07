package com.midel.schedulebott.queue;

import org.javatuples.Pair;

import java.util.*;

import static com.midel.schedulebott.queue.QueueController.userQueueToText;

public class Queue {
    private String chatId;
    private String messageId;
    private String queueId;
    private String title;
    private LinkedHashMap<Integer, Pair<String, String>> userQueue;
    private Boolean status;

    public Queue(String chatId, String messageId,
                 String title, LinkedHashMap<Integer, Pair<String, String>> userQueue,
                 Boolean status)
    {
        this.chatId = chatId;
        this.messageId = messageId;
        this.queueId = (chatId != null && messageId != null)? chatId+"/"+messageId : null;
        this.title = title;
        this.userQueue = userQueue;
        this.status = status;
    }

    public void copy(Queue queue) {
        this.chatId = queue.chatId;
        this.messageId = queue.messageId;
        this.queueId = queue.queueId;
        this.title = queue.title;
        this.status = queue.status;

        this.userQueue.clear();
        for(Map.Entry<Integer, Pair<String, String>> copy : queue.userQueue.entrySet()){
            this.userQueue.put(copy.getKey(), new Pair<>(copy.getValue().getValue0(), copy.getValue().getValue1()));
        }
    }

    public List<Object> toList() {
        return new ArrayList<>(Arrays.asList(
                chatId == null ? "null" : chatId,
                messageId == null ? "null" : messageId,
                title == null ? "null" : title,
                userQueue == null || userQueue.isEmpty()? "null" : userQueueToText(userQueue),
                status == null ? "null" : status ? "active" : "stopped"
        ));
    }

    @Override
    public String toString() {
        return "Queue{" +
                "\nchatId=" + chatId +
                "\nmessageId=" + messageId +
                "\ntitle=" + title +
                "\nuserQueue=" + userQueue +
                "\nstatus=" + status +
                "\n}";
    }

    public String toFormatString() {
        String format = "%s. <a href=\"tg://user?id=%s\">%s</a>\n";

        StringBuilder result = new StringBuilder();
        result.append("<b>").append(title).append("</b>\n\n");
        if (userQueue != null){
            for (Map.Entry<Integer, Pair<String, String>> user : userQueue.entrySet()){
                result.append(String.format(format, user.getKey(), user.getValue().getValue1(), user.getValue().getValue0()));
            }
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Queue queue = (Queue) o;
        return Objects.equals(chatId, queue.chatId) && Objects.equals(messageId, queue.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, messageId);
    }

    public String getChatId() {
        return chatId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public String getTitle() {
        return title;
    }

    public LinkedHashMap<Integer, Pair<String, String>> getUserQueue() {
        return userQueue;
    }

    public void setUserQueue(LinkedHashMap<Integer, Pair<String, String>> userQueue) {
        this.userQueue = userQueue;
    }

}
