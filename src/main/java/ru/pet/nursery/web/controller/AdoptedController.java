package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.web.service.AdoptedService;

@Tag(name = "Усыновление питомцев", description = "Эндпоинт для присвоение усыновителя к питомцу")
@RequestMapping("/adopters")
@RestController
public class AdoptedController {
    private final AdoptedService adoptedService;

    public AdoptedController(AdoptedService adoptedService) {
        this.adoptedService = adoptedService;
    }

    @PutMapping(params = {"animalId","adopterId"})
    public ResponseEntity<Animal> setAdopterForAnimal(@RequestParam Long animalId, @RequestParam Long adopterId) {
        return ResponseEntity.ok(adoptedService.setAdopterForAnimal(animalId, adopterId));
    }
    @PutMapping("/{animalId}/{days}")
    public ResponseEntity<Animal> prolongTrialForNDays(@PathVariable Long animalId, @PathVariable Integer days) {
        return ResponseEntity.ok(adoptedService.prolongTrialForNDays(animalId,days));
    }

    @PutMapping("/cancelTrial/{animalId}")
    public ResponseEntity<Animal> cancelTrial(@PathVariable Long animalId) {
        return ResponseEntity.ok(adoptedService.cancelTrial(animalId));
    }
}
