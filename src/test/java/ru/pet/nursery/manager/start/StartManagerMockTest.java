package ru.pet.nursery.manager.start;

import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;

@ExtendWith(MockitoExtension.class)
class StartManagerMockTest {
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
    void answerCommand_Test() {

    }

    @Test
    void answerMessage_Test() {
    }

    @Test
    void answerCallbackQuery_Test() {
    }
}