package ru.pet.nursery.manager.get;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;

@Component
public class GetManager extends AbstractManager {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;

    public GetManager(AnswerMethodFactory answerMethodFactory,
                      KeyboardFactory keyboardFactory,
                      TelegramBot telegramBot) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    public void answerCommand(Update update){
        // здесь должен возвращаться список в виде кнопок нажав на которые можно получить:
        // требования к усыновителю
        // необходимые условия для содержания животного
        String answerMessage = """
                 // здесь должен возвращаться список в виде кнопок нажав на которые можно получить:
                 // требования к усыновителю
                 // необходимые условия для содержания животного
                """;
        SendMessage sendMessage = answerMethodFactory.getSendMessage(update.message().chat().id(),
                answerMessage,
                null);
        telegramBot.execute(sendMessage);
    }

    @Override
    public void answerMessage(Update update) {

    }

    public void answerCallbackQuery(CallbackQuery callbackQuery){
        // здесь должен возвращаться список в виде кнопок нажав на которые можно получить:
        // требования к усыновителю
        // необходимые условия для содержания животного
        String answerMessage = """
                 // здесь должен возвращаться список в виде кнопок нажав на которые можно получить:
                 // требования к усыновителю
                 // необходимые условия для содержания животного
                """;
        EditMessageText editMessageText = answerMethodFactory.getEditMessageText(callbackQuery, answerMessage, null);
        telegramBot.execute(editMessageText);
    }
}
