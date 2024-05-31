package ru.pet.nursery.web.service;

import net.datafaker.Faker;
import net.datafaker.service.FakeValuesService;
import net.datafaker.service.FakerContext;
import net.datafaker.service.RandomService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.validator.ReportValidator;
import ru.pet.nursery.web.validator.VolunteerValidator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceMockTest {
    @Mock
    ReportRepo reportRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    ReportValidator reportValidator;
    @Mock
    VolunteerValidator volunteerValidator;
    @InjectMocks
    ReportService reportService;
    private final Faker faker = new Faker();

    @Test
    public void upload_positiveTest(){
        long adopterId = 2;
        User user = new User();
        user.setTelegramUserId(adopterId);
        Animal animal = new Animal();
        animal.setUser(user);
        Report report = new Report();
        report.setReportDate(LocalDateTime.now());
        report.setUser(user);
        report.setId(0);
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        Report reportFromDB = new Report();
        reportFromDB.setId(1);
        reportFromDB.setReportDate(report.getReportDate());
        reportFromDB.setUser(report.getUser());
        when(reportRepo.save(any())).thenReturn(reportFromDB);
        Assertions.assertEquals(reportFromDB, reportService.upload(adopterId));
    }


    @Test
    public void upload_negativeTestByNotValidAdopterId(){
        int adopterIsNotInDataBase = 2;
        Assertions.assertThrows(IllegalFieldException.class, () -> reportService.upload(adopterIsNotInDataBase));
    }


    @Test
    public void delete_positiveTest(){
        int id = 1;
        Report report = new Report();
        when(reportRepo.findById((long) id)).thenReturn(Optional.of(report));
        Assertions.assertEquals(report, reportService.delete(id));
    }


    @Test
    public void delete_negativeTest(){
        int id = -1;
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.delete(id));
    }


    @Test
    public void updateFoto_positiveTest() throws IOException {
        long userId = 2;
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        String fileName = "animalImage";
        report.setReportDate(LocalDateTime.now());
        byte[] array = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName + ".jpg", "image.jpg", array);
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updateFoto(report.getId(), multipartFile));
    }


    @Test
    public void updateFoto_negativeTest(){
        // проверка работы метода при невалидном reportId
        long reportId = -1;
        String fileName = "animalPhoto";
        byte[] array = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        MultipartFile multipartFile = new MockMultipartFile(fileName, array);
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updateFoto(reportId, multipartFile));

    }


    /**
     * Проверка работы метода getExtension, который
     * получает название файла и возвращает его расширение
     */
    @Test
    public void getExtension_positiveTest(){
        FakeValuesService fakeValuesService = new FakeValuesService();
        FakerContext fakerContext = new FakerContext(Locale.UK, new RandomService());
        for (int i = 0; i < 5; i++) {
            String extension = fakeValuesService.regexify("(\\.[a-z][a-z][a-z])", fakerContext);
            String fileName = faker.name().name();
            String fullName = fileName + extension;
            Assertions.assertEquals(extension.substring(1), reportService.getExtension(fullName));
        }
    }


    @Test
    public void updateDiet_positiveTest(){
        long userId = 2;
        String diet = "diet";
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setId(1);
        report.setDiet(diet);
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updateDiet(report.getId(), diet));
    }

    /**
     * Проверка работы метода при неваличном id отчёта.
     * Т.к. не замокано действие при поиске в базе данных отчёта,
     * то выбрасывается исключение EntityNotFoundException
     */
    @Test
    public void updateDiet_negativeTest(){
        // проверка при невалидном id отчёта
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updateDiet(-1, "diet"));
    }


    @Test
    public void updateHealth_positiveTest(){
        long userId = 2;
        String health = "health";
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setId(1);
        report.setDiet(health);
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updateHealth(report.getId(), health));
    }


    @Test
    public void updateHealth_negativeTest(){
        // проверка при невалидном id отчёта
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updateHealth(-1, "health"));
    }


    @Test
    public void updateBehaviour_positiveTest(){
        long userId = 2;
        String behaviour = "behaviour";
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setId(1);
        report.setDiet(behaviour);
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updateBehaviour(report.getId(), behaviour));
    }

    @Test
    public void updateBehaviour_negativeTest(){
        // проверка при невалидном id отчёта
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updateBehaviour(-1, "behaviour"));
    }

    @Test
    public void updateIsAllItemsIsAccepted_positiveTest(){
        long userId = 2;
        boolean isAllItemsAccepted = true;
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setId(1);
        report.setAllItemsIsAccepted(isAllItemsAccepted);
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updateIsAllItemsIsAccepted(report.getId(), isAllItemsAccepted));
    }

    @Test
    public void updateIsAllItemsIsAccepted_negativeTest(){
        // проверка при невалидном id отчёта
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updateIsAllItemsIsAccepted(-1, true));
    }

    @Test
    public void updatePhotoIsAccepted_positiveTest(){
        long userId = 2;
        boolean isPhotoAccepted = true;
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setId(1);
        report.setPhotoIsAccepted(isPhotoAccepted);
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updatePhotoIsAccepted(report.getId(), isPhotoAccepted));
    }

    @Test
    public void updatePhotoIsAccepted_negativeTest(){
        // проверка при невалидном id отчёта
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updatePhotoIsAccepted(-1, true));
    }
    @Test
    public void updateIsDietAccepted_positiveTest(){
        long userId = 2;
        boolean isDietAccepted = true;
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setId(1);
        report.setDietIsAccepted(isDietAccepted);
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updateIsDietAccepted(report.getId(), isDietAccepted));
    }

    @Test
    public void updateIsDietAccepted_negativeTest(){
        // проверка при невалидном id отчёта
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updateIsDietAccepted(-1, true));
    }


    @Test
    public void updateIsHealthAccepted_positiveTest(){
        long userId = 2;
        boolean isHealthAccepted = true;
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setId(1);
        report.setHealthIsAccepted(isHealthAccepted);
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updateIsHealthAccepted(report.getId(), isHealthAccepted));
    }

    @Test
    public void updateIsHealthAccepted_negativeTest(){
        // проверка при невалидном id отчёта
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updateIsHealthAccepted(-1, true));
    }

    @Test
    public void updateIsBehaviourAccepted_positiveTest(){
        long userId = 2;
        boolean isBehaviourAccepted = true;
        User user = new User();
        user.setTelegramUserId(userId);
        Report report = new Report();
        report.setId(1);
        report.setBehaviourIsAccepted(isBehaviourAccepted);
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updateIsHealthAccepted(report.getId(), isBehaviourAccepted));
    }


    @Test
    public void updateIsBehaviourAccepted_negativeTest(){
        // проверка при невалидном id отчёта
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.updateIsBehaviourAccepted(-1, true));
    }

    @Test
    public void getListOfReportByDate_Test(){
        List<Report> reports = new ArrayList<>();
        when(reportRepo.findByReportDate(any())).thenReturn(reports);
        Assertions.assertEquals(reports, reportService.getListOfReportByDate(LocalDate.now()));
    }



    @Test
    public void findByUserAndDate_Test(){
        User user = new User();
        Report report = new Report();
        when(reportRepo.findByUserAndReportDate(user, LocalDate.now().atStartOfDay())).thenReturn(report);
        Assertions.assertEquals(report, reportService.findByUserAndDate(user, LocalDate.now().atStartOfDay()));
    }


    @Test
    public void updatePhotoPath_Test(){
        String path = "/path";
        Report report = new Report();
        report.setId(1);
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        report.setPathToPhoto(path);
        when(reportRepo.save(report)).thenReturn(report);
        Assertions.assertEquals(report, reportService.updatePhotoPath(report.getId(), path));
    }

}
