package ru.pet.nursery.notifying;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.web.service.AdoptedService;
import ru.pet.nursery.web.service.AnimalService;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.service.VolunteerService;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class Notifier {
    private final Logger log = LoggerFactory.getLogger(AdoptedService.class);
    private final TelegramBot telegramBot;
    private final ReportService reportService;
    private final AnimalService animalService;
    private final VolunteerService volunteerService;
    private final AnimalRepo animalRepo;
    private final ReportRepo reportRepo;

    public Notifier(TelegramBot telegramBot,
                    ReportService reportService,
                    AnimalService animalService,
                    VolunteerService volunteerService,
                    AnimalRepo animalRepo,
                    ReportRepo reportRepo) {
        this.telegramBot = telegramBot;
        this.reportService = reportService;
        this.animalService = animalService;
        this.volunteerService = volunteerService;
        this.animalRepo = animalRepo;
        this.reportRepo = reportRepo;
    }


    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void run() {
        log.info("Запущен метод run класса Notifier");
        List<User> adopters =  animalRepo.findByUserIsNotNull()
                .stream()
                .map(Animal::getUser)
                .toList();

        adopters.stream()
                        .forEach(u -> {
                            List<Report> reports = reportRepo.findByUser(u);
                            if (reports.isEmpty()) {
                                SendMessage message = new SendMessage(u.getTelegramUserId(), "У вас пока нет ни одного отчёта.");
                                telegramBot.execute(message);
                            }else{
                                reports = reports.stream()
                                        .filter(r -> r.getReportDate().equals(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS)))
                                        .toList();
                                if(reports.isEmpty()){
                                    SendMessage message = new SendMessage(u.getTelegramUserId(), "Вы не отправили отчёт за " + LocalDate.now().minusDays(1) + ".");
                                    telegramBot.execute(message);
                                }
                                if(!reports.isEmpty()){
                                    if(!reports.get(0).isBehaviourIsAccepted() ||
                                    !reports.get(0).isDietIsAccepted() ||
                                    !reports.get(0).isHealthIsAccepted() ||
                                    !reports.get(0).isPhotoIsAccepted()){
                                        SendMessage message = new SendMessage(u.getTelegramUserId(), "Вы отправили неполный отчёт за " + reports.get(0).getReportDate().toLocalDate() + ". Пожалуйста, отправляйте отчёт по всем пунктам.");
                                        telegramBot.execute(message);
                                    }
                                }
                            }
                        });



        reportService.findByPetReturnDate().stream().filter(report ->
                !report.isPhotoIsAccepted() ||
                        !report.isBehaviourIsAccepted() ||
                        !report.isDietIsAccepted() ||
                        !report.isHealthIsAccepted()
        ).forEach(report -> {
            SendMessage message = new SendMessage(report.getUser()
                    .getTelegramUserId(), "У вас есть пропуск по отчётам. Пожалуйста, отправьте отчёт по животному за просроченный период.");
            telegramBot.execute(message);
            report.setReportDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusDays(1));
        });
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void completionOfTheAdaptationPeriod() {
        animalService.adoptionPeriod().forEach(animal -> {
            SendMessage message = new SendMessage(animal.getUser().getTelegramUserId(),
                    "У вас заканчивается адаптационный период "+animal.getPetReturnDate()+" у питомца "+animal.getAnimalName()+" " +
                            ", пожалуйста свяжитесь с волонтером для продления срока пребывания");
            telegramBot.execute(message);
            List<Volunteer> volunteerList = volunteerService.findIsActive();
            for (Volunteer volunteer : volunteerList) {
                message = new SendMessage(volunteer.getTelegramUserId(), "У пользователя "
                        + animal.getUser().getTelegramUserId() + " истек испытательный срок");
                telegramBot.execute(message);
            }
        });
    }
}
