package ru.pet.nursery.factory;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AnswerMethodFactoryTest {
    @MockBean
    private TelegramBot telegramBot;
    private final AnswerMethodFactory methodFactory = new AnswerMethodFactory();
    private final KeyboardFactory keyboardFactory = new KeyboardFactory();
    private final InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

    public AnswerMethodFactoryTest(){}
    @Test
    public void getSendMessage_Test() {
        Long chatId = 123L;
        String text = "test text";

        SendMessage expected = new SendMessage(chatId, text).replyMarkup(keyboardMarkup);

        SendMessage actual = methodFactory.getSendMessage(chatId, text, keyboardMarkup);

        assertEquals(expected.getParameters(), actual.getParameters());
        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getResponseType(), actual.getResponseType());
    }


    @Test
    public void getSendFoto_Test() throws IOException {
        Long chatId = 123L;
        byte[] photoArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        InlineKeyboardMarkup keyboard = keyboardFactory.getInlineKeyboard(List.of("button 1", "button 2"), List.of(1, 1), List.of("кнопка 1", "кнопка 2"));
        SendPhoto expected = new SendPhoto(chatId, photoArray).replyMarkup(keyboard);

        SendPhoto actual = methodFactory.getSendFoto(chatId, photoArray, keyboard);

        assertEquals(expected.getParameters(), actual.getParameters());
        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getResponseType(), actual.getResponseType());
    }


    @Test
    public void getEditMessageText_Test() throws URISyntaxException, IOException {
        String path = "E:\\JavaProjects\\pet nursery\\nursery\\src\\test\\resources\\ru.pet.nursery.factory\\update.json";
       // List<Update> updates = getUpdates(readJsonFromResource(path));

    }

    private String readJsonFromResource(String filename) throws URISyntaxException, IOException {
        return Files.readString(
                Paths.get(Objects.requireNonNull(filename)
                )
        );
    }
    private List<Update> getUpdates(String content) throws URISyntaxException, IOException {
        String json = readJsonFromResource("text_update.json");

        return Collections.singletonList(BotUtils.fromJson(
                        json.replace("%command%", content),
                        Update.class
                )
        );
    }


}
