package ru.pet.nursery.data;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageData {
    public static final String ERROR_MESSAGE = "error_message";
    public final Map<Long, String> chatIdReportStatus = new HashMap<>();


    public void putReportStatusByChatId(long chatId, String reportStatus){
        chatIdReportStatus.put(chatId, reportStatus);
    }

    public void removeChatId(long chatId){
        chatIdReportStatus.remove(chatId);
    }
}
