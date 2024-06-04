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
import ru.pet.nursery.web.service.IAnimalService;
import ru.pet.nursery.web.service.IShelterService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.pet.nursery.enumerations.CallbackDataEnum.*;

@Component
public class InfoManager extends AbstractManager {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final IShelterService shelterService;
    private final IAnimalService animalService;
    private final Map<Long, Animal> userIdAnimal = new HashMap<>(); // словарь соответствия chatId идентификатору животного, чтобы передавать новую фотографию

    public InfoManager(AnswerMethodFactory answerMethodFactory,
                       KeyboardFactory keyboardFactory,
                       TelegramBot telegramBot,
                       IShelterService shelterService,
                       IAnimalService animalService) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.shelterService = shelterService;
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
                        List.of(ADDRESS_AND_PHONE.toString(),
                                PET_INFORMATION.toString(),
                                WHAT_NEED_FOR_ADOPTION.toString(),
                                START.toString())
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
                        List.of(ADDRESS_AND_PHONE.toString(),
                                PET_INFORMATION.toString(),
                                WHAT_NEED_FOR_ADOPTION.toString(),
                                START.toString())
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
                    .append("Название: ").append(nursery.getNameShelter()).append(";\n")
                    .append("Адрес: ").append(nursery.getAddress()).append(";\n")
                    .append("Телефон: ").append(nursery.getPhoneNumber()).append(";\n")
                    .append("Ссылка на карту: ").append(nursery.getMapLink() == null ? "нет" : nursery.getMapLink()).append(";\n")
                    .append("-------------------\n");
        }
        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                callbackQuery.message().chat().id(),
                nurseryInfo.toString(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(INFO.toString())
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
                        List.of(CAT_PHOTO.toString(),
                                DOG_PHOTO.toString(),
                                INFO.toString())
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
                        List.of(INFO.toString())
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
        Animal nextAnimal = getNextAnimal(chatId, cats);
        String description = nextAnimal.getDescription();
        if(nextAnimal.getPhotoPath() == null){
            SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId,
                    "Фотография отсутствует\n\n" +
                            description,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Следующее фото c описанием",
                                    "Назад"),
                            List.of(1, 1),
                            List.of(DOG_PHOTO.toString(),
                                    PET_INFORMATION.toString())
                    ));
            telegramBot.execute(sendMessage);
            return;
        }
        byte[] photoArray = animalService.getPhotoByteArray(nextAnimal.getId());
        SendPhoto sendPhoto = answerMethodFactory.getSendPhoto(
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
        Animal nextAnimal = getNextAnimal(chatId, dogs);
        String description = nextAnimal.getDescription();
        if(nextAnimal.getPhotoPath() == null){
            SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId,
                    "Фотография отсутствует\n\n" +
                            description,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Следующее фото c описанием",
                                    "Назад"),
                            List.of(1, 1),
                            List.of(DOG_PHOTO.toString(),
                                    PET_INFORMATION.toString())
                    ));
            telegramBot.execute(sendMessage);
            return;
        }
        byte[] photoArray = animalService.getPhotoByteArray(nextAnimal.getId());
        SendPhoto sendPhoto = answerMethodFactory.getSendPhoto(
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
    public Animal getNextAnimal(long chatId, List<Animal> animals){
        logger.info("The getNextAnimal method of the InfoManager class works. Parameters: long -> {}, List<Animal> -> {}", chatId, animals);
        if(userIdAnimal.size() > 1000){
            userIdAnimal.clear();
        }
        if(userIdAnimal.containsKey(chatId)){
            Animal currentAnimal = userIdAnimal.get(chatId);
            int nextIndex = 0;
            for (int i = 0; i < animals.size(); i++) {
                if(currentAnimal.equals(animals.get(i))){
                    if(i == animals.size() - 1){
                        nextIndex = 0;
                    }else {
                        nextIndex = i;
                        nextIndex++;
                    }
                }
            }

            userIdAnimal.put(chatId, animals.get(nextIndex));
            return animals.get(nextIndex);
        }
        userIdAnimal.put(chatId, animals.get(0));
        return animals.get(0);
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
        if(userIdAnimal.containsKey(callbackQuery.message().chat().id())) {
            Animal cat = userIdAnimal.get(callbackQuery.message().chat().id());
            String description = cat.getDescription();
            String nursery = cat.getNursery().getNameShelter();
            SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                    String.format("%s\n%s: %s", description, "Приют", nursery),
                    keyboardFactory.getInlineKeyboard(
                            List.of("Следующее фото c описанием",
                                    "Назад"),
                            List.of(1, 1),
                            List.of(CAT_PHOTO.toString(),
                                    PET_INFORMATION.toString())
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
        if(userIdAnimal.containsKey(callbackQuery.message().chat().id())) {
            Animal dog = userIdAnimal.get(callbackQuery.message().chat().id());
            String description = dog.getDescription();
            String nursery = dog.getNursery().getNameShelter();
            SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                    String.format("%s\n%s: %s", description, "Приют", nursery),
                    keyboardFactory.getInlineKeyboard(
                            List.of("Следующее фото c описанием",
                                    "Назад"),
                            List.of(1, 1),
                            List.of(DOG_PHOTO.toString(),
                                    PET_INFORMATION.toString())
                    ));
            telegramBot.execute(sendMessage);
        }
    }

    public void putToUserIdAnimal(long chatId, Animal animal){
        userIdAnimal.put(chatId, animal);
    }

}
