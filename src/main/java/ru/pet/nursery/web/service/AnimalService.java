package ru.pet.nursery.web.service;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.manager.AbstractManager;
import ru.pet.nursery.mapper.AnimalDTOForUserMapper;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.dto.AnimalDTOForUser;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.ImageNotFoundException;
import ru.pet.nursery.web.exception.UserNotValidException;
import ru.pet.nursery.web.validator.Validator;
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
    private final Logger logger = LoggerFactory.getLogger(AbstractManager.class);
    @Value("${path.to.animals.folder}")
    private String animals_images;
    private final AnimalRepo animalRepo;
    private final UserRepo userRepo;
    private final NurseryRepo nurseryRepo;
    private final Validator validator;
    public AnimalService(AnimalRepo animalRepo,
                         UserRepo userRepo,
                         NurseryRepo nurseryRepo){
        this.animalRepo = animalRepo;
        this.userRepo = userRepo;
        this.nurseryRepo = nurseryRepo;
        this.validator = new Validator(nurseryRepo);
    }

    /**
     * Метод для загрузки данных о животном в таблицу animal_table базы данных
     * @param animalDTO - объект для передачи нужных полей
     * @return объект Animal
     */
    public Animal uploadAnimal(AnimalDTO animalDTO) {
        logger.info("Method uploadAnimal of AnimalService class with parameter AnimalDTO -> {}", animalDTO);
        validator.validateAnimalDTO(animalDTO);
        Nursery nursery = nurseryRepo.findById(animalDTO.getNurseryId())
                .orElseThrow(() -> new EntityNotFoundException(animalDTO.getNurseryId()));
        Animal newAnimal = new Animal();
        newAnimal.setAnimalName(animalDTO.getAnimalName());
        newAnimal.setAnimalType(animalDTO.getAnimalType());
        newAnimal.setDescription(animalDTO.getDescription());
        newAnimal.setGender(animalDTO.getGender());
        newAnimal.setBirthDate(animalDTO.getBirthDate());
        newAnimal.setNursery(nursery);
        newAnimal.setUser(null);                            // если стоит цифра 1 значит животное никто не взял
        return animalRepo.save(newAnimal);
    }

    /**
     * Метод для записи файла с изображением питомца на диск, а пути к этому файлу в базу данных.
     * @param animalId - идентификатор животного в таблице animal_table базы данных
     * @param animalPhoto - файл с изображением животного
     * @return ResponseEntity<Animal> - объект ResponseEntity содержащий объект Animal взятый из базы данных с
     *                                  измененным полем photo_path
     * @throws IOException - исключение ввода-вывода при работе с файлами
     */
    public ResponseEntity uploadPhoto(long animalId, MultipartFile animalPhoto) throws IOException {
        logger.info("Method uploadPhoto of AnimalService class with parameters long -> {}, MultipartFile -> {}", animalId, animalPhoto);
        Optional<Animal> animalFromDB = animalRepo.findById(animalId);
        if(animalFromDB.isEmpty()){
            throw new EntityNotFoundException(animalId);
        }
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\";
        }else{
            strPath += "/";
        }
        strPath += animals_images;
        Path path = Path.of(strPath);
        Path filePath = Path.of(path.toString(), animalId + "." + getExtension(Objects.requireNonNull(animalPhoto.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try(InputStream is = animalPhoto.getInputStream();
            OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ){
            bis.transferTo(bos);
        }

        animalFromDB.get().setPhotoPath(filePath.toString());
        animalRepo.save(animalFromDB.get());

        return ResponseEntity.ok().build();
    }

    /**
     * Метод для поиска и возвращения строки, содержащей расширения файла
     * @param fileName - имя файла
     * @return строка, содержащая расширение файла
     */
    public String getExtension(String fileName){
        logger.info("Method getExtension of AnimalService class with parameters String -> {}", fileName);
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * Метод для получения изображения животного по его идентификатору
     * @param id - идентификатор в таблице animal_table базы данных
     * @param response - объект в котором возвращается изображение животного
     */
    public void getAnimalPhoto(long id, HttpServletResponse response) {
        logger.info("Method getAnimalPhoto of AnimalService class with parameters long -> {}, HttpServletResponse -> {}", id, response);
        Animal animal = animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        if(animal.getPhotoPath() == null){
            throw new ImageNotFoundException("Путь к файлу с изображением отсутствует!");
        }
        Path path = Paths.get(animal.getPhotoPath());
        if(!Files.exists(path)){
            throw new ImageNotFoundException("Файл с изображением не найден!");
        }
        int size;
        SeekableByteChannel seekableByteChannel;
        try(SeekableByteChannel sbc = Files.newByteChannel(path, EnumSet.of(READ))){
            seekableByteChannel = sbc;
            size = (int)seekableByteChannel.size();
        } catch (IOException e) {
            throw new ImageNotFoundException(e.getMessage());
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
     * Метод для получения байтового массива для передачи через телеграм
     * @param id - идентификатор животного
     * @return байтовый массив фотографии
     */
    public byte[] getPhotoByteArray(long id) {
        logger.info("Method getPhotoByteArray of AnimalService class with parameter long -> {}", id);
        Animal animal = animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        if(animal.getPhotoPath() == null){
            throw new ImageNotFoundException("Путь к файлу с изображением отсутствует!");
        }
        Path path = Paths.get(animal.getPhotoPath());
        if(!Files.exists(path)){
            throw new ImageNotFoundException("Файл с изображением не найден!");
        }
        byte[] photoByteArray;
        try(InputStream is = Files.newInputStream(path)){
            photoByteArray = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return photoByteArray;
    }

    /**
     * Метод для удаления записи из таблицы animal_table по id
     * @param id - primary key животного в таблице animal_table
     * @return удаленная запись животного
     */
    public Animal delete(long id) {
        logger.info("Method delete of AnimalService class with parameter long -> {}", id);
        return animalRepo.findById(id)
                .map(animalToDel -> {
                    animalRepo.delete(animalToDel);
                    return animalToDel;
                })
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    /**
     * Метод для изменения столбца с датой усыновления и столбца об том кто усыновил в таблице питомца
     * @param animalId - идентификатор питомца в таблице
     * @param adoptedId - идентификатор усыновителя в таблице
     * @return Animal - объект питомца
     */
    public Animal insertDataOfHuman(long animalId, Long adoptedId) {
        logger.info("Method insertDataOfHuman of AnimalService class with parameter long -> {}, Long -> {}", animalId, adoptedId);
        Animal animalFromDB = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(animalId));
        User userAdopted = userRepo.findById(adoptedId).orElseThrow(() -> new EntityNotFoundException(adoptedId));
        // проверка на то, что у человека уже есть животное на испытательном сроке
        if(!animalRepo.findByUser(userAdopted).isEmpty()){
            throw new UserNotValidException(userAdopted.getFirstName() + " " + userAdopted.getLastName() + " уже взял животное на испытательный срок.");
        }
        animalFromDB.setUser(userAdopted);
        animalFromDB.setTookDate(LocalDate.now());
        return animalRepo.save(animalFromDB);
    }

    /**
     * Метод для получения списка животных, которые
     * находятся в питомниках постранично
     * @param pageNumber - номер страницы получается
     * @param pageSize - количество объектов в листе
     * @return список объектов AnimalDTOForUser
     */
    public List<AnimalDTOForUser> getPageList(Integer pageNumber, Integer pageSize) {
        logger.info("Method getPageList of AnimalService class with parameter Integer -> {}, Integer -> {}", pageNumber, pageSize);
        validator.validatePageNumber(pageNumber);
        validator.validatePageSize(pageSize);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        List<Animal> animals = animalRepo.findByUserIsNull(pageRequest);
        return convertListAnimalToListAnimalDTO(animals);
    }

    /**
     * Метод для преобразования списка объектов Animal в список объектов AnimalDTOForUser
     * @param animals - список объектов Animal
     * @return AnimalDTOForUser
     */
    public List<AnimalDTOForUser> convertListAnimalToListAnimalDTO(List<Animal> animals){
        logger.info("Method convertListAnimalToListAnimalDTO of AnimalService class with parameters List<Animal> -> {}", animals);
        AnimalDTOForUserMapper animalDTOForUserMapper = new AnimalDTOForUserMapper();
        return animals.stream()
                .filter(animal -> animal.getUser() == null)
                .map(animalDTOForUserMapper::perform)
                .toList();
    }

    /**
     * Метод для изменения строки возвращенного животного
     * @param animalId - идентификатор животного в таблице animal_table
     * @return Animal - объект питомца
     */
    public Animal insertDateOfReturn(long animalId) {
        logger.info("Method insertDateOfReturn of AnimalService class with parameter long -> {}", animalId);
        Animal animalOld = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(animalId));
        animalOld.setPetReturnDate(LocalDate.now());
        animalOld.setUser(null);
        animalOld.setTookDate(null);
        return animalRepo.save(animalOld);
    }

    /**
     * Метод для возвращения объекта AnimalDTOForUser по идентификатору животного
     * @param animalId - идентификатор животного в таблице animal_table
     * @return AnimalDTOForUser - объект животного с полями, которые нужны пользователю
     */
    public AnimalDTOForUser getById(long animalId) {
        logger.info("Method getById of AnimalService class with parameter long -> {}", animalId);
        AnimalDTOForUserMapper animalDTOForUserMapper = new AnimalDTOForUserMapper();
        Animal animalFromDB = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(animalId));
        return animalDTOForUserMapper.perform(animalFromDB);
    }

    /**
     * Метод для получения всего списка животных
     * @return - List<Animal> - список животных во всех приютах
     */
    public List<Animal> getAll() {
        logger.info("Method getAll of AnimalService class");
        return animalRepo.findAll();
    }

    /**
     * Метод для получения из базы данных только котов или только собак
     */
    public List<Animal> getAllAnimalsByType(AnimalType animalType){
        logger.info("Method getAllAnimalsByType of AnimalService class with parameter AnimalType -> {}", animalType);
        return animalRepo.findByAnimalType(animalType);
    }


    /**
     * Метод для получения объекта Animal
     */
    public Animal get(long id){
        logger.info("Method get of AnimalService class with parameter long -> {}", id);
        return animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }
}
