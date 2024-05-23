package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import java.time.LocalDate;
import java.util.List;

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
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Report> delete(@PathVariable long reportId){
        return reportService.delete(reportId);
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
    @PutMapping(value = "/{reportId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity putFoto(@PathVariable long reportId, @RequestParam MultipartFile animalPhoto) throws IOException {
        return reportService.updateFoto(reportId, animalPhoto);
    }




    @Operation(summary = "Добавление описания диеты питомца в отчёт по идентификатору отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка описания диеты прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            }
    )
    @PutMapping("/{reportId}/diet")
    public ResponseEntity<Report> putDiet(@PathVariable long reportId, @RequestParam String diet){
        return reportService.updateDiet(reportId, diet);
    }




    @Operation(summary = "Добавление описания здоровья питомца в отчёт по идентификатору отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка описания здоровья прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            }
    )
    @PutMapping("/{reportId}/health")
    public ResponseEntity<Report> putHealth(@PathVariable long reportId, @RequestParam String health){
        return reportService.updateHealth(reportId, health);
    }



    @Operation(summary = "Добавление описания поведения питомца в отчёт по идентификатору отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка описания поведения прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            }
    )
    @PutMapping("/{reportId}/behaviour")
    public ResponseEntity<Report> putBehaviour(@PathVariable long reportId, @RequestParam String behaviour){
        return reportService.updateBehaviour(reportId, behaviour);
    }



    @Operation(summary = "Изменение флага о принятии в базу данных всех пунктов отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка флага прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            }
    )
    @PutMapping("/{reportId}/acceptAll")
    public ResponseEntity<Report> putIsAllItemsAccepted(@PathVariable long reportId, @RequestParam boolean acceptAll){
        return reportService.updateIsAllItemsIsAccepted(reportId, acceptAll);
    }



    @Operation(summary = "Изменение флага о принятии в базу данных фотографии питомца для отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка флага прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            }
    )
    @PutMapping("/{reportId}/acceptPhoto")
    public ResponseEntity<Report> putIsFotoAccepted(@PathVariable long reportId, @RequestParam boolean acceptPhoto){
        return reportService.updatePhotoIsAccepted(reportId, acceptPhoto);
    }



    @Operation(summary = "Изменение флага о принятии в базу данных описания диеты для отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка флага прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            }
    )
    @PutMapping("/{reportId}/acceptDiet")
    public ResponseEntity<Report> putIsDietAccepted(@PathVariable long reportId, @RequestParam boolean acceptDiet){
        return reportService.updateIsDietAccepted(reportId, acceptDiet);
    }



    @Operation(summary = "Изменение флага о принятии в базу данных описания здоровья для отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка флага прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            }
    )
    @PutMapping("/{reportId}/acceptHealth")
    public ResponseEntity<Report> putIsHealthAccepted(@PathVariable long reportId, @RequestParam boolean acceptHealth){
        return reportService.updateIsHealthAccepted(reportId, acceptHealth);
    }



    @Operation(summary = "Изменение флага о принятии в базу данных описания поведения для отчёта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка флага прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            }
    )
    @PutMapping("/{reportId}/acceptBehaviour")
    public ResponseEntity<Report> putIsBehaviourAccepted(@PathVariable long reportId, @RequestParam boolean acceptBehaviour){
        return reportService.updateIsBehaviourAccepted(reportId, acceptBehaviour);
    }




    @Operation(summary = "Получение списка отчётов по дате",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Получение списка отчётов прошло успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class)),
                                    examples = @ExampleObject(
                                            name = "Список отчётов"
                                    )
                            )
                    )
            })
    @GetMapping("/{date}")
    public ResponseEntity<List<Report>> getListOfReportByDate(@Parameter(description = "Дата отчёта: ГОД-МЕСЯЦ-ДЕНЬ", example = "2024-05-01")
                                                                  @PathVariable LocalDate date){
        return reportService.getListOfReportByDate(date);
    }

}
