package ru.pet.nursery.manager.info;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.ShelterRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.service.AnimalService;
import ru.pet.nursery.web.service.ShelterService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@SpringBootTest
public class InfoManagerTest {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final ShelterService shelterService;
    private final AnimalService animalService;
    @Mock
    AnimalRepo animalRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    ShelterRepo shelterRepo;
    @Mock
    TelegramBot telegramBot;
    @InjectMocks
    private final InfoManager infoManager;
    public InfoManagerTest(){
        this.answerMethodFactory = new AnswerMethodFactory();
        this.keyboardFactory = new KeyboardFactory();
        this.shelterService = new ShelterService(shelterRepo);
        this.animalService = new AnimalService(animalRepo, userRepo, shelterRepo);
        this.infoManager = new InfoManager(answerMethodFactory, keyboardFactory, telegramBot, shelterRepo, animalRepo, animalService);
    }

    @Test
    public void answerCommand_Test() throws IOException {
        Update update = getUpdate();
        infoManager.answerCommand(update);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();
    }


    private Update getUpdate() throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\factory\\update.json";
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/factory/update.json";
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


}
