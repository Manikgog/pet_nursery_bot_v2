package ru.pet.nursery.manager.info;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.web.exception.ImageNotFoundException;
import ru.pet.nursery.web.service.IAnimalService;
import ru.pet.nursery.web.service.IShelterService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.data.CallbackData.*;
import static ru.pet.nursery.web.Constants.NURSERY_1;
import static ru.pet.nursery.web.Constants.NURSERY_2;

@ExtendWith(MockitoExtension.class)
public class InfoManagerMockTest {
    @Mock
    TelegramBot telegramBot;
    @Mock
    AnswerMethodFactory answerMethodFactory;
    @Mock
    KeyboardFactory keyboardFactory;
    @Mock
    IShelterService shelterService;
    @Mock
    IAnimalService animalService;
    @Mock
    AnimalRepo animalRepo;
    @InjectMocks
    InfoManager infoManager;
    private final Faker faker = new Faker();

    private final KeyboardFactory keyboardFactory_ = new KeyboardFactory();
    @Test
    public void answerCommand_Test() throws IOException {
        Update update = getUpdate("update.json");

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(List.of(
                "Адреса и телефоны приютов",
                        "Информация о питомцах",
                        "Что нужно для усыновления",
                        "Назад"),
                List.of(1, 1, 1, 1),
                List.of(ADDRESS_AND_PHONE,
                        PET_INFORMATION,
                        WHAT_NEED_FOR_ADOPTION,
                        START));

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(update.message().chat().id(),
                "Здесь вы можете посмотреть информацию о приютах, питомцах и требованиях к усыновителю",
                keyboardFactory_.getInlineKeyboard(
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

        when(keyboardFactory.getInlineKeyboard(List.of("Адреса и телефоны приютов",
                        "Информация о питомцах",
                        "Что нужно для усыновления",
                        "Назад"),
                List.of(1, 1, 1, 1),
                List.of(ADDRESS_AND_PHONE,
                        PET_INFORMATION,
                        WHAT_NEED_FOR_ADOPTION,
                        START))).thenReturn(inlineKeyboardMarkup);
        when(answerMethodFactory.getSendMessage(update.message().chat().id(),
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
                        START)))).thenReturn(sendMessage);
        infoManager.answerCommand(update);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть информацию о приютах, питомцах и требованиях к усыновителю");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }



    @Test
    public void answerCallbackQuery_Test() throws IOException {
        CallbackQuery callbackQuery_info = readJsonFromResource("callbackdata_info.json");

        when(keyboardFactory.getInlineKeyboard(List.of("Адреса и телефоны приютов",
                        "Информация о питомцах",
                        "Что нужно для усыновления",
                        "Назад"),
                List.of(1, 1, 1, 1),
                List.of(ADDRESS_AND_PHONE,
                        PET_INFORMATION,
                        WHAT_NEED_FOR_ADOPTION,
                        START))).thenReturn(keyboardFactory_.getInlineKeyboard(List.of("Адреса и телефоны приютов",
                        "Информация о питомцах",
                        "Что нужно для усыновления",
                        "Назад"),
                List.of(1, 1, 1, 1),
                List.of(ADDRESS_AND_PHONE,
                        PET_INFORMATION,
                        WHAT_NEED_FOR_ADOPTION,
                        START))
        );
        when(answerMethodFactory.getSendMessage(callbackQuery_info.message().chat().id(),
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
                                START)))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery_info.message().chat().id(),
                        "Здесь вы можете посмотреть информацию о приютах, питомцах и требованиях к усыновителю",
                        keyboardFactory_.getInlineKeyboard(
                                List.of("Адреса и телефоны приютов",
                                        "Информация о питомцах",
                                        "Что нужно для усыновления",
                                        "Назад"),
                                List.of(1, 1, 1, 1),
                                List.of(ADDRESS_AND_PHONE,
                                        PET_INFORMATION,
                                        WHAT_NEED_FOR_ADOPTION,
                                        START)
                        ))
        );

        infoManager.answerCallbackQuery(callbackQuery_info);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть информацию о приютах, питомцах и требованиях к усыновителю");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("Адреса и телефоны приютов",
                                "Информация о питомцах",
                                "Что нужно для усыновления",
                                "Назад"),
                        List.of(1, 1, 1, 1),
                        List.of(ADDRESS_AND_PHONE,
                                PET_INFORMATION,
                                WHAT_NEED_FOR_ADOPTION,
                                START)));

    }

    @Test
    public void addressAndPhoneNursery_Test() throws IOException {
        CallbackQuery callbackQuery_info = readJsonFromResource("callbackquery_nurseries.json");
        List<Nursery> nurseries = createNurseries(2);
        when(shelterService.getAll()).thenReturn(nurseries);
        StringBuilder nurseryInfo = new StringBuilder();
        for (Nursery nursery : nurseries) {
            nurseryInfo.append(nursery.isForDog() ? "Приют для собак\n" : "Приют для кошек\n")
                    .append("Адрес: ").append(nursery.getAddress()).append(";\n")
                    .append("Телефон: ").append(nursery.getPhoneNumber()).append(";\n")
                    .append("-------------------\n");
        }

        when(keyboardFactory.getInlineKeyboard(
                List.of("Назад"),
                List.of(1),
                List.of(INFO)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(INFO)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery_info.message().chat().id(),
                nurseryInfo.toString(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(INFO)
                )
                )).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery_info.message().chat().id(),
                        nurseryInfo.toString(),
                        keyboardFactory_.getInlineKeyboard(
                                List.of("Назад"),
                                List.of(1),
                                List.of(INFO)
                        ))
        );


        infoManager.addressAndPhoneNursery(callbackQuery_info);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                nurseryInfo.toString());
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(INFO)
                ));
    }

    @Test
    public void addressAndPhoneNursery_TestByListOfNurseriesIsNull() throws IOException {
        CallbackQuery callbackQuery_info = readJsonFromResource("callbackquery_nurseries.json");

        when(answerMethodFactory.getSendMessage(callbackQuery_info.message().chat().id(),
                """
                                    Ни одного приюта не найдено""",
                null
        )).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery_info.message().chat().id(),
                        """
                                    Ни одного приюта не найдено""",
                        null)
        );

        infoManager.addressAndPhoneNursery(callbackQuery_info);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                                    Ни одного приюта не найдено""");
    }


    @Test
    public void petInformation_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callbackquery_pet_info.json");

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Кошки",
                        "Собаки",
                        "Назад"),
                List.of(1, 1, 1),
                List.of(CAT_PHOTO,
                        DOG_PHOTO,
                        INFO));

        SendMessage sendMessage =  new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть информацию о питомцах",
                keyboardFactory_.getInlineKeyboard(
                        List.of("Кошки",
                                "Собаки",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CAT_PHOTO,
                                DOG_PHOTO,
                                INFO)));

        when(keyboardFactory.getInlineKeyboard(
                List.of("Кошки",
                        "Собаки",
                        "Назад"),
                List.of(1, 1, 1),
                List.of(CAT_PHOTO,
                        DOG_PHOTO,
                        INFO)
        )).thenReturn(inlineKeyboardMarkup);


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть информацию о питомцах",
                keyboardFactory.getInlineKeyboard(
                        List.of("Кошки",
                                "Собаки",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CAT_PHOTO,
                                DOG_PHOTO,
                                INFO)
        ))).thenReturn(sendMessage);

        infoManager.petInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть информацию о питомцах");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }


    @Test
    public void whatNeedForAdoption_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callbackquery_what_need_for_adoption.json");

        when(keyboardFactory.getInlineKeyboard(
                List.of("Назад"),
                List.of(1),
                List.of(INFO)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(INFO)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
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
                ))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        """
                        Приюты хотят быть уверены, что они усыновляют своих животных в семью, которая обеспечит им хороший дом.
                        Возьмите с собой паспорт и документы по прописке.
                        Если вы живете в съемной квартире или доме, предоставьте доказательства того, что у вас есть разрешение арендодателя на владение домашним животным, например, договор аренды или письмо.
                        Также могут возникнуть вопросы о ваших рабочих привычках, графике, философии ухода за домашними животными и финансовых возможностях по уходу за домашним животным.
                        """,
                        keyboardFactory_.getInlineKeyboard(
                                List.of("Назад"),
                                List.of(1),
                                List.of(INFO)
                        )
                )
        );

        infoManager.whatNeedForAdoption(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Приюты хотят быть уверены, что они усыновляют своих животных в семью, которая обеспечит им хороший дом.
                        Возьмите с собой паспорт и документы по прописке.
                        Если вы живете в съемной квартире или доме, предоставьте доказательства того, что у вас есть разрешение арендодателя на владение домашним животным, например, договор аренды или письмо.
                        Также могут возникнуть вопросы о ваших рабочих привычках, графике, философии ухода за домашними животными и финансовых возможностях по уходу за домашним животным.
                        """);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(INFO)
                ));
    }


