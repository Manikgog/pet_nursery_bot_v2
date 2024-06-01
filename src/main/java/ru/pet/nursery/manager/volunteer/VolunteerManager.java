package ru.pet.nursery.manager.volunteer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;
import ru.pet.nursery.repository.VolunteerRepo;
import ru.pet.nursery.web.service.VolunteerService;

import java.util.List;

import static ru.pet.nursery.data.CallbackData.*;
import static ru.pet.nursery.data.CallbackData.START;

@Component
public class VolunteerManager extends AbstractManager {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final VolunteerRepo volunteerRepo;
    private final VolunteerService volunteerService;

    public VolunteerManager(AnswerMethodFactory answerMethodFactory,
                            KeyboardFactory keyboardFactory,
                            TelegramBot telegramBot,
                            VolunteerRepo volunteerRepo,
                            VolunteerService volunteerService) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.volunteerRepo = volunteerRepo;
        this.volunteerService = volunteerService;
    }

    /**
     * Метод для отправки меню для получения информации
     * @param update - объект класса Update
     */
    @Override
    public void answerCommand(Update update) {
        // связь с волонтером через телеграм
        String answerMessage = """
                  // Задайте вопрос волонтеру.
                """;
        String startMessageToVolunteer = """
                   Вопрос у пользователя
                """ + "@" + update.message().chat().username();
        Volunteer volunteer = volunteerRepo.getVolunteerIsActive();
        long userChatId = update.message().chat().id();
        SendMessage sendMessageToUser = answerMethodFactory.getSendMessage(
                userChatId,
                answerMessage,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(START)
                ));
        telegramBot.execute(sendMessageToUser);
        if (volunteer != null) {
            SendMessage sendMessageToVolunteer = answerMethodFactory.getSendMessage(
                    volunteer.getTelegramUserId(),
                    startMessageToVolunteer,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Закрыть чат"),
                            List.of(1),
                            List.of(CLOSE_CHAT)
                    ));
            volunteerService.updateStatus(false, volunteer.getId());
            telegramBot.execute(sendMessageToVolunteer);
        } else {
            String answerMessageWithoutVolunteer = """
                    Все волонтеры заняты. Вам ответит первый освободившийся работник.
                            """;
            SendMessage sendMessageWithoutVolunteer = answerMethodFactory.getSendMessage(
                    userChatId,
                    answerMessageWithoutVolunteer,
                    null);
            telegramBot.execute(sendMessageWithoutVolunteer);
        }
    }

    @Override
    public void answerMessage(Update update) {

    }
    /**
     * Метод для связи пользователя с волонтером
     * @param callbackQuery - запрос обратного вызова
     */
    @Override
    public void answerCallbackQuery(CallbackQuery callbackQuery) {
        // связь с волонтером через телеграм
        String answerMessage = """
                  Ожидайте, с Вами свяжется первый свободный волонтер нашего питомника.
                """;

        String startMessageToVolunteer = """
                   Вопрос у пользователя
                """ + "@" + callbackQuery.message().chat().username();

        long userChatId = callbackQuery.message().chat().id();
        SendMessage sendMessageToUser = answerMethodFactory.getSendMessage(
                userChatId,
                answerMessage,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(START)
                ));
        telegramBot.execute(sendMessageToUser);
        Volunteer volunteer = volunteerRepo.getVolunteerIsActive();
        if (volunteer != null) {
            SendMessage sendMessageToVolunteer = answerMethodFactory.getSendMessage(
                    volunteer.getTelegramUserId(),
                    startMessageToVolunteer,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Закрыть чат"),
                            List.of(1),
                            List.of(CLOSE_CHAT)
                    ));
            volunteerService.updateStatus(false, volunteer.getId());
            telegramBot.execute(sendMessageToVolunteer);
        } else {
            String answerMessageWithoutVolunteer = """
                    Все волонтеры заняты. Вам ответит первый освободившийся работник.
                            """;
            SendMessage sendMessageWithoutVolunteer = answerMethodFactory.getSendMessage(
                    userChatId,
                    answerMessageWithoutVolunteer,
                    null);
            telegramBot.execute(sendMessageWithoutVolunteer);
        }
    }
    /**
     * Метод для закрытия чата волонтером
     * @param callbackQuery - запрос обратного вызова
     */
    public void closeChat(CallbackQuery callbackQuery) {
        Volunteer volunteer = volunteerRepo.getVolunteerByTelegramUserId(callbackQuery.message().chat().id());
        volunteerService.updateStatus(true, volunteer.getId());
    }
}