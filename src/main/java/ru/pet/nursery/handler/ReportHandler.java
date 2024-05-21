package ru.pet.nursery.handler;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import ru.pet.nursery.data.MessageData;
import ru.pet.nursery.manager.report.ReportManager;
import java.io.IOException;

import static ru.pet.nursery.data.MessageData.*;

@Service
public class ReportHandler {
    private final ReportManager reportManager;
    public ReportHandler(ReportManager reportManager){
        this.reportManager = reportManager;
    }
    public void answer(Update update) throws IOException {
        long chatId = update.message().chat().id();
        String reportStatus = MessageData.chatId_reportStatus.get(chatId);
        switch (reportStatus){
            case PHOTO_STATUS -> {
                reportManager.uploadPhotoToReport(update);
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
        MessageData.removeFromChatId_reportStatusMap(chatId);
    }
}