/*
    @Test
    public void catsInformation_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callbackquery_cats_info.json");

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Фото",
                        "Назад"),
                List.of(1, 1),
                List.of(CAT_PHOTO,
                        INFO)
        );

        when(keyboardFactory.getInlineKeyboard(
                List.of("Фото и описание",
                        "Назад"),
                List.of(1, 1),
                List.of(CAT_PHOTO,
                        INFO)
        )).thenReturn(inlineKeyboardMarkup);

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть фотографии котов",
                inlineKeyboardMarkup);

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть фотографии котов",
                keyboardFactory.getInlineKeyboard(
                        List.of("Фото и описание",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                INFO)
                ))).thenReturn(sendMessage);

        infoManager.catsInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть фотографии котов");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }*/


    @Test
    public void catPhoto_TestByListOfCatsIsEmpty() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callbackquery_cat_photo.json");

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                            В приютах нет кошек""",
                null
        )).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        """
                            В приютах нет кошек""",
                        null)
        );

        infoManager.catPhoto(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                            В приютах нет кошек""");


    }



    @Test
    public void catPhoto_TestByPhotoEmpty() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callbackquery_cat_photo.json");
        List<Animal> cats = createAnimals(10, AnimalType.CAT);
        when(animalService.getAllAnimalsByType(AnimalType.CAT)).thenReturn(cats);
        when(animalRepo.findById(1L)).thenReturn(Optional.of(cats.get(0)));
        long chatId = callbackQuery.message().chat().id();
        InlineKeyboardMarkup inlineKeyboardMarkup =  keyboardFactory.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(CAT_PHOTO,
                        CATS)
        );

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(
                chatId,
                "Фотография отсутствует\n\n" + cats.get(0).getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Следующее фото c описанием",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                CATS)
                )
        );

        when( keyboardFactory_.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(CAT_PHOTO,
                        CATS)
        )).thenReturn(inlineKeyboardMarkup);


        when(answerMethodFactory.getSendMessage(chatId,
                "Фотография отсутствует\n\n" + cats.get(0).getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Следующее фото c описанием",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                CATS)
                ))).thenReturn(sendMessage);

        infoManager.catPhoto(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Фотография отсутствует\n\n" + cats.get(0).getDescription());
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);

    }



    @Test
    public void catPhoto_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callbackquery_cat_photo.json");
        List<Animal> cats = createAnimals(10, AnimalType.CAT);
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\info\\1.jpg";
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/info/1.jpg";
        }
        cats.get(0).setPhotoPath(strPath);
        when(animalService.getAllAnimalsByType(AnimalType.CAT)).thenReturn(cats);
        when(animalRepo.findById(1L)).thenReturn(Optional.of(cats.get(0)));
        byte[] photoArray = getPhotoByteArray(cats.get(0));
        when(animalService.getPhotoByteArray(1L)).thenReturn(photoArray);

        when(answerMethodFactory.getSendPhoto(
                callbackQuery.message().chat().id(),
                photoArray,
                null)).thenReturn(
                new AnswerMethodFactory().getSendPhoto(
                        callbackQuery.message().chat().id(),
                        photoArray,
                        null)
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(CAT_PHOTO,
                        PET_INFORMATION)
        );
        when(keyboardFactory.getInlineKeyboard(
                        List.of("Следующее фото c описанием",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                PET_INFORMATION)
                )
        ).thenReturn(inlineKeyboardMarkup);

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(
                callbackQuery.message().chat().id(),
                cats.get(0).getDescription(),
                inlineKeyboardMarkup);


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                cats.get(0).getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Следующее фото c описанием",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                PET_INFORMATION)
                ))).thenReturn(sendMessage);


        infoManager.catPhoto(callbackQuery);

        ArgumentCaptor<SendPhoto> argumentCaptorPhoto = ArgumentCaptor.forClass(SendPhoto.class);
        Mockito.verify(telegramBot).execute(argumentCaptorPhoto.capture());
        SendPhoto actual = argumentCaptorPhoto.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("photo")).isEqualTo(
                photoArray);

        //infoManager.catInformation(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptorMessage = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptorMessage.capture());
        SendMessage actualMessage = argumentCaptorMessage.getValue();

        Assertions.assertThat(actualMessage.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actualMessage.getParameters().get("text")).isEqualTo(
                cats.get(0).getDescription());
        Assertions.assertThat(actualMessage.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);

    }


    @Test
    public void dogPhoto_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_dog_photo.json");
        List<Animal> dogs = createAnimals(10, AnimalType.DOG);
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\info\\1.jpg";
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/info/1.jpg";
        }
        dogs.get(0).setPhotoPath(strPath);
        when(animalService.getAllAnimalsByType(AnimalType.DOG)).thenReturn(dogs);
        when(animalRepo.findById(1L)).thenReturn(Optional.of(dogs.get(0)));
        byte[] photoArray = getPhotoByteArray(dogs.get(0));
        when(animalService.getPhotoByteArray(1L)).thenReturn(photoArray);

        SendPhoto sendPhoto = new AnswerMethodFactory().getSendPhoto(
                callbackQuery.message().chat().id(),
                photoArray,
                null
        );

        when(answerMethodFactory.getSendPhoto(
                callbackQuery.message().chat().id(),
                photoArray,
                null
                )
        ).thenReturn(sendPhoto);

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        PET_INFORMATION)
        );

        when(keyboardFactory.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        PET_INFORMATION)
                )
        ).thenReturn(inlineKeyboardMarkup);

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                dogs.get(0).getDescription(),
                inlineKeyboardMarkup);

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                        dogs.get(0).getDescription(),
                        keyboardFactory.getInlineKeyboard(
                                List.of("Следующее фото c описанием",
                                        "Назад"),
                                List.of(1, 1),
                                List.of(DOG_PHOTO,
                                        PET_INFORMATION)
                        ))).thenReturn(sendMessage);


        infoManager.dogPhoto(callbackQuery);

        ArgumentCaptor<SendPhoto> argumentCaptorPhoto = ArgumentCaptor.forClass(SendPhoto.class);
        Mockito.verify(telegramBot).execute(argumentCaptorPhoto.capture());
        SendPhoto actual = argumentCaptorPhoto.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("photo")).isEqualTo(photoArray);


        ArgumentCaptor<SendMessage> argumentCaptorMessage = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptorMessage.capture());
        SendMessage actualMessage = argumentCaptorMessage.getValue();

        Assertions.assertThat(actualMessage.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actualMessage.getParameters().get("text")).isEqualTo(dogs.get(0).getDescription());
        Assertions.assertThat(actualMessage.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }

    public byte[] getPhotoByteArray(Animal animal) {
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

    @Test
    public void dogPhoto_TestByListOfDogsIsEmpty() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_dog_photo.json");

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                            В приютах нет собак""",
                null
        )).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        """
                            В приютах нет собак""",
                        null)
        );

        infoManager.dogPhoto(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                            В приютах нет собак""");


    }



    @Test
    public void dogPhoto_TestByEmptyPhotoPath() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_dog_photo.json");
        List<Animal> dogs = createAnimals(10, AnimalType.DOG);
        when(animalService.getAllAnimalsByType(AnimalType.DOG)).thenReturn(dogs);
        when(animalRepo.findById(1L)).thenReturn(Optional.of(dogs.get(0)));
        long chatId = callbackQuery.message().chat().id();
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        PET_INFORMATION)
        );

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                "Фотография отсутствует\n\n" + dogs.get(0).getDescription(),
                keyboardFactory_.getInlineKeyboard(
                        List.of("Следующее фото c описанием",
                                "Назад"),
                        List.of(1, 1),
                        List.of(DOG_PHOTO,
                                PET_INFORMATION)));

        when(keyboardFactory.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        PET_INFORMATION)
        )).thenReturn(inlineKeyboardMarkup);


        when(answerMethodFactory.getSendMessage(chatId,
                "Фотография отсутствует\n\n" + dogs.get(0).getDescription(),
                        inlineKeyboardMarkup)).thenReturn(sendMessage);

        infoManager.dogPhoto(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Фотография отсутствует\n\n" + dogs.get(0).getDescription());
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }


    @Test
    public void getNextId_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_dog_photo.json");

        long chatId = callbackQuery.message().chat().id();
        List<Animal> dogs = createAnimals(10, AnimalType.DOG);
        long randomIndex = faker.random().nextInt(1, dogs.size() - 2);
        infoManager.putTOUserChatId_AnimalId(chatId, randomIndex);
        long actual = infoManager.getNextId(chatId, dogs);

        long expected = randomIndex + 1;
        Assertions.assertThat(expected).isEqualTo(actual);
        // проверка работы метода при индексе, который больше размера списка животных
        int tooBigIndex = dogs.size();
        infoManager.putTOUserChatId_AnimalId(chatId, tooBigIndex);
        actual = infoManager.getNextId(chatId, dogs);
        expected = 0;
        Assertions.assertThat(expected).isEqualTo(actual);

        // проверка работы метода при количестве записей в мапе userId_animalId > 1000
        for (int i = 0; i < 1001; i++) {
            infoManager.putTOUserChatId_AnimalId(i + 10, faker.random().nextInt(1, dogs.size() - 2));
        }
        actual = infoManager.getNextId(chatId, dogs);
        Assertions.assertThat(expected).isEqualTo(actual);
    }

