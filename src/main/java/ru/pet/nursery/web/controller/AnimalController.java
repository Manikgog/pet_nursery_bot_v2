package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.dto.AnimalDTOForUser;
import ru.pet.nursery.web.service.AnimalService;

import java.io.IOException;
import java.util.List;

@Tag(name = "Домашние животные", description = "Эндпоинты для работы с домашними животными содержащимися в приюте")
@RequestMapping("/animal")
@RestController
public class AnimalController {
    private final AnimalService animalService;
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @Operation(summary = "Загрузка в базу данных сведений о животном",
    responses = {
        @ApiResponse(
                responseCode = "200",
                description = "Загрузка сведений о животном прошла успешно",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = Animal.class),
                        examples = @ExampleObject(
                                name = "Питомец",
                                description = "Объект питомца загруженный в базу данных"
                        )
                )

        )
    })
    @PostMapping
    public ResponseEntity<Animal> putAnimal(@RequestBody AnimalDTO animalDTO){
        return animalService.uploadAnimal(animalDTO);
    }



    @Operation(summary = "Добавление фотографии животного по его идентификатору",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Загрузка фотографии животного прошла успешно",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = HttpStatus.class)
                        )
                )
            }
    )
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> uploadPhotoAnimal(@PathVariable("id") Integer id, @RequestParam MultipartFile animalPhoto) throws IOException, InterruptedException {
        return animalService.uploadPhoto(id, animalPhoto);
    }



    @Operation(summary = "Вставка данных о человеке, который забрал животное из приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Вставка данных о человеке, который забрал животное из приюта прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HttpStatus.class)
                            )
                    )
            }
    )
    @PostMapping("/{animalId}/{adoptedId}")
    public ResponseEntity<HttpStatus> insertHumanWhoTookAnimal(@PathVariable Integer animalId, @PathVariable Long adoptedId){
        return animalService.insertDataOfHuman(animalId, adoptedId);
    }



    @Operation(summary = "Вставка данных о возвращении животного в приют",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Вставка данных о возвращении животного в приют прошла успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HttpStatus.class)
                            )
                    )
            }
    )
    @PostMapping("/{animalId}/return")
    public ResponseEntity<HttpStatus> insertDateOfReturningAnimal(@PathVariable Integer animalId){
        return animalService.insertDateOfReturn(animalId);
    }



    @Operation(summary = "Получение фотографии животного по его id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Получение изображения животного по его id прошло успешно",
                            content = @Content(
                                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                            )
                    )
            }
    )
    @GetMapping("/{id}/photo")
    public void getAnimalPhoto(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        animalService.getAnimalPhoto(id, response);
    }



    @Operation(summary = "Удаление животного из таблицы animal_table по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаление животного из таблицы animal_table по id прошло успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class),
                                    examples = @ExampleObject(
                                            name = "Питомец",
                                            description = "Объект питомца удаленный из базы данных"
                                    )
                            )

                    )
            })
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Animal> deleteAnimal(@PathVariable Integer id){
        return animalService.delete(id);
    }



    @Operation(summary = "Получение списка животных, которые находятся в питомниках постранично",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Получение списка животных постранично прошло успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class)),
                                    examples = @ExampleObject(
                                            name = "Список животных"
                                    )
                            )
                    )
            })
    @GetMapping
    public ResponseEntity<List<AnimalDTOForUser>> getListByPage(@RequestParam("page") Integer pageNumber, @RequestParam("size") Integer pageSize){
        return animalService.getPageList(pageNumber, pageSize);
    }



    @Operation(summary = "Получение питомца по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Получение питомца по его id прошло успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class),
                                    examples = @ExampleObject(
                                            name = "Питомец",
                                            description = "Объект питомца удаленный из базы данных"
                                    )
                            )
                    )
            })
    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTOForUser> getById(@PathVariable Integer id){
        return animalService.getById(id);
    }


    @Operation(summary = "Получение списка всех питомцев",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Получение списка всех питомцев прошло успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class)),
                                    examples = @ExampleObject(
                                            name = "Список всех животных"
                                    )
                            )

                    )
            })
    @GetMapping("/all")
    public ResponseEntity<List<Animal>> getAnimals(){
        return animalService.getAll();
    }

}
