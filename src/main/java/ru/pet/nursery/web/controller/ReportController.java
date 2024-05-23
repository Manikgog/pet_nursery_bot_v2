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
    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity putFoto(@PathVariable long id, @RequestParam MultipartFile animalPhoto) throws IOException {
        return reportService.updateFoto(id, animalPhoto);
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
    @PutMapping("/{id}/diet")
    public ResponseEntity<Report> putDiet(@PathVariable long id, @RequestBody String diet){
        return reportService.updateDiet(id, diet);
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
    @PutMapping("/{id}/health")
    public ResponseEntity<Report> putHealth(@PathVariable long id, @RequestBody String health){
        return reportService.updateHealth(id, health);
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
    @PutMapping("/{id}/behaviour")
    public ResponseEntity<Report> putBehaviour(@PathVariable long id, @RequestBody String behaviour){
        return reportService.updateBehaviour(id, behaviour);
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
    @PutMapping("/{id}/acceptAll")
    public ResponseEntity<Report> putIsAllItemsAccepted(@PathVariable long id, @RequestParam boolean AllItemsIsAccepted){
        return reportService.updateIsAllItemsIsAccepted(id, AllItemsIsAccepted);
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
    @PutMapping("/{id}/acceptPhoto")
    public ResponseEntity<Report> putIsFotoAccepted(@PathVariable long id, @RequestParam boolean FotoIsAccepted){
        return reportService.updatePhotoIsAccepted(id, FotoIsAccepted);
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
    @PutMapping("/{id}/acceptDiet")
    public ResponseEntity<Report> putIsDietAccepted(@PathVariable long id, @RequestParam boolean DietIsAccepted){
        return reportService.updateIsDietAccepted(id, DietIsAccepted);
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
    @PutMapping("/{id}/acceptHealth")
    public ResponseEntity<Report> putIsHealthAccepted(@PathVariable long id, @RequestParam boolean HealthIsAccepted){
        return reportService.updateIsHealthAccepted(id, HealthIsAccepted);
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
    @PutMapping("/{id}/acceptBehaviour")
    public ResponseEntity<Report> putIsBehaviourAccepted(@PathVariable long id, @RequestParam boolean BehaviourIsAccepted){
        return reportService.updateIsBehaviourAccepted(id, BehaviourIsAccepted);
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
