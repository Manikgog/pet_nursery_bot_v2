package ru.pet.nursery.manager.start;

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
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.data.CallbackData.*;

@ExtendWith(MockitoExtension.class)
class StartManagerMockTest {
    @Mock
    TelegramBot telegramBot;
    @Mock
    AnswerMethodFactory answerMethodFactory;
    @Mock
    KeyboardFactory keyboardFactory;
    @InjectMocks
    StartManager startManager;
    private final Faker faker = new Faker();

    private final KeyboardFactory keyboardFactory_ = new KeyboardFactory();
    private final AnswerMethodFactory answerMethodFactory_ = new AnswerMethodFactory();


    @Test
    void answerCommand_Test() throws IOException {
        Update update = getUpdate("update_start.json");
        String answerMessage = """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO, REPORT, VOLUNTEER)
        );

        when(keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO, REPORT, VOLUNTEER)
        )).thenReturn(
                inlineKeyboardMarkup
        );

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

        startManager.answerCommand(update);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                answerMessage);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }

    @Test
    void answerMessage_Test() {

    }

    @Test
    void answerCallbackQuery_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_start.json");
        String answerMessage = """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO, REPORT, VOLUNTEER)
        );

        when(keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO, REPORT, VOLUNTEER)
        )).thenReturn(
                inlineKeyboardMarkup
        );

        when(answerMethodFactory.getSendMessage(
                callbackQuery.message().chat().id(),
                answerMessage,
                inlineKeyboardMarkup
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        callbackQuery.message().chat().id(),
                        answerMessage,
                        inlineKeyboardMarkup
                )
        );

        startManager.answerCallbackQuery(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                answerMessage);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);

    }

    private Update getUpdate(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\start\\" + filename;
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/start/" + filename;
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
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\start\\" + filename ;
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/start/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath)
                )
        );

        return BotUtils.fromJson(json, CallbackQuery.class);
    }
}