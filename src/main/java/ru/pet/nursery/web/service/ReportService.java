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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
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

    public ReportService(ReportRepo reportRepo,
                         UserRepo userRepo,
                         ReportValidator reportValidator) {
        this.reportRepo = reportRepo;
        this.reportValidator = reportValidator;
        this.userRepo = userRepo;
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
}
