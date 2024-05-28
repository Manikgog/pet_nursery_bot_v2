package ru.pet.nursery.manager.volunteer;

import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;

@ExtendWith(MockitoExtension.class)
class VolunteerManagerMockTest {
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
    void answerCommand() {
    }

    @Test
    void answerMessage() {
    }

    @Test
    void answerCallbackQuery() {
    }
}