/*
    @Test
    public void dogsInformation_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_dogs_info.json");

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Фото и описание",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        INFO)
        );

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(
                callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть фотографии собак",
                inlineKeyboardMarkup
        );


        when(keyboardFactory.getInlineKeyboard(
                List.of("Фото и описание",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        INFO)
        )).thenReturn(inlineKeyboardMarkup);


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть фотографии собак",
                keyboardFactory.getInlineKeyboard(
                        List.of("Фото и описание",
                                "Назад"),
                        List.of(1, 1),
                        List.of(DOG_PHOTO,
                                INFO)
                ))).thenReturn(sendMessage);

       infoManager.dogsInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть фотографии собак");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);

    }
*/

    @Test
    public void catInformation_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_cat_info.json");
        List<Animal> cats = createAnimals(10, AnimalType.CAT);
        when(animalService.getAllAnimalsByType(AnimalType.CAT)).thenReturn(cats);
        long chatId = callbackQuery.message().chat().id();
        long randomIndex = faker.random().nextInt(1, cats.size() - 2);
        Animal animal = cats.get((int)randomIndex);
        infoManager.putTOUserChatId_AnimalId(chatId, randomIndex);

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(CAT_PHOTO,
                        PET_INFORMATION));

        when(keyboardFactory.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(CAT_PHOTO,
                        PET_INFORMATION)
        )).thenReturn(inlineKeyboardMarkup);

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(
                callbackQuery.message().chat().id(),
                cats.get((int) randomIndex).getDescription(),
                inlineKeyboardMarkup
        );

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                animal.getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Следующее фото c описанием",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                PET_INFORMATION)))).thenReturn(sendMessage);

        infoManager.catInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                animal.getDescription());
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);

    }

    @Test
    public void dogInformation_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_dog_info.json");
        List<Animal> dogs = createAnimals(10, AnimalType.DOG);
        when(animalService.getAllAnimalsByType(AnimalType.DOG)).thenReturn(dogs);
        long chatId = callbackQuery.message().chat().id();
        long randomIndex = faker.random().nextInt(1, dogs.size() - 2);
        Animal animal = dogs.get((int)randomIndex);
        infoManager.putTOUserChatId_AnimalId(chatId, randomIndex);

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        PET_INFORMATION));

        when(keyboardFactory.getInlineKeyboard(
                List.of("Следующее фото c описанием",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        PET_INFORMATION)
        )).thenReturn(inlineKeyboardMarkup);

        SendMessage sendMessage = new AnswerMethodFactory().getSendMessage(
                callbackQuery.message().chat().id(),
                dogs.get((int) randomIndex).getDescription(),
                inlineKeyboardMarkup
        );

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                animal.getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Следующее фото c описанием",
                                "Назад"),
                        List.of(1, 1),
                        List.of(DOG_PHOTO,
                                PET_INFORMATION)))).thenReturn(sendMessage);

        infoManager.dogInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                animal.getDescription());
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }


    @Test
    public void putTOUserChatId_AnimalId_Test(){
        infoManager.putTOUserChatId_AnimalId(1L, 1L);
    }


    private List<Nursery> createNurseries(int amount){
        List<Nursery> nurseries = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Nursery nursery = new Nursery();
            nursery.setNameShelter(faker.funnyName().name());
            nursery.setAddress(faker.address().fullAddress());
            nursery.setPhoneNumber(faker.phoneNumber().phoneNumberNational());
            nursery.setForDog(i % 2 != 0);
            nurseries.add(nursery);
        }
        return nurseries;
    }

    private List<Animal> createAnimals(long amount, AnimalType type){
        List<Animal> animals = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Animal animal = new Animal();
            animal.setId(i + 1L);
            animal.setAnimalName(faker.name().name());
            animal.setAnimalType(type == AnimalType.CAT ? AnimalType.CAT : AnimalType.DOG);
            animal.setGender(faker.random().nextBoolean() ? Gender.MALE : Gender.FEMALE);
            animal.setDescription(faker.examplify(animal.getAnimalName()));
            if(type == AnimalType.CAT) {
                animal.setNursery(NURSERY_1);
            }else {
                animal.setNursery(NURSERY_2);
            }
            animal.setBirthDate(faker.date().birthdayLocalDate(1, 10));
            animal.setPhotoPath(null);
            animals.add(animal);
        }
        return animals;
    }



    private CallbackQuery readJsonFromResource(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\info\\" + filename ;
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/info/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath)
                )
        );

        return BotUtils.fromJson(json, CallbackQuery.class);
    }


    private Update getUpdate(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\info\\" + filename;
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/info/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath)
                ), Charset.forName("WINDOWS-1251")
        );
        return BotUtils.fromJson(
                json,
                Update.class
        );
    }

}
