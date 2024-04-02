package com.midel.queue;

import org.javatuples.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueueController {

    public static Queue getQueueById(String queueId) {

        for(Queue queue : QueueRepo.queues.values()){
            if (queue.getQueueId().equals(queueId)){
                return queue;
            }
        }
        return null;
    }

    public static boolean swapUserPos(Queue queue, Integer pos1, Integer pos2){

        Pair<String, String> user = queue.getUserQueue().replace(pos1, queue.getUserQueue().get(pos2));
        return user != null && queue.getUserQueue().replace(pos2, user) != null;

    }
    public static Integer getUserPos(Queue queue, String userId){
        Integer pos = -1;
        for (Map.Entry<Integer, Pair<String, String>> e : queue.getUserQueue().entrySet()) {
            if (e.getValue().getValue1().equals(userId)) {
                pos = e.getKey();
                break;
            }
        }

        return pos;
    }
    public static boolean addUserToQueue(Queue queue, String userName, String userId, int pos){
        Pair<String, String> user = new Pair<>(userName, userId);

        boolean containsUser = false;
        for (Map.Entry<Integer, Pair<String, String>> e : queue.getUserQueue().entrySet()) {
            if (e.getValue().getValue1().equals(userId)) {
                containsUser = true;
                break;
            }
        }

        if (!containsUser){
            if (pos != 0 && pos <= queue.getUserQueue().size()){
                queue.getUserQueue().put(queue.getUserQueue().size()+1, queue.getUserQueue().get(queue.getUserQueue().size()));
                for (int i = queue.getUserQueue().size()-1; i >= pos; i--) {
                    Pair<String, String> tempUser = queue.getUserQueue().replace(i, queue.getUserQueue().get(i-1));
                }
                queue.getUserQueue().replace(pos, user);
            } else {
                queue.getUserQueue().put(queue.getUserQueue().size() + 1, user);
            }
            return true;
        } else {
            return false;
        }
    }

    public static Pair<Boolean, Integer> removeUserFromQueue(Queue queue, String userId){
        LinkedHashMap<Integer, Pair<String, String>> newUserList = new LinkedHashMap<>();

        boolean containsUser = false;
        for (Map.Entry<Integer, Pair<String, String>> e : queue.getUserQueue().entrySet()) {
            if (e.getValue().getValue1().equals(userId)) {
                containsUser = true;
                break;
            }
        }

        if (containsUser){
            Integer ind = 1;
            Integer removeInd = -1;
            for(Map.Entry<Integer, Pair<String, String>> u : queue.getUserQueue().entrySet()){
                if (!u.getValue().getValue1().equals(userId)){
                    newUserList.put(ind, u.getValue());
                    ind++;
                } else {
                    removeInd = u.getKey();
                }
            }
            queue.setUserQueue(newUserList);

            return new Pair<>(true, removeInd);
        } else {
            return new Pair<>(false, -1);
        }
    }

    /*
        1.(username)-id / 2.(username)-id / 3.(username)-id
    */
    public static LinkedHashMap<Integer, Pair<String, String>> textToUserQueue(String stringQueue) {
        LinkedHashMap<Integer, Pair<String, String>> queue = new LinkedHashMap<>();


        String[] users = stringQueue.split(" / ");
        for(String user : users){
            int num = Integer.parseInt(user.substring(0, user.indexOf(".")));
            String username = user
                    .substring(user.indexOf("(")+1, user.indexOf(")"));

            String userId = user.replace(num+".("+username+")-", "");

            username = username.replace("&_&", " ");

            queue.put(num, new Pair<>(username, userId));
        }

        return queue;
    }

    public static String userQueueToText(LinkedHashMap<Integer, Pair<String, String>> userQueue){
        StringBuilder result = new StringBuilder();

        for(Map.Entry<Integer, Pair<String, String>> user : userQueue.entrySet()){
            String format = "%s.(%s)-%s / ";

            result.append(
                    String.format(format,
                            user.getKey(),
                            user.getValue().getValue0().replace(" ", "&_&"),
                            user.getValue().getValue1())
            );
        }

        return result.replace(result.lastIndexOf(" / "), result.length(), "").toString();
    }
}
