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
import ru.pet.nursery.web.service.AnimalService;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.service.VolunteerService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class Notifier {
    private final Logger log = LoggerFactory.getLogger(Notifier.class);
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

    /**
     * Метод проверяет ежедневные отчеты по расписанию (1 раз в день).
     * Отправляет напоминания пользователю 1 раз в день в 20:00 есть ли пропуски по отчету целиком или по его полям.
     */
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void run() {
        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        log.info("Запущен метод run класса Notifier");
        if (time.equals(LocalTime.of(20, 0))) {
            List<User> adopters = animalRepo.findByUserIsNotNull()
                    .stream()
                    .map(Animal::getUser)
                    .toList();
            adopters.forEach(u -> {
                        List<Report> reports = reportRepo.findByUser(u);
                        if (reports.isEmpty()) {
                            SendMessage message = new SendMessage(u.getTelegramUserId(), "У вас пока нет ни одного отчёта.");
                            telegramBot.execute(message);
                        } else {
                            reports = reports.stream()
                                    .filter(r -> r.getReportDate().equals(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)))
                                    .toList();
                            if (reports.isEmpty()) {
                                SendMessage message = new SendMessage(u.getTelegramUserId(), "Вы не отправили отчёт за " + LocalDate.now().minusDays(1) + ".");
                                telegramBot.execute(message);
                            }
                            if (!reports.isEmpty()) {
                                if (!reports.get(0).isBehaviourIsAccepted() ||
                                        !reports.get(0).isDietIsAccepted() ||
                                        !reports.get(0).isHealthIsAccepted() ||
                                        !reports.get(0).isPhotoIsAccepted()) {
                                    SendMessage message = new SendMessage(u.getTelegramUserId(), "Вы отправили неполный отчёт за " +
                                            reports.get(0).getReportDate().toLocalDate() + ". Пожалуйста, отправляйте отчёт по всем пунктам.");
                                    telegramBot.execute(message);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Метод добавляет по расписанию 1 день к испытательному сроку пребывания питомца если отчета нет в течение суток
     */
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void probation() {
        log.info("Запущен шедулер probation");
        List<User> adopters = animalRepo.findByUserIsNotNull()
                .stream()
                .map(Animal::getUser)
                .toList();
        adopters.forEach(u -> {
                    List<Report> reports = reportRepo.findByUser(u).stream().filter(report -> report.getReportDate()
                            .toLocalDate().isEqual(LocalDate.now().minusDays(1))).toList();
                    LocalDate tookDate = animalRepo.findByUser(u).get(0).getTookDate();
                    // если дата отчёта совпадает с датой когда питомца взяли из приюта, то отчёт не нужен
                    if(LocalDate.now().minusDays(1).isEqual(tookDate)){
                        return;
                    }
                    if (reports.isEmpty()) {
                        List<Animal> animal = animalRepo.findByUser(u);
                        animal.get(0).setPetReturnDate(animal.get(0).getPetReturnDate().plusDays(1));
                        animalRepo.save(animal.get(0));
                    }else {
                        if (!reports.get(0).isBehaviourIsAccepted() ||
                                !reports.get(0).isDietIsAccepted() ||
                                !reports.get(0).isHealthIsAccepted() ||
                                !reports.get(0).isPhotoIsAccepted()) {
                            List<Animal> animal = animalRepo.findByUser(u);
                            animal.get(0).setPetReturnDate(animal.get(0).getPetReturnDate().plusDays(1));
                            animalRepo.save(animal.get(0));
                        }
                    }

                });
    }

    /**
     * Метод отправляет по расписанию 1 раз в день сообщение волонтеру, что у одного из пользователей истек испытательный срок.
     */
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void completionOfTheAdaptationPeriod() {
        log.info("Запущен шедулер completionOfTheAdaptationPeriod");
        animalService.adoptionPeriod().forEach(animal -> {
            SendMessage message = new SendMessage(animal.getUser().getTelegramUserId(),
                    "У вас заканчивается адаптационный период " +
                            animal.getPetReturnDate() + " у питомца " +
                            animal.getAnimalName() + " " +
                            ", пожалуйста свяжитесь с волонтером для продления срока пребывания");
            telegramBot.execute(message);
            List<Volunteer> volunteerList = volunteerService.findIsActive();
            for (Volunteer volunteer : volunteerList) {
                message = new SendMessage(volunteer.getTelegramUserId(), "У пользователя @"
                        + animal.getUser().getTelegramUserId() + " истек испытательный срок");
                telegramBot.execute(message);
            }
        });
    }
}
