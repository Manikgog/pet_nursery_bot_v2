package ru.pet.nursery.manager.info;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.web.service.AnimalService;
import ru.pet.nursery.web.service.ShelterService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.data.CallbackData.*;
import static ru.pet.nursery.data.CallbackData.START;

@ExtendWith(MockitoExtension.class)
public class InfoManagerTest {
    @Mock
    private TelegramBot telegramBot;
    @Mock
    private AnswerMethodFactory answerMethodFactory;
    @Mock
    private KeyboardFactory keyboardFactory;
    @Mock
    private ShelterService shelterService;
    @Mock
    private AnimalService animalService;
    @InjectMocks
    private InfoManager infoManager;

    private final KeyboardFactory keyboardFactory_ = new KeyboardFactory();
    private final AnswerMethodFactory answerMethodFactory_ = new AnswerMethodFactory();

    @Test
    public void answerCommand_Test() throws IOException {
        String json = Files.readString(
                Paths.get("E:\\JavaProjects\\pet nursery\\nursery\\src\\test\\resources\\ru.pet.nursery\\listener\\text_update.json")
                , Charset.forName("WINDOWS-1251"));
        Update update = getUpdate(json, "/info");
        when(answerMethodFactory.getSendMessage(1874598997L, "any", new InlineKeyboardMarkup()))
                .thenReturn(getSendMessage(1874598997L,
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
                )));
        infoManager.answerCommand(update);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Здесь вы можете посмотреть информацию о приютах, питомцах и требованиях к усыновителю");
        Assertions.assertThat(actual.getParameters().get("parse_mode"))
                .isEqualTo(ParseMode.Markdown.name());
    }

    private Update getUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%command%", replaced), Update.class);
    }

    private SendMessage getSendMessage(long chatId, String text, InlineKeyboardMarkup ikm){
        return answerMethodFactory_.getSendMessage(chatId, text, ikm);
    }
}
