package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.VolunteerRepo;

import java.util.List;
import java.util.function.BiConsumer;

public class VolunteersAction implements BiConsumer<Update, TelegramBot> {
   private final VolunteerRepo volunteerRepo;
   public VolunteersAction(VolunteerRepo volunteerRepo){
       this.volunteerRepo = volunteerRepo;
   }
    @Override
    public void accept(Update update, TelegramBot telegramBot) {
        List<Volunteer> list = volunteerRepo.findAll();
        StringBuilder sb = new StringBuilder();
        list.stream().filter(volunteer -> volunteer.isActive()).forEach(volunteer -> sb.append(volunteer.getName() + " ").append(volunteer.getPhoneNumber() + "\n"));
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), sb.toString());
        telegramBot.execute(sendMessage);
    }
}
