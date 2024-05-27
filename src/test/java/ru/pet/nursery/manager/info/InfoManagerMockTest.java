package ru.pet.nursery.manager.info;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
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
                        START)))).thenReturn(
                new AnswerMethodFactory().getSendMessage(update.message().chat().id(),
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
        infoManager.answerCommand(update);
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
        Update update = BotUtils.fromJson(
                json,
                Update.class
        );
        return update;
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

        when(keyboardFactory.getInlineKeyboard(
                List.of("Кошки",
                        "Собаки",
                        "Назад"),
                List.of(1, 1, 1),
                List.of(CATS,
                        DOGS,
                        INFO)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of("Кошки",
                                "Собаки",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CATS,
                                DOGS,
                                INFO)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть информацию о питомцах",
                keyboardFactory.getInlineKeyboard(
                        List.of("Кошки",
                                "Собаки",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CATS,
                                DOGS,
                                INFO)
        ))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        "Здесь вы можете посмотреть информацию о питомцах",
                        keyboardFactory_.getInlineKeyboard(
                                List.of("Кошки",
                                        "Собаки",
                                        "Назад"),
                                List.of(1, 1, 1),
                                List.of(CATS,
                                        DOGS,
                                        INFO)
                        )
        )
        );

        infoManager.petInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть информацию о питомцах");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("Кошки",
                                "Собаки",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CATS,
                                DOGS,
                                INFO)
                ));
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



    @Test
    public void catsInformation_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callbackquery_cats_info.json");

        when(keyboardFactory.getInlineKeyboard(
                List.of("Фото",
                        "Назад"),
                List.of(1, 1),
                List.of(CAT_PHOTO,
                        INFO)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of("Фото",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                INFO)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть фотографии котов",
                keyboardFactory.getInlineKeyboard(
                        List.of("Фото",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                INFO)
                ))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        "Здесь вы можете посмотреть фотографии котов",
                        keyboardFactory_.getInlineKeyboard(
                                List.of("Фото",
                                        "Назад"),
                                List.of(1, 1),
                                List.of(CAT_PHOTO,
                                        INFO)
                        )
                )
        );

        infoManager.catsInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть фотографии котов");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("Фото",
                                "Назад"),
                        List.of(1, 1),
                        List.of(CAT_PHOTO,
                                INFO)
                ));
    }


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
        when(keyboardFactory.getInlineKeyboard(
                List.of(cats.get(0).getAnimalName(),
                        "Следующее фото",
                        "Назад"),
                List.of(1, 1, 1),
                List.of(CAT_INFORMATION,
                        CAT_PHOTO,
                        INFO)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of(cats.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CAT_INFORMATION,
                                CAT_PHOTO,
                                INFO)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Фотография отсутствует",
                keyboardFactory.getInlineKeyboard(
                        List.of(cats.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CAT_INFORMATION,
                                CAT_PHOTO,
                                INFO)
                ))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        "Фотография отсутствует",
                        keyboardFactory_.getInlineKeyboard(
                                List.of(cats.get(0).getAnimalName(),
                                        "Следующее фото",
                                        "Назад"),
                                List.of(1, 1, 1),
                                List.of(CAT_INFORMATION,
                                        CAT_PHOTO,
                                        INFO)
                        ))
        );

        infoManager.catPhoto(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Фотография отсутствует");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of(cats.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CAT_INFORMATION,
                                CAT_PHOTO,
                                INFO)
                ));

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
        when(keyboardFactory.getInlineKeyboard(
                List.of(cats.get(0).getAnimalName(),
                        "Следующее фото",
                        "Назад"),
                List.of(1, 1, 1),
                List.of(CAT_INFORMATION,
                        CAT_PHOTO,
                        CATS)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of(cats.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CAT_INFORMATION,
                                CAT_PHOTO,
                                CATS)
                )
        );


        when(answerMethodFactory.getSendFoto(
                callbackQuery.message().chat().id(),
                photoArray,
                keyboardFactory.getInlineKeyboard(
                        List.of(cats.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CAT_INFORMATION,
                                CAT_PHOTO,
                                CATS)
                ))).thenReturn(
                new AnswerMethodFactory().getSendFoto(
                        callbackQuery.message().chat().id(),
                        photoArray,
                        keyboardFactory_.getInlineKeyboard(
                                List.of(cats.get(0).getAnimalName(),
                                        "Следующее фото",
                                        "Назад"),
                                List.of(1, 1, 1),
                                List.of(CAT_INFORMATION,
                                        CAT_PHOTO,
                                        CATS)
                        ))
        );

        infoManager.catPhoto(callbackQuery);

        ArgumentCaptor<SendPhoto> argumentCaptor = ArgumentCaptor.forClass(SendPhoto.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendPhoto actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("photo")).isEqualTo(
                photoArray);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of(cats.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(CAT_INFORMATION,
                                CAT_PHOTO,
                                CATS)
                ));

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
        when(keyboardFactory.getInlineKeyboard(
                List.of(dogs.get(0).getAnimalName(),
                        "Следующее фото",
                        "Назад"),
                List.of(1, 1, 1),
                List.of(DOG_INFORMATION,
                        DOG_PHOTO,
                        DOGS)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of(dogs.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(DOG_INFORMATION,
                                DOG_PHOTO,
                                DOGS)
                )
        );


        when(answerMethodFactory.getSendFoto(
                callbackQuery.message().chat().id(),
                photoArray,
                keyboardFactory.getInlineKeyboard(
                        List.of(dogs.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(DOG_INFORMATION,
                                DOG_PHOTO,
                                DOGS)
                ))).thenReturn(
                new AnswerMethodFactory().getSendFoto(
                        callbackQuery.message().chat().id(),
                        photoArray,
                        keyboardFactory_.getInlineKeyboard(
                                List.of(dogs.get(0).getAnimalName(),
                                        "Следующее фото",
                                        "Назад"),
                                List.of(1, 1, 1),
                                List.of(DOG_INFORMATION,
                                        DOG_PHOTO,
                                        DOGS)
                        ))
        );

        infoManager.dogPhoto(callbackQuery);

        ArgumentCaptor<SendPhoto> argumentCaptor = ArgumentCaptor.forClass(SendPhoto.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendPhoto actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("photo")).isEqualTo(
                photoArray);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of(dogs.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(DOG_INFORMATION,
                                DOG_PHOTO,
                                DOGS)
                ));

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
        when(keyboardFactory.getInlineKeyboard(
                List.of(dogs.get(0).getAnimalName(),
                        "Следующее фото",
                        "Назад"),
                List.of(1, 1, 1),
                List.of(DOG_INFORMATION,
                        DOG_PHOTO,
                        INFO)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of(dogs.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(DOG_INFORMATION,
                                DOG_PHOTO,
                                INFO)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Фотография отсутствует",
                keyboardFactory.getInlineKeyboard(
                        List.of(dogs.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(DOG_INFORMATION,
                                DOG_PHOTO,
                                INFO)
                ))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        "Фотография отсутствует",
                        keyboardFactory_.getInlineKeyboard(
                                List.of(dogs.get(0).getAnimalName(),
                                        "Следующее фото",
                                        "Назад"),
                                List.of(1, 1, 1),
                                List.of(DOG_INFORMATION,
                                        DOG_PHOTO,
                                        INFO)
                        ))
        );

        infoManager.dogPhoto(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Фотография отсутствует");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of(dogs.get(0).getAnimalName(),
                                "Следующее фото",
                                "Назад"),
                        List.of(1, 1, 1),
                        List.of(DOG_INFORMATION,
                                DOG_PHOTO,
                                INFO)
                ));
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
        expected = 0;
        Assertions.assertThat(expected).isEqualTo(actual);
    }


    @Test
    public void dogsInformation_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_dogs_info.json");

        when(keyboardFactory.getInlineKeyboard(
                List.of("Фото",
                        "Назад"),
                List.of(1, 1),
                List.of(DOG_PHOTO,
                        INFO)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of("Фото",
                                "Назад"),
                        List.of(1, 1),
                        List.of(DOG_PHOTO,
                                INFO)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                "Здесь вы можете посмотреть фотографии собак",
                keyboardFactory.getInlineKeyboard(
                        List.of("Фото",
                                "Назад"),
                        List.of(1, 1),
                        List.of(DOG_PHOTO,
                                INFO)
                ))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        "Здесь вы можете посмотреть фотографии собак",
                        keyboardFactory_.getInlineKeyboard(
                                List.of("Фото",
                                        "Назад"),
                                List.of(1, 1),
                                List.of(DOG_PHOTO,
                                        INFO)
                        ))
        );

        infoManager.dogsInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть фотографии собак");
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("Фото",
                                "Назад"),
                        List.of(1, 1),
                        List.of(DOG_PHOTO,
                                INFO)
                ));

    }


    @Test
    public void catInformation_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_cat_info.json");
        List<Animal> cats = createAnimals(10, AnimalType.CAT);
        when(animalService.getAllAnimalsByType(AnimalType.CAT)).thenReturn(cats);
        long chatId = callbackQuery.message().chat().id();
        long randomIndex = faker.random().nextInt(1, cats.size() - 2);
        Animal animal = cats.get((int)randomIndex);
        infoManager.putTOUserChatId_AnimalId(chatId, randomIndex);

        when(keyboardFactory.getInlineKeyboard(
                List.of("Назад"),
                List.of(1),
                List.of(CATS)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(CATS)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                animal.getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(CATS)
                ))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        animal.getDescription(),
                        keyboardFactory_.getInlineKeyboard(
                                List.of("Назад"),
                                List.of(1),
                                List.of(CATS)
                        ))
        );

        infoManager.catInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                animal.getDescription());
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(CATS)
                ));

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

        when(keyboardFactory.getInlineKeyboard(
                List.of("Назад"),
                List.of(1),
                List.of(DOGS)
        )).thenReturn(
                keyboardFactory_.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(DOGS)
                )
        );


        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                animal.getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(DOGS)
                ))).thenReturn(
                new AnswerMethodFactory().getSendMessage(callbackQuery.message().chat().id(),
                        animal.getDescription(),
                        keyboardFactory_.getInlineKeyboard(
                                List.of("Назад"),
                                List.of(1),
                                List.of(DOGS)
                        ))
        );

        infoManager.dogInformation(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                animal.getDescription());
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(DOGS)
                ));
    }


    @Test
    public void putTOUserChatId_AnimalId_Test(){
        infoManager.putTOUserChatId_AnimalId(1L, 1L);
    }

    @Test
    public void answerMessage_Test() {

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

}
