package ru.pet.nursery.factory;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
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

        expected = new SendMessage(chatId, text);
        actual = methodFactory.getSendMessage(chatId, text, null);

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

        SendPhoto actual = methodFactory.getSendPhoto(chatId, photoArray, keyboard);

        assertEquals(expected.getParameters(), actual.getParameters());
        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getResponseType(), actual.getResponseType());
    }


    @Test
    public void getEditMessageText_Test() throws URISyntaxException, IOException {
        Long chatId = 1874598997L;
        String text = "text";
        InlineKeyboardMarkup keyboard = keyboardFactory.getInlineKeyboard(List.of("button 1", "button 2"), List.of(1, 1), List.of("кнопка 1", "кнопка 2"));
        CallbackQuery callbackQuery = getCallbackQuery();
        EditMessageText expected = new EditMessageText(chatId, callbackQuery.message().messageId(), text).replyMarkup(keyboard);

        EditMessageText actual = methodFactory.getEditMessageText(callbackQuery, text, keyboard);

        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getResponseType(), actual.getResponseType());
        assertEquals(expected.getParameters(), actual.getParameters());
        assertEquals(expected.getMethod(), actual.getMethod());


        expected = new EditMessageText(chatId, callbackQuery.message().messageId(), text);

        actual = methodFactory.getEditMessageText(callbackQuery, text, null);

        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getResponseType(), actual.getResponseType());
        assertEquals(expected.getParameters(), actual.getParameters());
        assertEquals(expected.getMethod(), actual.getMethod());

    }


    public List<Update> getListUpdates() throws URISyntaxException, IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\factory\\update.json";
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/factory/update.json";
        }
        String updateJson = readJsonFromResource(strPath);
        return getUpdates(updateJson);
    }

    private String readJsonFromResource(String filename) throws IOException {
        return Files.readString(
                Paths.get(Objects.requireNonNull(filename)
                ), Charset.forName("WINDOWS-1251")
        );
    }
    private List<Update> getUpdates(String content) throws URISyntaxException, IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\factory\\update.json";
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/factory/update.json";
        }
        String json = readJsonFromResource(strPath);

        return Collections.singletonList(BotUtils.fromJson(
                        json.replace("%command%", content),
                        Update.class
                )
        );
    }


    public CallbackQuery getCallbackQuery() throws URISyntaxException, IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\factory\\update_with_callbackquery.json";
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/factory/update_with_callbackquery.json";
        }
        String json = readJsonFromResource(strPath);
        Update update = BotUtils.fromJson(
                json,
                Update.class
        );
        return update.callbackQuery();
    }

    @Test
    public void getDeleteMessage_Test(){
        Long chatId = 1874598997L;
        Integer messageId = 1234656;

        DeleteMessage expected = new DeleteMessage(chatId, messageId);

        DeleteMessage actual = methodFactory.getDeleteMessage(chatId, messageId);

        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getResponseType(), actual.getResponseType());
        assertEquals(expected.getParameters(), actual.getParameters());
        assertEquals(expected.getMethod(), actual.getMethod());
    }


}
