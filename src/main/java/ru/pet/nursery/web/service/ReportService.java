package ru.pet.nursery.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.validator.ReportValidator;
import ru.pet.nursery.web.validator.VolunteerValidator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class ReportService {
    @Value("${path.to.report_foto.folder}")
    private String report_foto;
    private final ReportRepo reportRepo;
    private final UserRepo userRepo;
    private final ReportValidator reportValidator;
    private final VolunteerValidator validator;

    public ReportService(ReportRepo reportRepo,
                         UserRepo userRepo,
                         ReportValidator reportValidator,
                         VolunteerValidator validator) {
        this.reportRepo = reportRepo;
        this.reportValidator = reportValidator;
        this.userRepo = userRepo;
        this.validator = validator;
    }

    /**
     * Метод для загрузки нового отчёта в базу данных
     * @param adopterId - идентификатор усыновителя из таблицы пользователей
     * @return ResponseEntity.of(Optional.of(reportFromDB))
     */
    public ResponseEntity<Report> upload(long adopterId) {
        reportValidator.validate(adopterId);
        User user = userRepo.findById(adopterId)
                .orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setUser(user);
        newReport.setReportDate(LocalDate.now());
        Report reportFromDB = reportRepo.save(newReport);
        return ResponseEntity.of(Optional.of(reportFromDB));
    }

    /**
     * Метод для удаления отчёта по его идентификатору
     * @param id - идентификатор отчёта
     * @return удалённый из базы данных отчёт
     */
    public ResponseEntity<Report> delete(long id) {
        Report reportFromDB = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportRepo.delete(reportFromDB);
        return ResponseEntity.of(Optional.of(reportFromDB));
    }

    /**
     * Метод для загрузки фотографии питомца для отчета,
     * которая загружается на диск, а путь к ней в базу
     * данных
     * @param telegramUserId - идентификатор пользователя
     * @param reportFoto - файл с фотографией
     * @return ResponseEntity.ok()
     * @throws IOException - исключение ввода-вывода
     */
    public ResponseEntity updateFoto(long telegramUserId, MultipartFile reportFoto) throws IOException {
        reportValidator.validateIsAdopter(telegramUserId);
        User user = userRepo.findById(telegramUserId).orElseThrow(() -> new EntityNotFoundException(telegramUserId));
        Report reportFromDB = reportRepo.findByUser(user)
                .stream()
                .filter(r -> r.getReportDate().equals(LocalDate.now()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(telegramUserId));
        String strPath = System.getProperty("user.dir");
        strPath += report_foto;
        Path path = Path.of(strPath);
        Path filePath = Path.of(path.toString(), reportFromDB.getId() + "." + getExtention(Objects.requireNonNull(reportFoto.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try(InputStream is = reportFoto.getInputStream();
            OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ){
            bis.transferTo(bos);
        }

        updateFotoPathColumn(filePath.toString(), reportFromDB.getId());

        return ResponseEntity.ok().build();
    }

    /**
     * Метод для поиска и возвращения строки, содержащей расширения файла
     * @param fileName - имя файла
     * @return строка, содержащая расширения файла
     */
    public String getExtention(String fileName){
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private void updateFotoPathColumn(String path, long id){
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setPath_to_foto(path);
        Report reportNew = reportRepo.save(reportOld);
    }


    /**
     * Метод для обновления поля diet в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param diet - строка с описанием диеты питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public ResponseEntity<Report> updateDiet(long id, String diet) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        validator.stringValidate(diet);
        reportOld.setDiet(diet);
        Report reportNew = reportRepo.save(reportOld);
        return ResponseEntity.of(Optional.of(reportNew));
    }


    /**
     * Метод для обновления поля health в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param health - строка с описанием здоровья питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public ResponseEntity<Report> updateHealth(long id, String health) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        validator.stringValidate(health);
        reportOld.setHealth(health);
        Report reportNew = reportRepo.save(reportOld);
        return ResponseEntity.of(Optional.of(reportNew));
    }


    /**
     * Метод для обновления поля behaviour в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param behaviour - строка с описанием поведения питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public ResponseEntity<Report> updateBehaviour(long id, String behaviour) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        validator.stringValidate(behaviour);
        reportOld.setBehaviour(behaviour);
        Report reportNew = reportRepo.save(reportOld);
        return ResponseEntity.of(Optional.of(reportNew));
    }


    /**
     * Метод для обновления поля allItemsIsAccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isAllItemsAccepted - приняты или не приняты (true или false) все пункты отчёта, т.е. присутствуют ли они в базе данных
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public ResponseEntity<Report> updateIsAllItemsIsAccepted(long id, boolean isAllItemsAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setAllItemsIsAccepted(isAllItemsAccepted);
        Report reportNew = reportRepo.save(reportOld);
        return ResponseEntity.of(Optional.of(reportNew));
    }


    /**
     * Метод для обновления поля fotoIsAaccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isFotoAccepted - принята или не принята (true или false) волонтером фотография питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public ResponseEntity<Report> updateFotoIsAccepted(long id, boolean isFotoAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setFotoIsAaccepted(isFotoAccepted);
        Report reportNew = reportRepo.save(reportOld);
        return ResponseEntity.of(Optional.of(reportNew));
    }


    /**
     * Метод для обновления поля dietIsAccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isDietAccepted - принята или не принята (true или false) волонтером отчёт о диете питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public ResponseEntity<Report> updateIsDietAccepted(long id, boolean isDietAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setDietIsAccepted(isDietAccepted);
        Report reportNew = reportRepo.save(reportOld);
        return ResponseEntity.of(Optional.of(reportNew));
    }


    /**
     * Метод для обновления поля healthIsAccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isHealthAccepted - принят или не принят (true или false) волонтером отчёт о здоровье питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public ResponseEntity<Report> updateIsHealthAccepted(long id, boolean isHealthAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setHealthIsAccepted(isHealthAccepted);
        Report reportNew = reportRepo.save(reportOld);
        return ResponseEntity.of(Optional.of(reportNew));
    }



    /**
     * Метод для обновления поля behaviourIsAccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isBehaviourAccepted - принят или не принят (true или false) волонтером отчёт о поведении питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public ResponseEntity<Report> updateIsBehaviourAccepted(long id, boolean isBehaviourAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setBehaviourIsAccepted(isBehaviourAccepted);
        Report reportNew = reportRepo.save(reportOld);
        return ResponseEntity.of(Optional.of(reportNew));
    }


    /**
     * Метод для получения из базы данных списка отчётов по переданной дате
     * @param date - дата
     * @return ResponseEntity<List<Report>> - список объектов Report
     */
    public ResponseEntity<List<Report>> getListOfReportByDate(LocalDate date) {
        return ResponseEntity.of(Optional.ofNullable(reportRepo.findByReportDate(date)));
    }
}
