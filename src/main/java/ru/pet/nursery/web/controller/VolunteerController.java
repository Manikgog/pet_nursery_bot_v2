package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.web.service.VolunteerService;

@Tag(name = "Домашние животные", description = "Эндпоинты для работы с домашними животными содержащимися в приюте")
@RequestMapping("/volunteer")
@RestController
public class VolunteerController {
    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @Operation(summary = "Загрузка в базу данных сведений о волонтере",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Загрузка сведений о волонтере прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class),
                                    examples = @ExampleObject(
                                            name = "Волонтер",
                                            description = "Объект волонтера загруженный в базу данных"
                                    )
                            )

                    )
            })
    @PostMapping
    public ResponseEntity<Volunteer> put(@RequestBody Volunteer volunteer){
        return volunteerService.upload(volunteer);
    }
}
