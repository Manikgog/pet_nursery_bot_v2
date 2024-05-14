package ru.pet.nursery.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import ru.pet.nursery.action.*;
import ru.pet.nursery.repository.VolunteerRepo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Handler {
    private final VolunteerRepo volunteerRepo;
    private final Map<String, Action> actions;
    // карта пар идентификатор чата -> команда, которую отправил пользователь в прошлом запросе
    private final Map<String, String> bindingBy = new ConcurrentHashMap<>();

    public Handler(VolunteerRepo volunteerRepo, Map<String, Action> actions){
        this.volunteerRepo = volunteerRepo;
        this.actions = actions;
    }

    /**
     * Метод для сортировки запросов
     * @param update - объект класса Update
     * @param bot - объект класса TelegramBot
     */
    public void answer(Update update, TelegramBot bot) {
        String key = update.message().text();
        String chatId = update.message().chat().id().toString();
        switch (key){
            case "/start" -> new StartAction();
            case "/info" -> new InfoAction();
            case "/contacts" -> new ContactsAction();
            //case "/volunteer" -> new VolunteersAction(volunteerRepo).accept(update, bot);
        }
        /*if (actions.containsKey(key)) {
            actions.get(key).handle(update, bot);   // выполняется ответ пользователю в котором указывается, что надо ввести
            bindingBy.put(chatId, key);             // запись пары chatId - команда (/start, /new, /info ...)
        } else if (bindingBy.containsKey(chatId)) {
            actions.get(bindingBy.get(chatId)).callback(update, bot);   // проверяется ответ пользователя
            bindingBy.remove(chatId);                                   // удаляется идентификатор т.к. команда обработана
        }*/
    }
}
