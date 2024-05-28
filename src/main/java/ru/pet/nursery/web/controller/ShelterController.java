package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.web.service.ShelterService;

import java.util.List;

@Tag(name = "Приюты", description = "Эндпоинты для работы с приютами")
@RequestMapping("/shelter")
@RestController
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    /**
     * Эндпонит для добавления нового приюта в БД, может вернуть ShelterNullException если название приюта, номер телефона или адрес null
     * @param nursery в тело запроса предается объект типа Nursery
     * @return ResponseEntity в котором содержится сущность Nursery из БД
     */

    @Operation(summary = "Добавление приютов в БД", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Добавление приюта прошло успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Nursery.class),
                            examples = @ExampleObject(
                                    name = "Приют",
                                    description = "Объект приюта добавлен в базу данных"
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Nursery> putShelter(@RequestBody Nursery nursery) {
        return ResponseEntity.ok(shelterService.addShelter(nursery));
    }

    /**
     * Эндпоинт для поиска приюта по id, может вернуть ShelterNotFoundException если такого приюта нет
     * @param id передается в строке запроса
     * @return ResponseEntity в котором содержится найденный объект Nursery
     */
    @Operation(summary = "Поиск приюта по id", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Получение приюта по ID прошло успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Nursery.class),
                            examples = @ExampleObject(
                                    name = "Приют",
                                    description = "Объект найден"
                            )
                    )
            )
    })

    @GetMapping("/{id}")
    public ResponseEntity<Nursery> getShelterById(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.findShelter(id));
    }

    /**
     * Эндпоинт для обновления информации о приюте, может вернуть ShelterNotFoundException если такого приюта нет
     *
     * @param id      PK таблицы nursery_table приюта передается в строке запроса
     * @param nursery в тело запроса предается объект типа Nursery
     * @return ResponseEntity в котором содержится измененный объект Nursery
     */
    @Operation(summary = "Обновление информации об приюте где: id - PK приюта передается в строке запроса, " +
            "Nursery - передается в теле запроса", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Обновление данных о приюте прошло успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Nursery.class),
                            examples = @ExampleObject(
                                    name = "Приют",
                                    description = "Объект приюта изменен"
                            )
                    )
            )
    })

    @PutMapping("/{id}")
    public ResponseEntity<Nursery> updateShelter(@PathVariable Long id, @RequestBody Nursery nursery) {
        return ResponseEntity.ok(shelterService.updateShelter(id, nursery));
    }

    /**
     * Удаление приюта из БД через id, может вернуть ShelterNotFoundException если такого приюта нет
     *
     * @param shelterId PK таблицы nursery_table приюта передается в строке запроса
     * @return ResponseEntity возвращает удаленный объект
     */
    @Operation(summary = "Удаление приюта из БД через id", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Удаление приюта из таблицы nursery_table успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Nursery.class),
                            examples = @ExampleObject(
                                    name = "Приют",
                                    description = "Объект приюта удален"
                            )
                    )
            )
    })
    @DeleteMapping("/{shelterId}")
    public ResponseEntity<Nursery> deleteShelter(@PathVariable Long shelterId) {
        return ResponseEntity.ok(shelterService.removeShelter(shelterId));
    }

    /**
     * Получить список всех приютов постранично
     *
     * @param page номер страницы, передается в параметрах запроса
     * @param size количество элементов в списке
     * @return ResponseEntity в котором содержится список объектов, равное size
     */
    @Operation(summary = "Получить список всех приютов постранично", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Получение по страничного списка выполнено",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Nursery.class)),
                            examples = @ExampleObject(
                                    name = "Список приютов"
                            )
                    )
            )
    })
    @GetMapping(name = "/getNursery", params = {"page","size"})
    public ResponseEntity<List<Nursery>> getNursery(@RequestParam("page") int page, @RequestParam("size") int size) {
        return ResponseEntity.ok(shelterService.getAllShelter(page, size));
    }

    /**
     * @param kindOfAnimal параметр при котором: true - для собак, false - для кошек
     * @param page         номер страницы, передается в параметрах запроса
     * @param size         количество элементов в списке
     * @return ResponseEntity в котором содержится список объектов типа Nursery, количество объектов = size,
     * для собак или для кошек.
     */
    @Operation(summary = "Получить список всех приютов для кошек или собак", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Получение по страничного списка приютов для вида животного выполнено",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Nursery.class)),
                            examples = @ExampleObject(
                                    name = "Список приютов"
                            )
                    )
            )
    })
    @GetMapping("/getShelterForKindOfAnimals")
    public ResponseEntity<List<Nursery>> getShelterForKindOfAnimals(@RequestParam("kindOfAnimal") Boolean kindOfAnimal,
                                                                    @RequestParam("page") Integer page,
                                                                    @RequestParam("size") Integer size) {
        return ResponseEntity.ok(shelterService.getShelterForDog(kindOfAnimal, page, size));
    }
}
