package ru.pet.nursery.listener;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import ru.pet.nursery.handler.Handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TelegramBotUpdatesListener.class)
public class TelegramBotUpdatesListenerIntegratedTest {
    @Value("${telegram.bot.token}")
    private String token;

    @Autowired
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @MockBean
    private TelegramBot telegramBot;

    @Autowired
    private Handler handler;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    @Test
    public void testHandleUpdate() throws IOException {

        String jsonUpdate = getUpdateJson("update_start.json");

        HttpEntity<String> entity = new HttpEntity<>(jsonUpdate, new HttpHeaders());

        ResponseEntity<String> response = this.restTemplate.exchange(
                TELEGRAM_API_URL + token + "/sendMessage",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    private String getUpdateJson(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if (strPath.contains("\\")) {
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\listener\\" + filename;
        } else {
            strPath += "/src/test/resources/ru.pet.nursery/listener/" + filename;
        }
        return Files.readString(
                Paths.get(Objects.requireNonNull(strPath))
        );
    }
}

