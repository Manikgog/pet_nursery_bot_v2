package ru.pet.nursery.web.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.exception.ImageNotFoundException;
import ru.pet.nursery.web.exception.NotFoundException;
import java.io.*;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;

@Service
public class AnimalService {
    @Value("${path.to.animals.folder}")
    private String animals_images;
    private final AnimalRepo animalRepo;
    public AnimalService(AnimalRepo animalRepo){
        this.animalRepo = animalRepo;
    }

    /**
     * Метод для загрузки данных о животном в таблицу animal_table базы данных
     * @param animalDTO - объект для передачи нужных полей
     * @return объект ResponseEntity с объектом Animal, который извлечен из базы данных
     *         после загрузки
     */
    public ResponseEntity<Animal> uploadAnimal(AnimalDTO animalDTO) {
        Animal newAnimal = new Animal();
        newAnimal.setAnimalName(animalDTO.getAnimalName());
        newAnimal.setAnimalType(animalDTO.getAnimalType());
        newAnimal.setDescription(animalDTO.getDescription());
        newAnimal.setGender(animalDTO.getGender());
        newAnimal.setBirthDate(animalDTO.getBirthDate());
        newAnimal.setNurseryId(animalDTO.getNurseryId());
        newAnimal.setWhoTookPet(1L);                            // если стоит цифра 1 значит животное никто не взял
        Animal animalFromDB = animalRepo.save(newAnimal);
        return ResponseEntity.of(Optional.of(animalFromDB));
    }

    /**
     * Метод для записи файла с изображением питомца на диск, а пути к этому файлу в базу данных.
     * @param animalId - идентификатор животного в таблице animal_table базы данных
     * @param animalPhoto - файл с изображением животного
     * @return ResponseEntity<Animal> - объект ResponseEntity содержащий объект Animal взятый из базы данных с
     *                                  измененным полем photo_path
     * @throws IOException - исключение ввода-вывода при работе с файлами
     * @throws InterruptedException - исключение добавлено из-за наличия метода sleep
     */
    public ResponseEntity<Animal> uploadPhoto(Integer animalId, MultipartFile animalPhoto) throws IOException, InterruptedException {
        Optional<Animal> animalFromDB = animalRepo.findById(animalId);
        if(animalFromDB.isEmpty()){
            throw new NotFoundException((long)animalId);
        }
        String strPath = System.getProperty("user.dir");
        strPath += animals_images;
        Path path = Path.of(strPath);
        Path filePath = Path.of(path.toString(), animalId + "." + getExtention(Objects.requireNonNull(animalPhoto.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try(InputStream is = animalPhoto.getInputStream();
            OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ){
            bis.transferTo(bos);
        }

        animalRepo.updatePhotoPathColumn(filePath.toString(), animalId);
        Thread.sleep(50);                                           // Задержка добавлена для достоверной записи изменений в таблицу базы данных.
                                                                          //  Без использования исключения возвращается не измененная строка таблицы
        return ResponseEntity.of(animalRepo.findById(animalId));
    }

    /**
     * Метод для поиска и возвращения строки, содержащей расширения файла
     * @param fileName - имя файла
     * @return строка, содержащая расширения файла
     */
    public String getExtention(String fileName){
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * Метод для получения изображения животного по его идентификатору
     * @param id - идентификатор в таблице animal_table базы данных
     * @param response - объект в котором возвращается изображение животного
     * @throws IOException - исключение ввода-вывода при работе с файлами
     */
    public void getAnimalPhoto(int id, HttpServletResponse response) throws IOException {
        Animal animal = animalRepo.findById(id).orElseThrow(() -> new NotFoundException((long)id));
        Path path = Paths.get(animal.getPhotoPath());
        if(!Files.exists(path)){
            throw new ImageNotFoundException("Файл с изображением не найден!");
        }
        int size;
        SeekableByteChannel seekableByteChannel = null;
        try{
            seekableByteChannel = Files.newByteChannel(path, EnumSet.of(READ));
            size = (int)seekableByteChannel.size();
        } catch (IOException e) {
            throw new ImageNotFoundException(e.getMessage());
        } finally {
            seekableByteChannel.close();
        }
        try(InputStream is = Files.newInputStream(path);
            OutputStream os = response.getOutputStream()){
            response.setContentType(Files.probeContentType(path));
            response.setContentLength(size);
            is.transferTo(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
