package ru.pet.nursery.manager.volunteer;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
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
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.repository.VolunteerRepo;
import ru.pet.nursery.web.service.VolunteerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static ru.pet.nursery.data.CallbackData.*;

@ExtendWith(MockitoExtension.class)
class VolunteerManagerMockTest {
    @Mock
    TelegramBot telegramBot;
    @Mock
    AnswerMethodFactory answerMethodFactory;
    @Mock
    KeyboardFactory keyboardFactory;
    @Mock
    VolunteerRepo volunteerRepo;
    @Mock
    VolunteerService volunteerService;
    @InjectMocks
    VolunteerManager volunteerManager;
    private final Faker faker = new Faker();

    private final KeyboardFactory keyboardFactory_ = new KeyboardFactory();
    private final AnswerMethodFactory answerMethodFactory_ = new AnswerMethodFactory();

    @Test
    void answerCommand_TestByVolunteerIsActive() throws IOException {
        Update update = getUpdate("update_volunteer.json");

        Volunteer volunteer = new Volunteer();

        volunteer.setName("Name");
        volunteer.setId(1);
        volunteer.setActive(true);
        volunteer.setPhoneNumber("+7 654-987-3214");
        volunteer.setTelegramUserId(123);
        String answerMessage = """
                  // Задайте вопрос волонтеру.
                """;
        String startMessageToVolunteer = """
                   Вопрос у пользователя
                """ + "@" + update.message().chat().username();


        when(volunteerRepo.getVolunteerIsActive()).thenReturn(volunteer);

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Назад"),
                List.of(1),
                List.of(START.toString())
        );

        when(keyboardFactory.getInlineKeyboard(List.of("Назад"),
                List.of(1),
                List.of(START.toString()))).thenReturn(inlineKeyboardMarkup);

        when(answerMethodFactory.getSendMessage(
                update.message().chat().id(),
                answerMessage,
                inlineKeyboardMarkup
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        update.message().chat().id(),
                        answerMessage,
                        inlineKeyboardMarkup
                )
        );

        InlineKeyboardMarkup inlineKeyboardMarkup1 = keyboardFactory_.getInlineKeyboard(
                List.of("Закрыть чат"),
                List.of(1),
                List.of(CLOSE_CHAT.toString())
        );

        when(keyboardFactory.getInlineKeyboard(List.of("Закрыть чат"),
                List.of(1),
                List.of(CLOSE_CHAT.toString()))).thenReturn(inlineKeyboardMarkup1);

        SendMessage sendMessageToVolunteer = answerMethodFactory_.getSendMessage(
                volunteer.getTelegramUserId(),
                startMessageToVolunteer,
                inlineKeyboardMarkup1
                );

        when(answerMethodFactory.getSendMessage(
                volunteer.getTelegramUserId(),
                startMessageToVolunteer,
                inlineKeyboardMarkup1
        )).thenReturn(sendMessageToVolunteer);

        volunteerManager.answerCommand(update);

        ArgumentCaptor<SendMessage> argumentCaptorStartMessageToVolunteer = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptorStartMessageToVolunteer.capture());
        SendMessage actual = argumentCaptorStartMessageToVolunteer.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(startMessageToVolunteer);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup1);

    }


    @Test
    void answerCommand_TestByVolunteerIsNotActive() throws IOException {
        Update update = getUpdate("update_volunteer.json");

        String answerMessage = """
                  // Задайте вопрос волонтеру.
                """;
        String answerMessageWithoutVolunteer = """
                    Все волонтеры заняты. Вам ответит первый освободившийся работник.
                    """;


        when(volunteerRepo.getVolunteerIsActive()).thenReturn(null);

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Назад"),
                List.of(1),
                List.of(START)
        );

        when(keyboardFactory.getInlineKeyboard(List.of("Назад"),
                List.of(1),
                List.of(START))).thenReturn(inlineKeyboardMarkup);

        when(answerMethodFactory.getSendMessage(
                update.message().chat().id(),
                answerMessage,
                inlineKeyboardMarkup
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        update.message().chat().id(),
                        answerMessage,
                        inlineKeyboardMarkup
                )
        );


        SendMessage sendMessageToUser = answerMethodFactory_.getSendMessage(
                update.message().chat().id(),
                answerMessageWithoutVolunteer,
                null
        );

        when(answerMethodFactory.getSendMessage(
                update.message().chat().id(),
                answerMessageWithoutVolunteer,
                null
        )).thenReturn(sendMessageToUser);

        volunteerManager.answerCommand(update);

        ArgumentCaptor<SendMessage> argumentCaptorAnswerMessage = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptorAnswerMessage.capture());
        SendMessage actual = argumentCaptorAnswerMessage.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(answerMessageWithoutVolunteer);

    }


    @Test
    void answerCallbackQuery() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_volunteer.json");

        String answerMessage = """
                  Ожидайте, с Вами свяжется первый свободный волонтер нашего питомника.
                """;

        String startMessageToVolunteer = """
                   Вопрос у пользователя
                """ + "@" + callbackQuery.message().chat().username();

        long userChatId = callbackQuery.message().chat().id();

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("Назад"),
                List.of(1),
                List.of(START)
        );

        when(keyboardFactory.getInlineKeyboard(List.of("Назад"),
                List.of(1),
                List.of(START))).thenReturn(inlineKeyboardMarkup);

        SendMessage sendMessageToUser = answerMethodFactory_.getSendMessage(
                userChatId,
                answerMessage,
                inlineKeyboardMarkup);


        when(answerMethodFactory.getSendMessage(
                userChatId,
                answerMessage,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(START)
                )
        )).thenReturn(sendMessageToUser);

        Volunteer volunteer = new Volunteer();
        volunteer.setTelegramUserId(123L);

        when(volunteerRepo.getVolunteerIsActive()).thenReturn(volunteer);

        InlineKeyboardMarkup volunteerKeyboard = keyboardFactory_.getInlineKeyboard(
                List.of("Закрыть чат"),
                List.of(1),
                List.of(CLOSE_CHAT)
        );

        when(keyboardFactory.getInlineKeyboard(List.of("Закрыть чат"),
                List.of(1),
                List.of(CLOSE_CHAT))).thenReturn(volunteerKeyboard);

        SendMessage sendMessageToVolunteer = answerMethodFactory_.getSendMessage(
                volunteer.getTelegramUserId(),
                startMessageToVolunteer,
                volunteerKeyboard);

        when(answerMethodFactory.getSendMessage(
                        volunteer.getTelegramUserId(),
                        startMessageToVolunteer,
                        keyboardFactory.getInlineKeyboard(
                                List.of("Закрыть чат"),
                                List.of(1),
                                List.of(CLOSE_CHAT)
                        ))).thenReturn(sendMessageToVolunteer);

        volunteerManager.answerCallbackQuery(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                startMessageToVolunteer);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(volunteerKeyboard);

    }

    private Update getUpdate(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\volunteer\\" + filename;
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/volunteer/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath))
        );
        return BotUtils.fromJson(
                json,
                Update.class
        );
    }

    private CallbackQuery readJsonFromResource(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\volunteer\\" + filename ;
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/volunteer/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath)
                )
        );

        return BotUtils.fromJson(json, CallbackQuery.class);
    }
}