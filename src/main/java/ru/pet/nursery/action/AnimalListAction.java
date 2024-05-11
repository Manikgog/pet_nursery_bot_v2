package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.mapper.AnimalMapper;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.web.dto.AnimalDTO;

import java.util.ArrayList;
import java.util.List;

public class AnimalListAction implements Action{
    private final AnimalRepo animalRepo;

    public AnimalListAction(AnimalRepo animalRepo) {
        this.animalRepo = animalRepo;
    }

    @Override
    public void handle(Update update, TelegramBot bot) {

    }

    @Override
    public void callback(Update update, TelegramBot bot) {
        List<Animal> animalList = animalRepo.findAll();
        List<AnimalDTO> animalDTOS = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(Animal item : animalList) {
            AnimalDTO animalDTO = new AnimalMapper().perform(item);
            sb.append(animalDTO.toTelegramString()).append("\n------------------\n");
        }
        SendMessage sendMessage = new SendMessage(
                update.message().chat().id(),
                sb.toString());
        bot.execute(sendMessage);
    }
}
