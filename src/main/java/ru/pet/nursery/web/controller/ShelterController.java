package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
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
    @PostMapping
    @Operation(summary = "Добавление приютов в БД")
    public ResponseEntity<Nursery> putShelter(@RequestBody Nursery nursery) {
        return ResponseEntity.ok(shelterService.addShelter(nursery));
    }

    /**
     * Эндпоинт для поиска приюта по id, может вернуть ShelterNotFoundException если такого приюта нет
     * @param id передается в строке запроса
     * @return ResponseEntity в котором содержится найденный объект Nursery
     */
    @GetMapping("/{id}")
    @Operation(summary = "Поиск приюта по id")
    public ResponseEntity<Nursery> getShelterById(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.findShelter(id));
    }

    /**
     * Эндпоинт для обновления информации о приюте, может вернуть ShelterNotFoundException если такого приюта нет
     * @param id PK таблицы nursery_table приюта передается в строке запроса
     * @param nursery в тело запроса предается объект типа Nursery
     * @return ResponseEntity в котором содержится измененный объект Nursery
     */

    @PutMapping("/{id}")
    @Operation(summary = "Обновление информации об приюте где: id - PK приюта передается в строке запроса, " +
            "Nursery - передается в теле запроса")
    public ResponseEntity<Nursery> updateShelter(@PathVariable Long id, @RequestBody Nursery nursery) {
        return ResponseEntity.ok(shelterService.updateShelter(id, nursery));
    }

    /**
     * Удаление приюта из БД через id, может вернуть ShelterNotFoundException если такого приюта нет
     * @param shelterId PK таблицы nursery_table приюта передается в строке запроса
     * @return ResponseEntity возвращает удаленный объект
     */

    @DeleteMapping("/{shelterId}")
    @Operation(summary = "Удаление приюта из БД через id")
    public ResponseEntity<Nursery> deleteShelter(@PathVariable Long shelterId) {
        return ResponseEntity.ok(shelterService.removeShelter(shelterId));
    }

    /**
     * Получить список всех приютов постранично
     * @param page номер страницы, передается в параметрах запроса
     * @param size количество элементов в списке
     * @return ResponseEntity в котором содержится список объектов, равное size
     */

    @GetMapping
    @Operation(summary = "Получить список всех приютов постранично")
    public ResponseEntity<List<Nursery>> getNursery(@RequestParam @Min(1) int page, @RequestParam @Min(1) int size) {
        return ResponseEntity.ok(shelterService.getAllShelter(page, size));
    }

    /**
     *
     * @param kindOfAnimal параметр при котором: true - для собак, false - для кошек
     * @param page номер страницы, передается в параметрах запроса
     * @param size количество элементов в списке
     * @return ResponseEntity в котором содержится список объектов типа Nursery, количество объектов = size,
     * для собак или для кошек.
     */
    @GetMapping("/getShelterFor")
    @Operation(summary = "Получить список всех приютов для кошек или собак")
    public ResponseEntity<List<Nursery>> getShelterForKindOfAnimals(@RequestParam Boolean kindOfAnimal,
                                                                    @RequestParam @Min(1) int page,
                                                                    @RequestParam @Min(1) int size) {
        return ResponseEntity.ok(shelterService.getShelterForDog(kindOfAnimal, page, size));
    }
}
