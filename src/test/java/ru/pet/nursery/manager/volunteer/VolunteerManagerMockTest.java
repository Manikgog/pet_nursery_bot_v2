package ru.pet.nursery.manager.volunteer;

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
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VolunteerManagerMockTest {
    @Mock
    TelegramBot telegramBot;
    @Mock
    AnswerMethodFactory answerMethodFactory;
    @Mock
    KeyboardFactory keyboardFactory;
    @InjectMocks
    VolunteerManager volunteerManager;
    private final Faker faker = new Faker();

    private final KeyboardFactory keyboardFactory_ = new KeyboardFactory();
    private final AnswerMethodFactory answerMethodFactory_ = new AnswerMethodFactory();

    @Test
    void answerCommand_Test() throws IOException {
        Update update = getUpdate("update_volunteer.json");

        String answerMessage = """
                  // связь с волонтером через телеграм
                """;

        when(answerMethodFactory.getSendMessage(
                update.message().chat().id(),
                answerMessage,
                null
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        update.message().chat().id(),
                        answerMessage,
                        null
                )
        );

        volunteerManager.answerCommand(update);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                answerMessage);

    }

    @Test
    void answerMessage() {
    }

    @Test
    void answerCallbackQuery() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_volunteer.json");

        String answerMessage = """
                  // связь с волонтером через телеграм
                """;

        when(answerMethodFactory.getSendMessage(
                callbackQuery.message().chat().id(),
                answerMessage,
                null
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        callbackQuery.message().chat().id(),
                        answerMessage,
                        null
                )
        );

        volunteerManager.answerCallbackQuery(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                answerMessage);

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