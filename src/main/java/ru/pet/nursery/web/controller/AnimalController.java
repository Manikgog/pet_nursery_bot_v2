package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.service.AnimalService;
import java.io.IOException;

@Tag(name = "Домашние животные", description = "Эндпоинты для работы с домашними животными содержащимися в приюте")
@RequestMapping("/animal")
@RestController
public class AnimalController {
    private final AnimalService animalService;
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }
    @PostMapping
    @Operation(summary = "загрузка в базу данных сведений о животном")
    public ResponseEntity<Animal> putAnimal(@RequestBody AnimalDTO animalDTO){
        return animalService.uploadAnimal(animalDTO);
    }

    @PostMapping(value = "/{id}/animal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "добавление фотографии животного по его идентификатору")
    public ResponseEntity<Animal> uploadPhotoAnimal(@PathVariable Integer id, @RequestParam MultipartFile animal) throws IOException, InterruptedException {
        return animalService.uploadPhoto(id, animal);
    }

    @GetMapping("/{id}/photo")
    @Operation(summary = "")
    public void getAnimalPhoto(int id, HttpServletResponse response) throws IOException {
        animalService.getAnimalPhoto(id, response);
    }
    // написать методы:
    // для удаления животного из таблицы
    // для вставки в строку данных о человеке, который забрал животное и даты когда это произошло
    // для вставки в строку данных о возвращении животного в приют
    // для получения списка с фотографиями животных
    // для получения списка животных по нескольку строк
}
