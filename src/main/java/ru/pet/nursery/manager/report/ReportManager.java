package ru.pet.nursery.manager.report;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.validator.ReportValidator;

import java.util.List;

import static ru.pet.nursery.data.CallbackData.*;

@Component
public class ReportManager extends AbstractManager {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final ReportValidator reportValidator;
    private final UserRepo userRepo;
    private final ReportService reportService;

    public ReportManager(AnswerMethodFactory answerMethodFactory,
                         KeyboardFactory keyboardFactory,
                         TelegramBot telegramBot,
                         ReportValidator reportValidator,
                         UserRepo userRepo,
                         ReportService reportService) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.reportValidator = reportValidator;
        this.userRepo = userRepo;
        this.reportService = reportService;
    }

    @Override
    public void answerCommand(Update update){
        // здесь должен возвращаться список в виде кнопок нажав на которые можно отправить отчет:
        // инструкция по отправке отчета
        // отправка отчета
        String answerMessage = """
                  // здесь должен возвращаться список в виде кнопок нажав на которые можно отправить отчет:
                  // инструкция по отправке отчета
                  // отправка отчета
                """;
        SendMessage sendMessage = answerMethodFactory.getSendMessage(update.message().chat().id(),
                answerMessage,
                null);
        telegramBot.execute(sendMessage);
    }

    @Override
    public void answerMessage(Update update) {

    }

    @Override
    public void answerCallbackQuery(CallbackQuery callbackQuery){
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("инструкция по отправке отчёта",
                                "фото питомца",
                                "поведение питомца",
                                "диета питомца",
                                "здоровье питомца"),
                        List.of(1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH)
                ));
        telegramBot.execute(sendMessage);
    }

    public void answerInstruction(CallbackQuery callbackQuery){
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Для выполнения отчёта необходимо
                        1. Сделать фотографию питомца, которого
                        вы усыновили у нас и отправить ее через бота
                        2. Описать как изменилось поведение питомца
                        и отправить через бота
                        3. Описать его диету и отправить через бота
                        4. Описать самочувствие питомца и отправить
                        через бота
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                ));
        telegramBot.execute(sendMessage);
    }

    /**
     * Метод для отправки сообщения пользователю кнопки и действий по
     * отправке фотографии для отчёта
     * @param callbackQuery - запрос обратного вызова пользователю
     */
    public void answerFoto(CallbackQuery callbackQuery){
        // проверить есть ли в базе данных пользователь с таким chatId, который усыновил животное
        long adopterId = callbackQuery.message().chat().id();
        try {
            reportValidator.validateIsAdopter(adopterId);
        }catch (IllegalParameterException e){
            // если пользователь не усыновлял животное, то отправить пользователю сообщение, что он не усыновлял животное
            answerUserIsNotAdopter(callbackQuery);
            return;
        }
        // проверить есть ли в базе данных в таблице report_table отчёт для этого пользователя на сегодня
        User user = userRepo.findById(adopterId).orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        if(!reportValidator.isReportInDataBase(adopterId, user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Cначала нажмите на кнопку в надписью "фото питомца".
                        Затем сфотографируйте его и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("фото питомца", "назад"),
                        List.of(1, 1),
                        List.of(FOTO, BACK_TO_REPORT_MENU)
                )
        );
        telegramBot.execute(sendMessage);
    }

    public void answerHealth(CallbackQuery callbackQuery){
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Cначала нажмите на кнопку в надписью "здоровье питомца".
                        Затем напишите текст и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("здоровье питомца", "назад"),
                        List.of(1, 1),
                        List.of(HEALTH, BACK_TO_REPORT_MENU)
                )
        );
        telegramBot.execute(sendMessage);
    }

    public void answerDiet(CallbackQuery callbackQuery){
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Cначала нажмите на кнопку в надписью "диета питомца".
                        Затем напишите текст и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("диета питомца", "назад"),
                        List.of(1, 1),
                        List.of(DIET, BACK_TO_REPORT_MENU)
                )
        );
        telegramBot.execute(sendMessage);
    }

    public void answerBehaviour(CallbackQuery callbackQuery){
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Cначала нажмите на кнопку в надписью "поведение питомца".
                        Затем напишите текст и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("поведение питомца", "назад"),
                        List.of(1, 1),
                        List.of(BEHAVIOUR, BACK_TO_REPORT_MENU)
                )
        );
        telegramBot.execute(sendMessage);
    }

    public void answerUserIsNotAdopter(CallbackQuery callbackQuery){
        String answerMessage = """
                  Вы не усыновляли нашего питомца
                """;
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                answerMessage,
                null);
        telegramBot.execute(sendMessage);
    }


}
