package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.VolunteerRepo;

import java.util.List;

@Service
public class VolunteersPhoneAction implements Action {

   private final VolunteerRepo volunteerRepo;
   public VolunteersPhoneAction(VolunteerRepo volunteerRepo){
       this.volunteerRepo = volunteerRepo;
   }
    public void accept(Update update, TelegramBot telegramBot) {
        List<Volunteer> list = volunteerRepo.findAll();
        StringBuilder sb = new StringBuilder();
        list.stream().filter(volunteer -> volunteer.isActive()).forEach(volunteer -> sb.append(volunteer.getName()).append(" ").append(volunteer.getPhoneNumber()).append("\n"));
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), sb.toString());
        telegramBot.execute(sendMessage);
    }

    @Override
    public void handle(Update update, TelegramBot bot) {

    }

    /**
     * Метод для отправки сообщения с контактами активного волонтера,
     * которые достаются из базы данных
     * @param update - объект класса Update
     * @param bot - объект класса TelegramBot
     */
    @Override
    public void callback(Update update, TelegramBot bot) {
        List<Volunteer> list = volunteerRepo.findAll();
        StringBuilder sb = new StringBuilder();
        list.stream().filter(volunteer -> volunteer.isActive()).forEach(volunteer -> sb.append(volunteer.getName()).append(" ").append(volunteer.getPhoneNumber()).append("\n"));
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), sb.toString());
        bot.execute(sendMessage);
    }
}
