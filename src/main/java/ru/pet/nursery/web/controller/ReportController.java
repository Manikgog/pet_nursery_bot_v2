package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.web.service.ReportService;

import java.io.IOException;

@Tag(name = "Отчёты", description = "Эндпоинты для работы с отчётами о содержании питомцев")
@RequestMapping("/report")
@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }




    @Operation(summary = "Загрузка нового отчёта о содержании питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка отчёта прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class),
                                    examples = @ExampleObject(
                                            name = "Отчёт",
                                            description = "Объект отчёта загруженный в базу данных"
                                    )
                            )
                    )
            })
    @PostMapping("/{adopterId}")
    public ResponseEntity<Report> upload(@PathVariable long adopterId){
        return reportService.upload(adopterId);
    }





    @Operation(summary = "Удаление отчёта о содержании питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаление отчёта прошло успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class),
                                    examples = @ExampleObject(
                                            name = "Отчёт",
                                            description = "Объект отчёта удалённый из базы данных"
                                    )
                            )
                    )
            })
    @DeleteMapping
    public ResponseEntity<Report> delete(long id){
        return reportService.delete(id);
    }





    @Operation(summary = "Добавление фотографии в отчёт о питомце по идентификатору отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка фотографии прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HttpStatus.class)
                            )
                    )
            }
    )
    @PutMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity updateFoto(@PathVariable long id, @RequestParam MultipartFile animalPhoto) throws IOException {
        return reportService.updateFoto(id, animalPhoto);
    }



}
