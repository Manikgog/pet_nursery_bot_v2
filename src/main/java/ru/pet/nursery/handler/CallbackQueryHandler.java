package ru.pet.nursery.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import ru.pet.nursery.manager.get.GetManager;
import ru.pet.nursery.manager.info.InfoManager;
import ru.pet.nursery.manager.report.ReportManager;
import ru.pet.nursery.manager.volunteer.VolunteerManager;

import static ru.pet.nursery.data.CallbackData.*;
@Service
public class CallbackQueryHandler {
    private final GetManager getManager;
    private final InfoManager infoManager;
    private final ReportManager reportManager;
    private final VolunteerManager volunteerManager;
    private final TelegramBot telegramBot;

    public CallbackQueryHandler(GetManager getManager,
                                InfoManager infoManager,
                                ReportManager reportManager,
                                VolunteerManager volunteerManager,
                                TelegramBot telegramBot) {
        this.getManager = getManager;
        this.infoManager = infoManager;
        this.reportManager = reportManager;
        this.volunteerManager = volunteerManager;
        this.telegramBot = telegramBot;
    }

    public void answer(Update update){
        String callbackData = update.callbackQuery().data();

        switch (callbackData){
            case INFO -> {
                infoManager.answerCallbackQuery(update.callbackQuery());
            }
            case GET -> {
                getManager.answerCallbackQuery(update.callbackQuery());
            }
            case REPORT -> {
                reportManager.answerCallbackQuery(update.callbackQuery());
            }
            case VOLUNTEER -> {
                volunteerManager.answerCallbackQuery(update.callbackQuery());
            }
            case INSTRUCTION -> {
                reportManager.answerInstruction(update.callbackQuery());
            }
            case HEALTH -> {
                reportManager.answerHealth(update.callbackQuery());
            }
            case BEHAVIOUR -> {
                reportManager.answerBehaviour(update.callbackQuery());
            }
            case FOTO -> {
                reportManager.answerFoto(update.callbackQuery());
            }
            case DIET -> {
                reportManager.answerDiet(update.callbackQuery());
            }
            case BACK_TO_REPORT_MENU -> {
                reportManager.answerCallbackQuery(update.callbackQuery());
            }
            default -> {
                defaultAnswer(update.callbackQuery());
            }
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
