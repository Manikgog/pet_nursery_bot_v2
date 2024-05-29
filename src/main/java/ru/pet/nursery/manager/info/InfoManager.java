package ru.pet.nursery.manager.info;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.service.IAnimalService;
import ru.pet.nursery.web.service.IShelterService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.pet.nursery.data.CallbackData.*;

@Component
public class InfoManager extends AbstractManager {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final IShelterService shelterService;
    private final AnimalRepo animalRepo;
    private final IAnimalService animalService;
    private final Map<Long, Long> userId_animalId = new HashMap<>(); // словарь соответствия chatId идентификатору животного, чтобы передавать новую фотографию

    public InfoManager(AnswerMethodFactory answerMethodFactory,
                       KeyboardFactory keyboardFactory,
                       TelegramBot telegramBot,
                       IShelterService shelterService,
                       AnimalRepo animalRepo,
                       IAnimalService animalService) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.shelterService = shelterService;
        this.animalRepo = animalRepo;
        this.animalService = animalService;
    }


    /**
     * Метод для отправки меню для получения информации
     * @param update - объект класса Update
     */
    @Override
    public void answerCommand(Update update) {
        logger.info("the answerCommand method of the InfoManager class works. Parameter: Update -> {}", update);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(update.message().chat().id(),
                "Здесь вы можете посмотреть информацию о приютах, питомцах и требованиях к усыновителю",
                keyboardFactory.getInlineKeyboard(
                        List.of("Адреса и телефоны приютов",
                                "Информация о питомцах",
                                "Что нужно для усыновления",
                                "Назад"),
                        List.of(1, 1, 1, 1),
                        List.of(ADDRESS_AND_PHONE,
                                PET_INFORMATION,
                                WHAT_NEED_FOR_ADOPTION,
                                START)
                ));

        telegramBot.execute(sendMessage);
    }


    /**
     * Метод для отправки меню для получения информации
     * @param callbackQuery - запрос обратного вызова
     */
    @Override
    public void answerCallbackQuery(CallbackQuery callbackQuery){
        logger.info("the answerCallbackQuery method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть информацию о приютах, питомцах и требованиях к усыновителю",
                keyboardFactory.getInlineKeyboard(
                        List.of("Адреса и телефоны приютов",
                                "Информация о питомцах",
                                "Что нужно для усыновления",
                                "Назад"),
                        List.of(1, 1, 1, 1),
                        List.of(ADDRESS_AND_PHONE,
                                PET_INFORMATION,
                                WHAT_NEED_FOR_ADOPTION,
                                START)
                ));
        telegramBot.execute(sendMessage);
    }


    /**
     * Метод для отправки меню для получения информации о приютах
     * @param callbackQuery - запрос обратного вызова
     */
    public void addressAndPhoneNursery(CallbackQuery callbackQuery) {
        logger.info("The addressAndPhoneNursery method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        long chatId = callbackQuery.message().chat().id();
        List<Nursery> listOfNursery = shelterService.getAll();
        if(listOfNursery.isEmpty()){
            telegramBot.execute(answerMethodFactory.getSendMessage(
                            chatId,
                            """
                                    Ни одного приюта не найдено""",
                            null
                    )
            );
            return;
        }
        StringBuilder nurseryInfo = new StringBuilder();
        for (Nursery nursery : listOfNursery) {
            nurseryInfo.append(nursery.isForDog() ? "Приют для собак\n" : "Приют для кошек\n")
                    .append("Адрес: ").append(nursery.getAddress()).append(";\n")
                    .append("Телефон: ").append(nursery.getPhoneNumber()).append(";\n")
                    .append("-------------------\n");
        }
        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                callbackQuery.message().chat().id(),
                nurseryInfo.toString(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(INFO)
                ));
        telegramBot.execute(sendMessage);
    }


    /**
     * Метод для отправки меню для получения информации о животных
     * @param callbackQuery - запрос обратного вызова
     */
    public void petInformation(CallbackQuery callbackQuery) {
        logger.info("The petInformation method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть информацию о питомцах",
                keyboardFactory.getInlineKeyboard(
                        List.of("Кошки",
                                "Собаки",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CATS,
                                DOGS,
                                INFO)
                ));
        telegramBot.execute(sendMessage);
    }

    public void whatNeedForAdoption(CallbackQuery callbackQuery) {
        logger.info("The whatNeedForAdoption method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Приюты хотят быть уверены, что они усыновляют своих животных в семью, которая обеспечит им хороший дом.
                        Возьмите с собой паспорт и документы по прописке.
                        Если вы живете в съемной квартире или доме, предоставьте доказательства того, что у вас есть разрешение арендодателя на владение домашним животным, например, договор аренды или письмо.
                        Также могут возникнуть вопросы о ваших рабочих привычках, графике, философии ухода за домашними животными и финансовых возможностях по уходу за домашним животным.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(INFO)
                ));
        telegramBot.execute(sendMessage);
    }


    /**
     * Метод для отправки меню для просмотра фотографий кошек
     * @param callbackQuery - запрос обратного вызова
     */
    public void catsInformation(CallbackQuery callbackQuery){
        logger.info("The catsInformation method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть фотографии котов",
                keyboardFactory.getInlineKeyboard(
                        List.of("Фото и описание",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                INFO)
                ));
        telegramBot.execute(sendMessage);
    }



    /**
     * Метод для отправки фотографии животного пользователю.
     * Сначала вычисляется идентификатор животного, фото которого
     * нужно показать. Затем определяется его кличка. Если путь к фото
     * в базе данных отсутствует, то отправляется меню с надписью - "Фотография отсутствует".
     * Если путь к фото в базе данных есть, то создается и заполняется байтовый массив.
     * Формируется объект типа SendPhoto и отправляется пользователю.
     * @param callbackQuery - запрос обратного вызова
     * @throws IOException - исключение ввода-вывода
     */
    public void catPhoto(CallbackQuery callbackQuery) throws IOException {
        logger.info("The catPhoto method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        long chatId = callbackQuery.message().chat().id();
        List<Animal> cats = animalService.getAllAnimalsByType(AnimalType.CAT)
                .stream()
                .filter(a -> a.getUser() == null)
                .toList();
        // если список пуст, то отправляем сообщение пользователю
        if(cats.isEmpty()){
            telegramBot.execute(answerMethodFactory.getSendMessage(
                            chatId,
                            """
                                    В приютах нет кошек""",
                            null
                    )
            );
            return;
        }
        long id = cats.get((int)getNextId(chatId, cats)).getId();
        Animal animal = animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        String description = animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id)).getDescription();
        if(animal.getPhotoPath() == null){
            SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId,
                    "Фотография отсутствует\n\n" +
                            description,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Следующее фото c описанием",
                                    "Назад"),
                            List.of(1, 1),
                            List.of(CAT_PHOTO,
                                    CATS)
                    ));
            telegramBot.execute(sendMessage);
            return;
        }
        byte[] photoArray = animalService.getPhotoByteArray(id);
        SendPhoto sendPhoto = answerMethodFactory.getSendFoto(
                callbackQuery.message().chat().id(),
                photoArray,
                null
        );
        telegramBot.execute(sendPhoto);

        catInformation(callbackQuery);
    }

    /**
     * Метод для отправки фотографии животного пользователю.
     * Сначала вычисляется идентификатор животного, фото которого
     * нужно показать. Затем определяется его кличка. Если путь к фото
     * в базе данных отсутствует, то отправляется меню с надписью - "Фотография отсутствует".
     * Если путь к фото в базе данных есть, то создается и заполняется байтовый массив.
     * Формируется объект типа SendPhoto и отправляется пользователю.
     * @param callbackQuery - запрос обратного вызова
     * @throws IOException - исключение ввода-вывода
     */
    public void dogPhoto(CallbackQuery callbackQuery) throws IOException {
        logger.info("The dogPhoto method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        long chatId = callbackQuery.message().chat().id();
        List<Animal> dogs = animalService.getAllAnimalsByType(AnimalType.DOG)
                .stream()
                .filter(a -> a.getUser() == null)
                .toList();
        if(dogs.isEmpty()){
            telegramBot.execute(answerMethodFactory.getSendMessage(
                            chatId,
                            """
                                    В приютах нет собак""",
                            null
                    )
            );
            return;
        }
        long id = dogs.get((int)getNextId(chatId, dogs)).getId();
        Animal animal = animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        String description = animalRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id)).getDescription();
        if(animal.getPhotoPath() == null){
            SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId,
                    "Фотография отсутствует\n\n" +
                            description,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Следующее фото c описанием",
                                    "Назад"),
                            List.of(1, 1),
                            List.of(DOG_PHOTO,
                                    DOGS)
                    ));
            telegramBot.execute(sendMessage);
            return;
        }
        byte[] photoArray = animalService.getPhotoByteArray(id);
        SendPhoto sendPhoto = answerMethodFactory.getSendFoto(
                callbackQuery.message().chat().id(),
                photoArray,
                null
        );
        telegramBot.execute(sendPhoto);
        dogInformation(callbackQuery);
    }


    /**
     * Метод для определения следующего идентификатора животного
     * @param chatId - идентификатор чата с данным пользователем
     * @param animals - список животных
     * @return новый идентификатор
     */
    public long getNextId(long chatId, List<Animal> animals){
        logger.info("The getNextId method of the InfoManager class works. Parameters: long -> {}, List<Animal> -> {}", chatId, animals);
        if(userId_animalId.size() > 1000){
            userId_animalId.clear();
        }
        if(userId_animalId.containsKey(chatId)){
            long newId = userId_animalId.get(chatId) + 1;
            if(newId >= animals.size()){
                newId = 0;
                userId_animalId.put(chatId, 0L);
                return newId;
            }
            userId_animalId.put(chatId, newId);
            return newId;
        }
        userId_animalId.put(chatId, 0L);
        return 0;
    }


    /**
     * Метод для отправки меню для просмотра фотографий собак
     * @param callbackQuery - запрос обратного вызова
     */
    public void dogsInformation(CallbackQuery callbackQuery){
        logger.info("The dogsInformation method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть фотографии собак",
                keyboardFactory.getInlineKeyboard(
                        List.of("Фото и описание",
                                "Назад"),
                        List.of(1, 1),
                        List.of(DOG_PHOTO,
                                INFO)
                ));
        telegramBot.execute(sendMessage);
    }

    /**
     * Метод для получения описания кошки по ее идентификатору в базе данных.
     * Сначала из базы достаются все записи о кошках. Затем определяется запрашивал ли
     * данный пользователь фото какой-либо кошки (это определяется по словарю userId_animalId).
     * Если запрашивал, то по его chatId определяется индекс животного в списке cats, объект
     * достается из списка и берётся описание кошки.
     * @param callbackQuery - запрос обратного вызова
     */
    public void catInformation(CallbackQuery callbackQuery){
        logger.info("The catInformation method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        List<Animal> cats = animalService.getAllAnimalsByType(AnimalType.CAT)
                .stream()
                .filter(a -> a.getUser() == null)
                .toList();
        if(userId_animalId.containsKey(callbackQuery.message().chat().id())) {
            long catIndex = userId_animalId.get(callbackQuery.message().chat().id());
            String description = cats.get((int)catIndex).getDescription();
            SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                    description,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Следующее фото c описанием",
                                    "Назад"),
                            List.of(1, 1),
                            List.of(CAT_PHOTO,
                                    CATS)
                    ));
            telegramBot.execute(sendMessage);
        }
    }

    /**
     * Метод для получения описания собаки по ее идентификатору в базе данных.
     * Сначала из базы достаются все записи о собаках. Затем определяется запрашивал ли
     * данный пользователь фото какой-либо собаки (это определяется по словарю userId_animalId).
     * Если запрашивал, то по его chatId определяется индекс животного в списке dogs, объект
     * достается из списка и берётся описание собаки.
     * @param callbackQuery - запрос обратного вызова
     */
    public void dogInformation(CallbackQuery callbackQuery){
        logger.info("The dogInformation method of the InfoManager class works. Parameter: CallbackQuery -> {}", callbackQuery);
        List<Animal> dogs = animalService.getAllAnimalsByType(AnimalType.DOG)
                .stream()
                .filter(a -> a.getUser() == null)
                .toList();
        if(userId_animalId.containsKey(callbackQuery.message().chat().id())) {
            long dogIndex = userId_animalId.get(callbackQuery.message().chat().id());
            String description = dogs.get((int)dogIndex).getDescription();
            SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                    description,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Следующее фото c описанием",
                                    "Назад"),
                            List.of(1, 1),
                            List.of(DOG_PHOTO,
                                    DOGS)
                    ));
            telegramBot.execute(sendMessage);
        }
    }

    public void putTOUserChatId_AnimalId(long chatId, long animalId){
        userId_animalId.put(chatId, animalId);
    }

}
