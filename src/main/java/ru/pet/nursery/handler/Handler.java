package ru.pet.nursery.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import ru.pet.nursery.action.*;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.VolunteerRepo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Handler {

    private final VolunteerRepo volunteerRepo;
    private final AnimalRepo animalRepo;
    // карта пар идентификатор чата -> команда, которую отправил пользователь в прошлом запросе
    private final Map<String, String> bindingBy = new ConcurrentHashMap<>();

    public Handler(VolunteerRepo volunteerRepo,
                   AnimalRepo animalRepo){
        this.volunteerRepo = volunteerRepo;
        this.animalRepo = animalRepo;
    }
    public void answer(Update update, TelegramBot bot) {
        String key = update.message().text();
        String chatId = update.message().chat().id().toString();
        switch (key){
            case "/start" -> new StartAction();
            case "/info" -> new InfoAction();
            case "/contacts" -> new ContactsAction();
            case "/volunteer" -> {
                new VolunteerMenuAction().accept(update, bot);
                bindingBy.put(chatId, key);                     // запись пары chatId - команда (/start, /new, /info ...)
                return;
            }
        }
        if(bindingBy.containsKey(chatId)) {
            switch (update.message().text()) {
                case "/phone" -> new VolunteersPhoneAction(volunteerRepo).callback(update, bot);
                case "/list" -> new AnimalListAction(animalRepo).callback(update, bot);
            }
            bindingBy.remove(chatId);                            // удаляется идентификатор т.к. команда обработана
        }

    }
}
