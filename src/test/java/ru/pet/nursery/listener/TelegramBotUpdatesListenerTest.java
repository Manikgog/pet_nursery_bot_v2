package ru.pet.nursery.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TelegramBotUpdatesListenerTest {
    final private Long CHAT_ID = 1874598997L;
    @MockBean
    private TelegramBot telegramBot;
    @Autowired
    private TelegramBotUpdatesListener telegramBotListener;



    @Test
    void process() throws URISyntaxException, IOException {
        String fileName = "update.json";
        Update start_update = getUpdateFromFile(fileName);
        fileName = "response_by_start.json";
        SendResponse response_by_start = getSendResponseFromFile(fileName);
        when(telegramBot.execute(any())).thenReturn(response_by_start, SendResponse.class);

        telegramBotListener.process(List.of(start_update));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        //Verify SendMessage-s:
        //  1) Greet User
        //  2) Next Level Menu
        Mockito.verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();
        // Check Menu Message:
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(CHAT_ID);
        /*Assertions.assertThat(actual.getParameters().size()).isEqualTo(4);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Выберите приют:");
        Assertions.assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        Assertions.assertThat(actual.getParameters().get("parse_mode"))
                .isEqualTo(ParseMode.Markdown.name());*/
    }

    @Test
    public void process_Test() throws IOException {
        String fileName = "update.json";
        Update update = getUpdateFromFile(fileName);
    }


    public Update getUpdateFromFile(String fileName) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\listener\\";
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/listener/";
        }
        strPath += fileName;
        File file = new File(strPath);
        Reader reader = new FileReader(file, Charset.forName("WINDOWS-1251"));
        Update update = BotUtils.parseUpdate(reader);
        return update;
    }

    public SendResponse getSendResponseFromFile(String fileName) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\listener\\";
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/listener/";
        }
        strPath += fileName;
        File file = new File(strPath);
        Reader reader = new FileReader(file, Charset.forName("WINDOWS-1251"));
        ObjectMapper objectMapper = new ObjectMapper();
        SendResponse sendResponse = objectMapper.readValue(reader, SendResponse.class);
        return sendResponse;
    }

}
