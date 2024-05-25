package ru.pet.nursery.web.service;

import net.datafaker.Faker;
import net.datafaker.service.FakeValuesService;
import net.datafaker.service.FakerContext;
import net.datafaker.service.RandomService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.exception.*;
import ru.pet.nursery.web.validator.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.READ;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.pet.nursery.Constants.*;
import static ru.pet.nursery.web.Constants.*;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceMockTest {
    private String animalImagesDir = "\\test_animal_images";
    @Mock
    AnimalRepo animalRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    NurseryRepo nurseryRepo;
    @Mock
    Validator validator;
    @InjectMocks
    AnimalService animalService;
    private final Faker faker = new Faker();

    @Test
    public void uploadAnimal_positiveTest(){
        when(animalRepo.save(VASKA)).thenReturn(VASKA_FROM_DB);
        when(nurseryRepo.findById(1L)).thenReturn(Optional.ofNullable(NURSERY_1));
        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(USER));
        ResponseEntity<Animal> actualResult = animalService.uploadAnimal(VASKA_DTO);
        Assertions.assertEquals(VASKA.getAnimalName(), actualResult.getBody().getAnimalName());
        Assertions.assertEquals(VASKA.getAnimalType(), actualResult.getBody().getAnimalType());
        Assertions.assertEquals(VASKA.getUser(), actualResult.getBody().getUser());
        Assertions.assertEquals(VASKA.getDescription(), actualResult.getBody().getDescription());

        when(animalRepo.save(PALKAN)).thenReturn(PALKAN_FROM_DB);
        when(nurseryRepo.findById(2L)).thenReturn(Optional.ofNullable(NURSERY_2));
        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(USER));
        actualResult = animalService.uploadAnimal(PALKAN_DTO);
        Assertions.assertEquals(PALKAN.getAnimalName(), actualResult.getBody().getAnimalName());
        Assertions.assertEquals(PALKAN.getAnimalType(), actualResult.getBody().getAnimalType());
        Assertions.assertEquals(PALKAN.getUser(), actualResult.getBody().getUser());
        Assertions.assertEquals(PALKAN.getDescription(), actualResult.getBody().getDescription());
    }


    @Test
    public void uploadAnimal_negativeTestByNameNull(){
        // проверка при получении объекта с невалидным полем имени null
        AnimalDTO animal = new AnimalDTO();
        animal.setAnimalName(null);
        animal.setAnimalType(AnimalType.CAT);
        animal.setBirthDate(LocalDate.now().minusYears(1));
        animal.setGender(Gender.MALE);
        animal.setNurseryId(1L);
        animal.setDescription(faker.name().malefirstName());

        Assertions.assertThrows(IllegalFieldException.class, () -> animalService.uploadAnimal(animal));
    }


    @Test
    public void uploadAnimal_negativeTestByNameEmpty(){
        // проверка при получении объекта с пустым полем имени
        AnimalDTO animal = new AnimalDTO();
        animal.setAnimalName("");
        animal.setAnimalType(AnimalType.CAT);
        animal.setBirthDate(LocalDate.now().minusYears(1));
        animal.setGender(Gender.MALE);
        animal.setNurseryId(1L);
        animal.setDescription(faker.name().malefirstName());
        when(nurseryRepo.findById(1L)).thenReturn(Optional.ofNullable(NURSERY_1));
        Assertions.assertThrows(IllegalFieldException.class, () -> animalService.uploadAnimal(animal));
    }


    @Test
    public void uploadAnimal_negativeTestByNameSpaces(){
        // проверка при получении объекта с полем имени, содержащим одни пробелы
        AnimalDTO animal = new AnimalDTO();
        animal.setAnimalName("    ");
        animal.setAnimalType(AnimalType.CAT);
        animal.setBirthDate(LocalDate.now().minusYears(1));
        animal.setGender(Gender.MALE);
        animal.setNurseryId(1L);
        animal.setDescription(faker.name().malefirstName());
        when(nurseryRepo.findById(1L)).thenReturn(Optional.ofNullable(NURSERY_1));
        Assertions.assertThrows(IllegalFieldException.class, () -> animalService.uploadAnimal(animal));
    }


    @Test
    public void uploadAnimal_negativeTestByAnimalTypeNull(){
        // проверка при получении объекта с полем AnimalType равным null
        AnimalDTO animal = new AnimalDTO();
        animal.setAnimalName(faker.name().name());
        animal.setAnimalType(null);
        animal.setBirthDate(LocalDate.now().minusYears(1));
        animal.setGender(Gender.MALE);
        animal.setNurseryId(1L);
        animal.setDescription(faker.name().malefirstName());
        when(nurseryRepo.findById(1L)).thenReturn(Optional.ofNullable(NURSERY_1));
        Assertions.assertThrows(IllegalFieldException.class, () -> animalService.uploadAnimal(animal));
    }


    @Test
    public void uploadAnimal_negativeTestByBirthDateInFuture(){
        // проверка при получении объекта с полем BirthDate на один день в будущем
        AnimalDTO animal = new AnimalDTO();
        animal.setAnimalName(faker.name().name());
        animal.setAnimalType(AnimalType.CAT);
        animal.setBirthDate(LocalDate.now().plusDays(1));
        animal.setGender(Gender.MALE);
        animal.setNurseryId(1L);
        animal.setDescription(faker.name().malefirstName());
        when(nurseryRepo.findById(1L)).thenReturn(Optional.ofNullable(NURSERY_1));
        Assertions.assertThrows(IllegalFieldException.class, () -> animalService.uploadAnimal(animal));
    }



    @Test
    public void uploadAnimal_negativeTestByBirthDateNull(){
        // проверка при получении объекта с полем BirthDate равным null
        AnimalDTO animal = new AnimalDTO();
        animal.setAnimalName(faker.name().name());
        animal.setAnimalType(AnimalType.CAT);
        animal.setBirthDate(null);
        animal.setGender(Gender.MALE);
        animal.setNurseryId(1L);
        animal.setDescription(faker.name().malefirstName());
        when(nurseryRepo.findById(1L)).thenReturn(Optional.ofNullable(NURSERY_1));
        Assertions.assertThrows(IllegalFieldException.class, () -> animalService.uploadAnimal(animal));
    }



    @Test
    public void uploadAnimal_negativeTestByNurseryIdNotInDataBase(){
        // проверка при получении объекта с полем NurseryId, которого нет в базе данных
        AnimalDTO animal = new AnimalDTO();
        animal.setAnimalName(faker.name().name());
        animal.setAnimalType(AnimalType.CAT);
        animal.setBirthDate(LocalDate.now().minusYears(1));
        animal.setGender(Gender.MALE);
        animal.setNurseryId(1L);
        animal.setDescription(faker.name().malefirstName());

        Assertions.assertThrows(IllegalFieldException.class, () -> animalService.uploadAnimal(animal));
    }


    @Test
    public void uploadPhoto_negativeTestInIsNotInDataBase(){
        Integer id = 0;
        String name = "Polya";
        byte[] array = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        MultipartFile photo = new MockMultipartFile(name, array);
        Assertions.assertThrows(EntityNotFoundException.class, () -> animalService.uploadPhoto(id, photo));

    }

    /**
     * Проверка работы метода getExtension, который
     * получает название файла и возвращает его расширение
     */
    @Test
    public void getExtension_positiveTest(){
        FakeValuesService fakeValuesService = new FakeValuesService();
        FakerContext fakerContext = new FakerContext(Locale.UK, new RandomService());
        for (int i = 0; i < 5; i++) {
            String extension = fakeValuesService.regexify("(\\.[a-z][a-z][a-z])", fakerContext);
            String fileName = faker.name().name();
            String fullName = fileName + extension;
            Assertions.assertEquals(extension.substring(1), animalService.getExtension(fullName));
        }
    }


    @Test
    public void getPhotoByteArray_positiveTest() throws IOException {
        User user = new User();
        user.setTelegramUserId(1L);
        String strPath = System.getProperty("user.dir");
        strPath += this.animalImagesDir;
        strPath += "\\1.jpg";
        Path path = Path.of(strPath);
        int id = 1;
        Animal animal = new Animal();
        animal.setId((long) id);
        animal.setUser(user);
        animal.setPhotoPath(path.toString());
        animal.setAnimalName(faker.cat().name());
        animal.setAnimalType(AnimalType.CAT);
        animal.setBirthDate(LocalDate.now().minusYears(1));
        animal.setGender(Gender.MALE);
        animal.setDescription(faker.animal().scientificName());

        byte[] expectedArray = getByteArray(path);
        when(animalRepo.findById(any())).thenReturn(Optional.ofNullable(animal));

        for (int i = 0; i < 100; i++) {
            Assertions.assertEquals(expectedArray[i], animalService.getPhotoByteArray(1L)[i]);
        }

        Assertions.assertEquals(expectedArray.length, animalService.getPhotoByteArray(1L).length);

    }


    /**
     * Метод для получения байтового массива из файла с изображением
     * @param path - путь к файлу
     * @return байтовый массив
     * @throws IOException - checked исключение
     */
    private byte[] getByteArray(Path path) throws IOException {
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
        byte[] photoByteArray = new byte[size];
        try(InputStream is = Files.newInputStream(path)){
            photoByteArray = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return photoByteArray;
    }



    @Test
    public void getPhotoByteArray_negativeTestByNotValidId(){
        int id = 0;
        Assertions.assertThrows(EntityNotFoundException.class, () -> animalService.getPhotoByteArray((long) id));
    }


    @Test
    public void delete_positiveTest(){
        User user = new User();
        user.setTelegramUserId(1L);
        String strPath = System.getProperty("user.dir");
        strPath += this.animalImagesDir;
        strPath += "\\1.jpg";
        Path path = Path.of(strPath);
        int id = 1;
        Animal animal = new Animal();
        animal.setId((long) id);
        animal.setUser(user);
        animal.setPhotoPath(path.toString());
        animal.setAnimalName(faker.cat().name());
        animal.setAnimalType(AnimalType.CAT);
        animal.setBirthDate(LocalDate.now().minusYears(1));
        animal.setGender(Gender.MALE);
        animal.setDescription(faker.animal().scientificName());

        when(animalRepo.findById((long) id)).thenReturn(Optional.of(animal));

        Assertions.assertEquals(ResponseEntity.of(Optional.of(animal)), animalService.delete((long) id));
    }



    @Test
    public void delete_negativeTestByNotValidId(){
        int id = 0;
        Assertions.assertThrows(EntityNotFoundException.class, () -> animalService.delete((long) id));
    }


    @Test
    public void insertDataOfHuman_positiveTest(){
        int animalId = 1;
        long adopterId = 1;
        Animal animal = new Animal();
        animal.setId((long) animalId);
        User user = new User();
        user.setTelegramUserId(adopterId);

        when(animalRepo.findById((long) animalId)).thenReturn(Optional.of(animal));
        when(userRepo.findById(adopterId)).thenReturn(Optional.of(user));
        when(animalRepo.save(animal)).thenReturn(animal);
        Assertions.assertEquals(ResponseEntity.of(Optional.of(animal)), animalService.insertDataOfHuman((long) animalId, adopterId));
    }



    @Test
    public void insertDataOfHuman_negativeTestByNotValidAnimalId(){
        int animalId = -1;
        long adopterId = 1;
        Assertions.assertThrows(EntityNotFoundException.class, () -> animalService.insertDataOfHuman((long) animalId, adopterId));
    }


    @Test
    public void insertDataOfHuman_negativeTestByNotValidAdopterId(){
        int animalId = 1;
        long adopterId = -1;
        Assertions.assertThrows(EntityNotFoundException.class, () -> animalService.insertDataOfHuman((long) animalId, adopterId));
    }



    @Test
    public void getPageList_negativeTestByNotValidPageAndSize(){
        int finalPage = 0;
        int finalSize = 1;
        Assertions.assertThrows(PageNumberException.class, () -> animalService.getPageList(finalPage, finalSize));

        int finalPage1 = 1;
        int finalSize1 = 0;
        Assertions.assertThrows(PageSizeException.class, () -> animalService.getPageList(finalPage1, finalSize1));
    }


    @Test
    public void insertDateOfReturn_positiveTest(){
        int animalId = 1;
        long adopterId = 1;
        Animal animal = new Animal();
        animal.setId((long) animalId);
        User user = new User();
        user.setTelegramUserId(adopterId);

        when(animalRepo.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(animalRepo.save(animal)).thenReturn(animal);
        Assertions.assertEquals(ResponseEntity.of(Optional.of(animal)), animalService.insertDateOfReturn((long) animalId));
    }


    @Test
    public void insertDateOfReturn_negativeTest(){
        int notValidId = -1;
        Mockito.doThrow(new EntityNotFoundException((long) notValidId)).when(animalRepo).findById((long) notValidId);
        Assertions.assertThrows(EntityNotFoundException.class, () -> animalService.insertDateOfReturn((long) notValidId));
    }

}
