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

    @PostMapping
    @Operation(summary = "Добавление приютов в БД")
    public ResponseEntity<Nursery> putShelter(@RequestBody Nursery nursery) {
        return ResponseEntity.ok(shelterService.addShelter(nursery));
    }

    @GetMapping
    @Operation(summary = "Поиск приюта по id")
    public ResponseEntity<Nursery> getShelterById(@RequestPart Long id) {
        return ResponseEntity.ok(shelterService.findShelter(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление информации об приюте где: id - PK приюта (передается в строке запроса, " +
            "Nursery - передается в теле запроса")
    public ResponseEntity<Nursery> updateShelter(@PathVariable Long id, @RequestBody Nursery nursery) {
        return ResponseEntity.ok(shelterService.updateShelter(id, nursery));
    }

    @DeleteMapping("/{shelterId}")
    @Operation(summary = "Удаление приюта из БД через id")
    public ResponseEntity<Nursery> deleteShelter(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.removeShelter(id));
    }

    @GetMapping
    @Operation(summary = "Получить список всех приютов постранично")
    public ResponseEntity<List<Nursery>> getNursery(@RequestParam @Min(1) int page, @RequestParam @Min(1) int size) {
        return ResponseEntity.ok(shelterService.getAllShelter(page, size));
    }

    @GetMapping("/getShelterFor/{kindOfAnimal}")
    public ResponseEntity<List<Nursery>> getShelterForKindOfAnimals(@RequestParam Boolean kindOfAnimal,
                                                                    @RequestParam @Min(1) int page,
                                                                    @RequestParam @Min(1) int size) {
        return ResponseEntity.ok(shelterService.getShelterForDog(kindOfAnimal, page, size));
    }
}
