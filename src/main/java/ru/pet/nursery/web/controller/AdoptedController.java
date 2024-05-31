package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.web.service.AdoptedService;

@Tag(name = "Усыновление питомцев", description = "Эндпоинт для присвоение усыновителя к питомцу")
@RequestMapping("/adopters")
@RestController
public class AdoptedController {
    private final AdoptedService adoptedService;

    public AdoptedController(AdoptedService adoptedService) {
        this.adoptedService = adoptedService;
    }
    @Operation(summary = "Присвоение усыновителя к питомцу", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Обновление данных о усыновителе прошла успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class),
                            examples = @ExampleObject(
                                    name = "Питомец",
                                    description = "Объект питомца изменен"
                            )
                    )
            )
    })
    @PutMapping(value = "/setAdopterForAnimal", params = {"animalId","adopterId"})
    public ResponseEntity<Animal> setAdopterForAnimal(@RequestParam Long animalId, @RequestParam Long adopterId) {
        return ResponseEntity.ok(adoptedService.setAdopterForAnimal(animalId, adopterId));
    }
    @Operation(summary = "Продление испытательного срока", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Продление испытательного срока успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class),
                            examples = @ExampleObject(
                                    name = "Питомец",
                                    description = "Объект питомца изменен"
                            )
                    )
            )
    })
    @PutMapping(value = "/prolongTrialForNDays", params = {"animalId","days"})
    public ResponseEntity<Animal> prolongTrialForNDays(@RequestParam Long animalId, @RequestParam Integer days) {
        return ResponseEntity.ok(adoptedService.prolongTrialForNDays(animalId,days));
    }
    @Operation(summary = "Удаление испытательного срока прошло успешно", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Обновление данных у питомца прошло успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class),
                            examples = @ExampleObject(
                                    name = "Питомец",
                                    description = "Объект питомца изменен"
                            )
                    )
            )
    })
    @PutMapping(value = "cancelTrial", params = {"animalId"})
    public ResponseEntity<Animal> cancelTrial(@RequestParam Long animalId) {
        return ResponseEntity.ok(adoptedService.cancelTrial(animalId));
    }
}
