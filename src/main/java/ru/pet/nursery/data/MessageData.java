package ru.pet.nursery.data;

import java.util.HashMap;
import java.util.Map;

public class MessageData {
    public static final Map<Long, String> chatId_reportStatus = new HashMap<>();

    public static void putToChatId_reportStatusMap(long chatId, String reportStatus){
        chatId_reportStatus.put(chatId, reportStatus);
    }

    public static void removeFromChatId_reportStatusMap(long chatId){
        chatId_reportStatus.remove(chatId);
    }
    public static final String PHOTO_STATUS = "photo_status";
    public static final String DIET_STATUS = "diet_status";
    public static final String HEALTH_STATUS = "health_status";
    public static final String BEHAVIOUR_STATUS = "behaviour_status";
}
