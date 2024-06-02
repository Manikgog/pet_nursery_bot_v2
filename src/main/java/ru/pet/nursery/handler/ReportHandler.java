package ru.pet.nursery.handler;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.pet.nursery.data.MessageData;
import ru.pet.nursery.manager.report.ReportManager;
import java.io.IOException;
import java.util.Arrays;

import static ru.pet.nursery.data.ReportStatus.*;

@Service
public class ReportHandler {
    private final Logger logger = LoggerFactory.getLogger(CallbackQueryHandler.class);
    private final ReportManager reportManager;
    private final MessageData messageData;
    public ReportHandler(ReportManager reportManager,
                         MessageData messageData){
        this.reportManager = reportManager;
        this.messageData = messageData;
    }

    public void answer(Update update) {
        long chatId = update.message().chat().id();
        String status = messageData.chatIdReportStatus.get(chatId);

        switch (status){
            case PHOTO_STATUS -> {
                try {
                    reportManager.uploadPhotoToReport(update);
                }catch (IOException e){
                    StringBuilder sb = new StringBuilder();
                    sb.append(e.getMessage()).append(e.getCause()).append(Arrays.toString(e.getStackTrace()));
                    logger.error(sb.toString());
                    throw new RuntimeException(sb.toString());
                }
            }
            case DIET_STATUS -> {
                reportManager.uploadDietToReport(update);
            }
            case HEALTH_STATUS -> {
                reportManager.uploadHealthToReport(update);
            }
            case BEHAVIOUR_STATUS -> {
                reportManager.uploadBehaviourToReport(update);
            }
        }
        messageData.removeChatId(chatId);
    }
}
