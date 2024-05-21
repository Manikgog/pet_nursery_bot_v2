package ru.pet.nursery.notifying;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pet.nursery.web.service.AdoptedService;


import java.util.concurrent.TimeUnit;

@Component
public class Notifier {
    private final TelegramBot telegramBot;
    private final AdoptedService adoptedService;


    public Notifier(TelegramBot telegramBot, AdoptedService adoptedService) {
        this.telegramBot = telegramBot;
        this.adoptedService = adoptedService;
    }
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void run() {
        adoptedService.findByPetReturnDate().forEach(animal -> {
            SendMessage message = new SendMessage(animal.getUser()
                    .getTelegramUserId(), "У вас есть пропуски по отчётам. Пожалуйста, отправьте отчёт по животному за просроченный период.");
            telegramBot.execute(message);
        });
    }
}
