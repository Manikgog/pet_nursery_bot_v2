package ru.pet.nursery.manager.info;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.web.service.IAnimalService;
import ru.pet.nursery.web.service.IShelterService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.data.CallbackData.*;

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
    @InjectMocks
    InfoManager infoManager;

    private final Faker faker = new Faker();

    private KeyboardFactory keyboardFactory_ = new KeyboardFactory();
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
    public void catPhoto_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callbackquery_cat_photo.json");



    }


    private List<Nursery> createNurseries(int amount){
        List<Nursery> nurseries = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Nursery nursery = new Nursery();
            nursery.setNameShelter(faker.funnyName().name());
            nursery.setAddress(faker.address().fullAddress());
            nursery.setPhoneNumber(faker.phoneNumber().phoneNumberNational());
            if(i%2 == 0) {
                nursery.setForDog(false);
            }else{
                nursery.setForDog(true);
            }
        }
        return nurseries;
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
