package ru.pet.nursery.notifying;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pet.nursery.web.service.AnimalService;
import ru.pet.nursery.web.service.ReportService;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class Notifier {
    private final TelegramBot telegramBot;
    private final ReportService reportService;
    private final AnimalService animalService;

    public Notifier(TelegramBot telegramBot, ReportService reportService, AnimalService animalService) {
        this.telegramBot = telegramBot;
        this.reportService = reportService;
        this.animalService = animalService;
    }


    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    public void run() {
        reportService.findByPetReturnDate().forEach(report -> {
            SendMessage message = new SendMessage(report.getUser()
                    .getTelegramUserId(), "У вас есть пропуск по отчётам. Пожалуйста, отправьте отчёт по животному за просроченный период.");
            telegramBot.execute(message);
            report.setReportDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1));
        });
    }
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void completionOfTheAdaptationPeriod() {
        animalService.adoptionPeriod().forEach(animal -> {
            SendMessage message = new SendMessage(animal.getUser().getTelegramUserId(),
                    "У вас заканчивается адаптационный период "+animal.getPetReturnDate()+" у питомца "+animal.getAnimalName()+" " +
                            ", пожалуйста свяжитесь с волонтером для продления срока пребывания");
            telegramBot.execute(message);
        });
    }
}
