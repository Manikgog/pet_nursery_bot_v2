package ru.pet.nursery.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.info.InfoManager;
import ru.pet.nursery.manager.report.ReportManager;
import ru.pet.nursery.manager.start.StartManager;
import ru.pet.nursery.manager.volunteer.VolunteerManager;

import static ru.pet.nursery.data.Command.*;

@Service
public class CommandHandler {
    private final InfoManager infoManager;
    private final ReportManager reportManager;
    private final VolunteerManager volunteerManager;
    private final StartManager startManager;
    private final TelegramBot telegramBot;

    public CommandHandler(InfoManager infoManager,
                          ReportManager reportManager,
                          VolunteerManager volunteerManager,
                          StartManager startManager,
                          TelegramBot telegramBot) {
        this.infoManager = infoManager;
        this.reportManager = reportManager;
        this.volunteerManager = volunteerManager;
        this.startManager = startManager;
        this.telegramBot = telegramBot;
    }

    public void answer(Update update){
        String command = update.message().text();
        switch (command){
            case START_COMMAND -> startManager.answerCommand(update);
            case INFO_COMMAND -> infoManager.answerCommand(update);
            case REPORT_COMMAND -> reportManager.answerCommand(update);
            case VOLUNTEER_COMMAND -> volunteerManager.answerCommand(update);
            default -> defaultAnswer(update);
        }
    }

    private void defaultAnswer(Update update) {
        String answerMessage = """
                Неподдерживаемая команда
                """;
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), answerMessage);
        telegramBot.execute(sendMessage);
    }

}
