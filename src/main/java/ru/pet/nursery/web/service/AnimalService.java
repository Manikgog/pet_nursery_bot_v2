package ru.pet.nursery.web.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.dto.AnimalDTOForUser;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.ImageNotFoundException;
import java.io.*;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;

@Service
public class AnimalService {
    @Value("${path.to.animals.folder}")
    private String animals_images;
    private final AnimalRepo animalRepo;
    private final UserRepo userRepo;
    private final NurseryRepo nurseryRepo;
    public AnimalService(AnimalRepo animalRepo,
                         UserRepo userRepo,
                         NurseryRepo nurseryRepo){
        this.animalRepo = animalRepo;
        this.userRepo = userRepo;
        this.nurseryRepo = nurseryRepo;
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
    public ResponseEntity<HttpStatus> uploadPhoto(Integer animalId, MultipartFile animalPhoto) throws IOException, InterruptedException {
        Optional<Animal> animalFromDB = animalRepo.findById(animalId);
        if(animalFromDB.isEmpty()){
            throw new EntityNotFoundException((long)animalId);
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

        return ResponseEntity.of(Optional.of(HttpStatus.OK));
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
        Animal animal = animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException((long)id));
        if(animal.getPhotoPath() == null){
            throw new ImageNotFoundException("Путь к файлу с изображением отсутствует!");
        }
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

    /**
     * Метод для удаления записи из таблицы animal_table по id
     * @param id - primary key животного в таблице animal_table
     * @return удаленная запись животного
     */
    public ResponseEntity<Animal> delete(Integer id) {
        return ResponseEntity.of(Optional.of(
                animalRepo.findById(id)
                .map(animalToDel -> {
                    animalRepo.delete(animalToDel);
                    return animalToDel;
                })
                .orElseThrow(() -> new EntityNotFoundException(Long.valueOf(id)))));
    }

    public ResponseEntity<HttpStatus> insertDataOfHuman(Integer animalId, Long adoptedId) {
        Animal animalFromDB = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(Long.valueOf(animalId)));
        User userAdopted = userRepo.findById(adoptedId).orElseThrow(() -> new EntityNotFoundException(adoptedId));
        animalRepo.updateWhoTookPetAndTookDate(userAdopted.getTelegramUserId(), LocalDate.now(), animalFromDB.getId());

        return ResponseEntity.of(Optional.of(HttpStatus.OK));
    }

    /**
     * Метод для получения списка животных постранично
     * @param pageNumber - номер страницы получается
     * @param pageSize - количество объектов в листе
     * @return ResponseEntity листа объектов AnimalDTOForUser c нужной для пользователя информацией
     */
    public ResponseEntity<List<AnimalDTOForUser>> getPageList(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        List<Animal> animals = animalRepo.findAll(pageRequest).getContent();
        List<AnimalDTOForUser> resultAnimals = convertListAnimalToListAnimalDTO(animals);
        return ResponseEntity.of(Optional.of(resultAnimals));
    }

    /**
     * Метод для преобразования списка объектов Animal в список объектов AnimalDTOForUser
     * @param animals - список объектов Animal
     * @return список объектов AnimalDTOForUser
     */
    private List<AnimalDTOForUser> convertListAnimalToListAnimalDTO(List<Animal> animals){
        return animals.stream()
                .filter(animal -> animal.getWhoTookPet() == 1)
                .map(this::convertAnimalToAnimalDTO)
                .toList();
    }

    /**
     * Метод для преобразования объекта Animal в объект AnimalDTOForUser
     * @param animal - объект Animal
     * @return animalDTOForUser
     */
    private AnimalDTOForUser convertAnimalToAnimalDTO(Animal animal){
        AnimalDTOForUser animalDTOForUser = new AnimalDTOForUser();
        animalDTOForUser.setId(animal.getId());
        animalDTOForUser.setAnimalName(animal.getAnimalName());
        animalDTOForUser.setAnimalType(animal.getAnimalType());
        animalDTOForUser.setNursery(nurseryRepo.findById(animal.getNurseryId()).get());
        animalDTOForUser.setGender(animal.getGender());
        animalDTOForUser.setBirthDate(animal.getBirthDate());
        animalDTOForUser.setDescription(animal.getDescription());
        return animalDTOForUser;
    }

    /**
     * Метод для изменения строки возвращенного животного
     * @param animalId - идентификатор животного в таблице animal_table
     * @return HttpStatus
     */
    public ResponseEntity<HttpStatus> insertDateOfReturn(Integer animalId) {
        Animal animalFromDB = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(Long.valueOf(animalId)));
        animalRepo.updateReturnDateAnimal(LocalDate.now(), animalFromDB.getId());

        return ResponseEntity.of(Optional.of(HttpStatus.OK));

    }

    /**
     * Метод для возвращения объекта AnimalDTOForUser оп идентификатору животного
     * @param animalId - идентификатор животного в таблице animal_table
     * @return HttpStatus
     */
    public ResponseEntity<AnimalDTOForUser> getById(Integer animalId) {
        Animal animalFromDB = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(Long.valueOf(animalId)));
        return ResponseEntity.of(Optional.of(convertAnimalToAnimalDTO(animalFromDB)));
    }
}
