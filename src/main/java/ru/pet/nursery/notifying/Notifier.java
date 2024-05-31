package ru.pet.nursery.notifying;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pet.nursery.web.service.ReportService;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class Notifier {
    private final TelegramBot telegramBot;
    private final ReportService reportService;

    public Notifier(TelegramBot telegramBot, ReportService reportService) {
        this.telegramBot = telegramBot;
        this.reportService = reportService;
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
}
