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
import ru.pet.nursery.web.exception.ReportIsExistException;
import ru.pet.nursery.web.validator.ReportValidator;
import ru.pet.nursery.web.validator.VolunteerValidator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class ReportService {
    @Value("${path.to.report_foto.folder}")
    private String REPORT_FOTO;
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
    public Report upload(long adopterId) {
        User user = userRepo.findById(adopterId)
                .orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        try {
            reportValidator.validate(user);
        }catch (ReportIsExistException e){
            return reportRepo.findByUserAndReportDate(user, LocalDate.now());
        }
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setUser(user);
        newReport.setReportDate(LocalDateTime.now());
        return reportRepo.save(newReport);
    }

    /**
     * Метод для удаления отчёта по его идентификатору
     * @param id - идентификатор отчёта
     * @return удалённый из базы данных отчёт
     */
    public Report delete(long id) {
        Report reportFromDB = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportRepo.delete(reportFromDB);
        return reportFromDB;
    }

    /**
     * Метод для загрузки фотографии питомца для отчета,
     * которая загружается на диск, а путь к ней в базу
     * данных
     * @param id - идентификатор отчёта
     * @param reportFoto - файл с фотографией
     * @return ResponseEntity.ok()
     * @throws IOException - исключение ввода-вывода
     */
    public ResponseEntity updateFoto(long id, MultipartFile reportFoto) throws IOException {
        Report reportFromDB = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        String strPath = System.getProperty("user.dir");
        strPath += REPORT_FOTO;
        Path path = Path.of(strPath);
        Path filePath = Path.of(path.toString(), reportFromDB.getId() + "." + getExtension(Objects.requireNonNull(reportFoto.getOriginalFilename())));
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
    public String getExtension(String fileName){
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private void updateFotoPathColumn(String path, long id){
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setPathToPhoto(path);
        Report reportNew = reportRepo.save(reportOld);
    }


    /**
     * Метод для обновления поля diet в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param diet - строка с описанием диеты питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public Report updateDiet(long id, String diet) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        validator.stringValidate(diet);
        reportOld.setDiet(diet);
        return reportRepo.save(reportOld);
    }


    /**
     * Метод для обновления поля health в строке с идентификатором id
     *
     * @param id     - идентификатор отчёта в таблице отчётов
     * @param health - строка с описанием здоровья питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public Report updateHealth(long id, String health) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        validator.stringValidate(health);
        reportOld.setHealth(health);
        return reportRepo.save(reportOld);
    }


    /**
     * Метод для обновления поля behaviour в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param behaviour - строка с описанием поведения питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public Report updateBehaviour(long id, String behaviour) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        validator.stringValidate(behaviour);
        reportOld.setBehaviour(behaviour);
        return reportRepo.save(reportOld);
    }


    /**
     * Метод для обновления поля allItemsIsAccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isAllItemsAccepted - приняты или не приняты (true или false) все пункты отчёта, т.е. присутствуют ли они в базе данных
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public Report updateIsAllItemsIsAccepted(long id, boolean isAllItemsAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setAllItemsIsAccepted(isAllItemsAccepted);
        return reportRepo.save(reportOld);
    }


    /**
     * Метод для обновления поля fotoIsAaccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isPhotoAccepted - принята или не принята (true или false) волонтером фотография питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public Report updatePhotoIsAccepted(long id, boolean isPhotoAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setPhotoIsAccepted(isPhotoAccepted);
        return reportRepo.save(reportOld);
    }


    /**
     * Метод для обновления поля dietIsAccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isDietAccepted - принята или не принята (true или false) волонтером отчёт о диете питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public Report updateIsDietAccepted(long id, boolean isDietAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setDietIsAccepted(isDietAccepted);
        return reportRepo.save(reportOld);
    }


    /**
     * Метод для обновления поля healthIsAccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isHealthAccepted - принят или не принят (true или false) волонтером отчёт о здоровье питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public Report updateIsHealthAccepted(long id, boolean isHealthAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setHealthIsAccepted(isHealthAccepted);
        return reportRepo.save(reportOld);
    }



    /**
     * Метод для обновления поля behaviourIsAccepted в строке с идентификатором id
     * @param id - идентификатор отчёта в таблице отчётов
     * @param isBehaviourAccepted - принят или не принят (true или false) волонтером отчёт о поведении питомца
     * @return ResponseEntity<Report> - измененный объект отчёта из базы данных
     */
    public Report updateIsBehaviourAccepted(long id, boolean isBehaviourAccepted) {
        Report reportOld = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        reportOld.setBehaviourIsAccepted(isBehaviourAccepted);
        return reportRepo.save(reportOld);
    }


    /**
     * Метод для получения из базы данных списка отчётов по переданной дате
     * @param date - дата
     * @return ResponseEntity<List<Report>> - список объектов Report
     */
    public List<Report> getListOfReportByDate(LocalDate date) {
        return reportRepo.findByReportDate(date);
    }

    /**
     * Метод для поиска отчёта по усыновителю и по дате отчёта
     * @param user - усыновитель
     * @param date - дата отчёта
     * @return объект отчёта
     */
    public Report findByUserAndDate(User user, LocalDate date){
        return reportRepo.findByUserAndReportDate(user, date);
    }


    public Report updatePhotoPath(long reportId, String path){
        Report reportOld = reportRepo.findById(reportId).orElseThrow(() -> new EntityNotFoundException(reportId));
        reportOld.setPathToPhoto(path);
        return reportRepo.save(reportOld);
    }


    public List<Report> findByPetReturnDate() {
        return reportRepo.findByNextReportDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
    }
}
