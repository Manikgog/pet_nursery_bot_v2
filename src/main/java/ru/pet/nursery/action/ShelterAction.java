package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.mapper.ShelterMapper;
import ru.pet.nursery.repository.ShelterRepo;
import ru.pet.nursery.web.dto.ShelterDTO;


import java.util.List;
import java.util.function.BiConsumer;

public class ShelterAction implements BiConsumer<Update, TelegramBot> {

    private final ShelterRepo shelterRepo;

    public ShelterAction(ShelterRepo shelterRepo) {
        this.shelterRepo = shelterRepo;
    }

    @Override
    public void accept(Update update, TelegramBot telegramBot) {
        List<Nursery> nurseryList = shelterRepo.findAll();
        StringBuilder builder = new StringBuilder();
        for (Nursery item : nurseryList) {
            ShelterDTO shelterDTO = new ShelterMapper().perform(item);
            builder.append(shelterDTO.toTelegramString()).append("\n--------------------\n");
        }
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), builder.toString());
        telegramBot.execute(sendMessage);
    }
}
