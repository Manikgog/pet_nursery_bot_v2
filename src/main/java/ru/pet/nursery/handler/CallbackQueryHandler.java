package ru.pet.nursery.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import ru.pet.nursery.manager.info.InfoManager;
import ru.pet.nursery.manager.report.ReportManager;
import ru.pet.nursery.manager.start.StartManager;
import ru.pet.nursery.manager.volunteer.VolunteerManager;

import java.io.IOException;

import static ru.pet.nursery.data.CallbackData.*;
@Service
public class CallbackQueryHandler {
    private final InfoManager infoManager;
    private final ReportManager reportManager;
    private final StartManager startManager;
    private final VolunteerManager volunteerManager;
    private final TelegramBot telegramBot;

    public CallbackQueryHandler(InfoManager infoManager,
                                ReportManager reportManager,
                                StartManager startManager,
                                VolunteerManager volunteerManager,
                                TelegramBot telegramBot) {
        this.infoManager = infoManager;
        this.reportManager = reportManager;
        this.startManager = startManager;
        this.volunteerManager = volunteerManager;
        this.telegramBot = telegramBot;
    }

    public void answer(Update update) throws IOException {
        String callbackData = update.callbackQuery().data();

        switch (callbackData){
            case INFO -> infoManager.answerCallbackQuery(update.callbackQuery());
            case REPORT -> reportManager.answerCallbackQuery(update.callbackQuery());
            case VOLUNTEER -> volunteerManager.answerCallbackQuery(update.callbackQuery());
            case INSTRUCTION -> reportManager.answerInstruction(update.callbackQuery());
            case HEALTH -> reportManager.answerHealth(update.callbackQuery());
            case BEHAVIOUR -> reportManager.answerBehaviour(update.callbackQuery());
            case FOTO -> reportManager.answerFoto(update.callbackQuery());
            case DIET -> reportManager.answerDiet(update.callbackQuery());
            case BACK_TO_REPORT_MENU -> reportManager.answerCallbackQuery(update.callbackQuery());
            case ADDRESS_AND_PHONE -> infoManager.addressAndPhoneNursery(update.callbackQuery());
            case PET_INFORMATION -> infoManager.petInformation(update.callbackQuery());
            case WHAT_NEED_FOR_ADOPTION -> infoManager.whatNeedForAdoption(update.callbackQuery());
            case DOGS -> infoManager.dogsInformation(update.callbackQuery());
            case CATS -> infoManager.catsInformation(update.callbackQuery());
            case CAT_PHOTO -> infoManager.catPhoto(update.callbackQuery());
            case DOG_PHOTO -> infoManager.dogPhoto(update.callbackQuery());
            case CAT_INFORMATION -> infoManager.catInformation(update.callbackQuery());
            case DOG_INFORMATION -> infoManager.dogInformation(update.callbackQuery());
            case START -> startManager.answerCallbackQuery(update.callbackQuery());
            default -> defaultAnswer(update.callbackQuery());
        }
    }

    private void defaultAnswer(CallbackQuery callbackQuery) {
        String answerMessage = """
                Неподдерживаемая команда
                """;
        SendMessage sendMessage = new SendMessage(callbackQuery.message().chat().id(), answerMessage);
        telegramBot.execute(sendMessage);
    }
}
