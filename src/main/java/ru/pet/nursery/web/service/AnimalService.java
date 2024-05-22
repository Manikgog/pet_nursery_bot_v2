package ru.pet.nursery.web.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
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
     * @return объект ResponseEntity с объектом Animal, который извлечен из базы данных
     *         после загрузки
     */
    public ResponseEntity<Animal> uploadAnimal(AnimalDTO animalDTO) {
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
     */
    public ResponseEntity uploadPhoto(long animalId, MultipartFile animalPhoto) throws IOException {
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
     * @return строка, содержащая расширения файла
     */
    public String getExtension(String fileName){
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * Метод для получения изображения животного по его идентификатору
     * @param id - идентификатор в таблице animal_table базы данных
     * @param response - объект в котором возвращается изображение животного
     * @throws IOException - исключение ввода-вывода при работе с файлами
     */
    public void getAnimalPhoto(long id, HttpServletResponse response) throws IOException {
        Animal animal = animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
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
     * Метод для получения байтового массива для передачи через телеграм
     * @param id - идентификатор животного
     * @return байтовый массив фотографии
     */
    public byte[] getPhotoByteArray(long id) {

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
    public ResponseEntity<Animal> delete(long id) {
        return ResponseEntity.of(Optional.of(
                animalRepo.findById(id)
                .map(animalToDel -> {
                    animalRepo.delete(animalToDel);
                    return animalToDel;
                })
                .orElseThrow(() -> new EntityNotFoundException(id))));
    }

    /**
     * Метод для изменения столбца с датой усыновления и столбца об том кто усыновил в таблице питомца
     * @param animalId - идентификатор питомца в таблице
     * @param adoptedId - идентификатор усыновителя в таблице
     * @return статус HTTP
     */
    public ResponseEntity<Animal> insertDataOfHuman(long animalId, Long adoptedId) {
        Animal animalFromDB = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(animalId));
        User userAdopted = userRepo.findById(adoptedId).orElseThrow(() -> new EntityNotFoundException(adoptedId));
        // проверка на то, что у человека уже есть животное на испытательном сроке
        if(!animalRepo.findByUser(userAdopted).isEmpty()){
            throw new UserNotValidException(userAdopted.getFirstName() + " " + userAdopted.getLastName() + " уже взял животное на испытательный срок.");
        }
        animalFromDB.setUser(userAdopted);
        animalFromDB.setTookDate(LocalDate.now());
        Animal newAnimal = animalRepo.save(animalFromDB);
        return ResponseEntity.of(Optional.of(newAnimal));
    }

    /**
     * Метод для получения списка животных, которые
     * находятся в питомниках постранично
     * @param pageNumber - номер страницы получается
     * @param pageSize - количество объектов в листе
     * @return ResponseEntity листа объектов AnimalDTOForUser c нужной для пользователя информацией
     */
    public ResponseEntity<List<AnimalDTOForUser>> getPageList(Integer pageNumber, Integer pageSize) {
        validator.validatePageNumber(pageNumber);
        validator.validatePageSize(pageSize);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        List<Animal> animals = animalRepo.findByUserIsNull(pageRequest);
        List<AnimalDTOForUser> resultAnimals = convertListAnimalToListAnimalDTO(animals);
        return ResponseEntity.of(Optional.of(resultAnimals));
    }

    /**
     * Метод для преобразования списка объектов Animal в список объектов AnimalDTOForUser
     * @param animals - список объектов Animal
     * @return список объектов AnimalDTOForUser
     */
    public List<AnimalDTOForUser> convertListAnimalToListAnimalDTO(List<Animal> animals){
        AnimalDTOForUserMapper animalDTOForUserMapper = new AnimalDTOForUserMapper();
        return animals.stream()
                .filter(animal -> animal.getUser() == null)
                .map(animalDTOForUserMapper::perform)
                .toList();
    }

    /**
     * Метод для изменения строки возвращенного животного
     * @param animalId - идентификатор животного в таблице animal_table
     * @return HttpStatus
     */
    public ResponseEntity<Animal> insertDateOfReturn(long animalId) {
        Animal animalOld = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(animalId));
        animalOld.setPetReturnDate(LocalDate.now());
        animalOld.setUser(null);
        animalOld.setTookDate(null);
        Animal newAnimal = animalRepo.save(animalOld);
        return ResponseEntity.of(Optional.of(newAnimal));
    }

    /**
     * Метод для возвращения объекта AnimalDTOForUser по идентификатору животного
     * @param animalId - идентификатор животного в таблице animal_table
     * @return HttpStatus
     */
    public ResponseEntity<AnimalDTOForUser> getById(long animalId) {
        AnimalDTOForUserMapper animalDTOForUserMapper = new AnimalDTOForUserMapper();
        Animal animalFromDB = animalRepo.findById(animalId).orElseThrow(() -> new EntityNotFoundException(animalId));
        return ResponseEntity.of(Optional.of(animalDTOForUserMapper.perform(animalFromDB)));
    }

    /**
     * Метод для получения всего списка животных
     * @return - ResponseEntity<List<Animal>>
     */
    public ResponseEntity<List<Animal>> getAll() {
        return ResponseEntity.of(Optional.of(animalRepo.findAll()));
    }

    /**
     * Метод для получения из базы данных только котов или только собак
     */
    public List<Animal> getAllAnimalsByType(AnimalType animalType){
        return animalRepo.findByAnimalType(animalType);
    }


    /**
     * Метод для получения объекта Animal
     */
    public Animal get(long id){
        return animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }
}
