package com.midel.queue;

import com.midel.config.DBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static com.midel.google.SheetAPI.readSheetForRange;
import static com.midel.google.SheetAPI.updateValues;


public class QueueRepo {

    public static final LinkedHashMap<String, Queue> queues = new LinkedHashMap<>();
    static final Logger logger = LoggerFactory.getLogger(QueueRepo.class);

    public static boolean importQueuesList(){
        try {
            List<List<Object>> valuesFromQueues = readSheetForRange(DBConfig.adminPanelInfoSheet, DBConfig.queuesListRange);

            if (valuesFromQueues == null || valuesFromQueues.isEmpty()) {
                logger.warn("No data found in \"Черги\"");
                queues.clear();
            } else {
                for (List<Object> rowQueue : valuesFromQueues) {
                    Queue queue = new Queue(
                            rowQueue.get(0).toString().equals("null") ? null : rowQueue.get(0).toString(),
                            rowQueue.get(1).toString().equals("null") ? null : rowQueue.get(1).toString(),
                            rowQueue.get(2).toString().equals("null") ? null : rowQueue.get(2).toString(),
                            rowQueue.get(3).toString().equals("null") ? new LinkedHashMap<>() : QueueController.textToUserQueue(rowQueue.get(3).toString()),
                            rowQueue.get(4).toString().equals("null") ? null : rowQueue.get(4).toString().equals("active")
                    );

                    if (queues.containsKey(queue.getQueueId())){
                        queues.get(queue.getQueueId()).copy(queue);
                    } else {
                        queues.put(queue.getQueueId(), queue);
                    }
                }
                logger.info("Successful importing of the table \"Черги\"");
                logger.trace("Queues list: \n{}", queues);
            }

            return true;
        } catch (Exception e) {
            logger.error("Failed to import table \"Черги\"", e);
            return false;
        }
    }

    public static boolean exportQueuesList(){

        List<List<Object>> queuesToExport = new ArrayList<>();
        for (Queue queue: queues.values()) {
            queuesToExport.add(queue.toList());
        }
        queuesToExport.add(new ArrayList<>(Collections.nCopies((int) DBConfig.queuesEdge - 64, "")));

        if (updateValues(DBConfig.adminPanelInfoSheet, DBConfig.queuesListRange, queuesToExport)){
            logger.info("Successful export of the table \"Черги\".");
            return true;
        } else {
            logger.error("Error when exporting table \"Черги\".");
            return false;
        }

    }

    public static boolean addQueueToList(Queue queue) {
        try {
            if (queue.getQueueId() == null || queues.containsKey(queue.getQueueId())){
                return false;
            } else {
                queues.put(queue.getQueueId(), queue);

                if (QueueRepo.exportQueuesList()){
                    logger.info("Successfully added a student to the table \"Черги\".");
                } else {
                    queues.remove(queue.getQueueId());
                    throw new Exception();
                }

                return true;
            }
        } catch (Exception e){
            logger.error("Failed to add and update the table \"Черги\" = {}", queue, e);
            return false;
        }
    }
}
