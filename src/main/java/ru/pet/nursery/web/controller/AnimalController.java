package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping
    @Operation(summary = "загрузка в базу данных сведений о животном")
    public ResponseEntity<Animal> putAnimal(@RequestBody AnimalDTO animalDTO){
        return animalService.uploadAnimal(animalDTO);
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "добавление фотографии животного по его идентификатору")
    public ResponseEntity<HttpStatus> uploadPhotoAnimal(@PathVariable Integer id, @RequestParam MultipartFile animal) throws IOException, InterruptedException {
        return animalService.uploadPhoto(id, animal);
    }

    @PostMapping("/{animalId}/{adoptedId}")
    @Operation(summary = "Вставка данных о человеке, который забрал животное из приюта")
    public ResponseEntity<HttpStatus> insertHumanWhoTookAnimal(@PathVariable Integer animalId, @PathVariable Long adoptedId){
        return animalService.insertDataOfHuman(animalId, adoptedId);
    }

    @PostMapping("/{animalId}/return")
    @Operation(summary = "Вставка данных о возвращении животного в приют")
    public ResponseEntity<HttpStatus> insertDateOfReturningAnimal(@PathVariable Integer animalId){
        return animalService.insertDateOfReturn(animalId);
    }

    @GetMapping("/{id}/photo")
    @Operation(summary = "Получение фотографии животного по его id")
    public void getAnimalPhoto(int id, HttpServletResponse response) throws IOException {
        animalService.getAnimalPhoto(id, response);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Удаление животного из таблицы animal_table по id")
    public ResponseEntity<Animal> deleteAnimal(@PathVariable Integer id){
        return animalService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка животных постранично")
    public ResponseEntity<List<AnimalDTOForUser>> getListByPage(@RequestParam("page") Integer pageNumber, @RequestParam("size") Integer pageSize){
        return animalService.getPageList(pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение питомца по id")
    public ResponseEntity<AnimalDTOForUser> getById(@PathVariable Integer id){
        return animalService.getById(id);
    }

}
