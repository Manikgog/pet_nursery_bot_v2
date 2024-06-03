package ru.pet.nursery.manager.report;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.pet.nursery.data.MessageData;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.validator.ReportValidator;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.pet.nursery.data.CallbackData.*;
import static ru.pet.nursery.data.ReportStatus.*;

@Component
public class ReportManager extends AbstractManager {
    @Value("${path.to.report_photo.folder}")
    private String reportPhoto;
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final ReportValidator reportValidator;
    private final UserRepo userRepo;
    private final AnimalRepo animalRepo;
    private final ReportService reportService;
    private final MessageData messageData;


    public ReportManager(AnswerMethodFactory answerMethodFactory,
                         KeyboardFactory keyboardFactory,
                         TelegramBot telegramBot,
                         ReportValidator reportValidator,
                         UserRepo userRepo,
                         AnimalRepo animalRepo,
                         ReportService reportService,
                         MessageData messageData) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.reportValidator = reportValidator;
        this.userRepo = userRepo;
        this.animalRepo = animalRepo;
        this.reportService = reportService;
        this.messageData = messageData;
    }

    @Override
    public void answerCommand(Update update){
        logger.info("Processing update in method answerCommand ReportManager class: {}", update);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(update.message().chat().id(),
                """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("инструкция по отправке отчёта",
                                "фото питомца",
                                "поведение питомца",
                                "диета питомца",
                                "здоровье питомца",
                                "назад"),
                        List.of(1, 1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START)
                ));
        telegramBot.execute(sendMessage);
    }


    @Override
    public void answerCallbackQuery(CallbackQuery callbackQuery){
        logger.info("Processing callbackQuery in method answerCallbackQuery ReportManager class: {}", callbackQuery);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("инструкция по отправке отчёта",
                                "фото питомца",
                                "поведение питомца",
                                "диета питомца",
                                "здоровье питомца",
                                "назад"),
                        List.of(1, 1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START)
                ));
        telegramBot.execute(sendMessage);
    }

    public void answerInstruction(CallbackQuery callbackQuery){
        logger.info("Processing callbackQuery in method answerInstruction ReportManager class: {}", callbackQuery);
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
     * Метод для отправки сообщения пользователю действий по
     * отправке фотографии для отчёта
     * @param callbackQuery - запрос обратного вызова пользователя
     */
    public void answerPhoto(CallbackQuery callbackQuery){
        logger.info("Processing callbackQuery in method answerPhoto ReportManager class: {}", callbackQuery);
        // проверить есть ли в базе данных пользователь с таким chatId, который усыновил животное
        long adopterId = callbackQuery.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new EntityNotFoundException(adopterId));
        try {
            reportValidator.validateIsAdopter(user);
        }catch (IllegalParameterException e){
            logger.warn(e.getMessage());
            // если пользователь не усыновлял животное, то отправить пользователю сообщение, что он не усыновлял животное
            answerUserIsNotAdopter(callbackQuery);
            return;
        }
        // проверить есть ли в базе данных в таблице report_table отчёт для этого пользователя на сегодня
        if(!reportValidator.isReportInDataBase(user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        // записываем статус отчёта для правильной реакции на следующее сообщение от этого пользователя
        messageData.putReportStatusByChatId(adopterId, PHOTO_STATUS);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Cфотографируйте питомца и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );

        telegramBot.execute(sendMessage);
    }

    /**
     * Метод для отправки сообщения пользователю действий по
     * отправке описания здоровья питомца для отчёта
     * @param callbackQuery - запрос обратного вызова пользователя
     */
    public void answerHealth(CallbackQuery callbackQuery){
        logger.info("Processing callbackQuery in method answerHealth ReportManager class: {}", callbackQuery);
        // проверить есть ли в базе данных пользователь с таким chatId, который усыновил животное
        long adopterId = callbackQuery.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new EntityNotFoundException(adopterId));
        try {
            reportValidator.validateIsAdopter(user);
        }catch (IllegalParameterException e){
            logger.warn(e.getMessage());
            // если пользователь не усыновлял животное, то отправить пользователю сообщение, что он не усыновлял животное
            answerUserIsNotAdopter(callbackQuery);
            return;
        }
        // проверить есть ли в базе данных в таблице report_table отчёт для этого пользователя на сегодня
        if(!reportValidator.isReportInDataBase(user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        // записываем статус отчёта для правильной реакции на следующее сообщение от этого пользователя
        messageData.putReportStatusByChatId(adopterId, HEALTH_STATUS);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Опишите здоровье питомца и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );
        telegramBot.execute(sendMessage);
    }


    /**
     * Метод для отправки сообщения пользователю действий по
     * отправке описания диеты питомца для отчёта
     * @param callbackQuery - запрос обратного вызова пользователя
     */
    public void answerDiet(CallbackQuery callbackQuery){
        logger.info("Processing callbackQuery in method answerDiet ReportManager class: {}", callbackQuery);
        // проверить есть ли в базе данных пользователь с таким chatId, который усыновил животное
        long adopterId = callbackQuery.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new EntityNotFoundException(adopterId));
        try {
            reportValidator.validateIsAdopter(user);
        }catch (IllegalParameterException e){
            logger.warn(e.getMessage());
            // если пользователь не усыновлял животное, то отправить пользователю сообщение, что он не усыновлял животное
            answerUserIsNotAdopter(callbackQuery);
            return;
        }
        // проверить есть ли в базе данных в таблице report_table отчёт для этого пользователя на сегодня
        if(!reportValidator.isReportInDataBase(user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        // записываем статус отчёта для правильной реакции на следующее сообщение от этого пользователя
        messageData.putReportStatusByChatId(adopterId, DIET_STATUS);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Опишите диету питомца и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );
        telegramBot.execute(sendMessage);
    }


    /**
     * Метод для отправки сообщения пользователю действий по
     * отправке описания поведения питомца для отчёта
     * @param callbackQuery - запрос обратного вызова пользователя
     */
    public void answerBehaviour(CallbackQuery callbackQuery){
        logger.info("Processing callbackQuery in method answerBehaviour ReportManager class: {}", callbackQuery);
        // проверить есть ли в базе данных пользователь с таким chatId, который усыновил животное
        long adopterId = callbackQuery.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new EntityNotFoundException(adopterId));
        try {
            reportValidator.validateIsAdopter(user);
        }catch (IllegalParameterException e){
            logger.warn(e.getMessage());
            // если пользователь не усыновлял животное, то отправить пользователю сообщение, что он не усыновлял животное
            answerUserIsNotAdopter(callbackQuery);
            return;
        }
        // проверить есть ли в базе данных в таблице report_table отчёт для этого пользователя на сегодня
        if(!reportValidator.isReportInDataBase(user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        // записываем статус отчёта для правильной реакции на следующее сообщение от этого пользователя
        messageData.putReportStatusByChatId(adopterId, BEHAVIOUR_STATUS);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Опишите поведение питомца и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );
        telegramBot.execute(sendMessage);
    }



    /**
     * Метод для отправки сообщения пользователю,
     * который не является усыновителем питомца
     * @param callbackQuery - запрос обратного вызова пользователя
     */
    public void answerUserIsNotAdopter(CallbackQuery callbackQuery){
        logger.warn("Method answerUserIsNotAdopter. User with id = {} is not adopter", callbackQuery.message().chat().id());
        String answerMessage = """
                Вы не усыновляли нашего питомца
                """;
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                answerMessage,
                null);
        telegramBot.execute(sendMessage);
    }


    /**
     * Метод для загрузки фотографии питомца для отчёта
     * @param update - обновление от пользователя
     * @throws IOException - исключение ввода-вывода
     */
    public void uploadPhotoToReport(Update update) throws IOException {
        logger.info("Processing update in method uploadPhotoToReport ReportManager class: {}", update);
        long adopterId = update.message().chat().id();
        User user = userRepo.findById(adopterId)
                .orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        Report report = reportService.findByUserAndDate(user, LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        List<Animal> animals = animalRepo.findByUser(user);
        if(animals.isEmpty()){
            logger.warn("Method uploadPhotoToReport. User with id = {} is not adopter", adopterId);
            throw new IllegalParameterException("Пользователь с id = " + adopterId + " не усыновлял питомца");
        }

        if(update.message().photo() != null){
            PhotoSize[] photos = update.message().photo();
            String fileId;

            fileId = photos[photos.length - 1].fileId();

            GetFile request = new GetFile(fileId);
            GetFileResponse getFileResponse = telegramBot.execute(request);

            File file = getFileResponse.file();

            String fileName = String.format(
                    "%d_%s.%s",
                    report.getId(),
                    LocalDate.now(),
                    StringUtils.getFilenameExtension(file.filePath())
            );

            Path filePath = Paths.get(reportPhoto, fileName);

            String urlTelegramFile = String.format("https://api.telegram.org/file/bot%s/%s",
                    telegramBot.getToken(),
                    file.filePath());

            Files.copy(
                    new URL(urlTelegramFile).openStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            reportService.updatePhotoPath(report.getId(), filePath.toString());

            sendMessage(user.getTelegramUserId(), "Фотография вашего питомца добавлена к отчёту");
            return;
        }
        sendMessage(user.getTelegramUserId(), "Мы ожидали от вас фото питомца");
    }


    /**
     * Метод для добавления описания диеты питомца в отчёт
     * @param update - обновление от пользователя
     */
    public void uploadDietToReport(Update update){
        logger.info("Processing update in method uploadDietToReport ReportManager class: {}", update);
        long adopterId = update.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        List<Animal> animals = animalRepo.findByUser(user);
        if(animals.isEmpty()){
            logger.warn("Method uploadDietToReport. User with id = {} is not adopter", adopterId);
            throw new IllegalParameterException("Пользователь с id = " + adopterId + " не усыновлял питомца");
        }
        String diet = update.message().text();
        if(diet.isBlank() || diet.isEmpty()){
            sendMessage(user.getTelegramUserId(), "Описание диеты не должно быть пустой строкой");
            return;
        }
        Report report = reportService.findByUserAndDate(user, LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        if(report == null) {
            return;
        }
        reportService.updateDiet(report.getId(), diet);

        sendMessage(user.getTelegramUserId(), "Описание диеты вашего питомца добавлено к отчёту");
    }


    /**
     * Метод для добавления описания здоровья питомца в отчёт
     * @param update - обновление от пользователя
     */
    public void uploadHealthToReport(Update update){
        logger.info("Processing update in method uploadHealthToReport ReportManager class: {}", update);
        long adopterId = update.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        List<Animal> animals = animalRepo.findByUser(user);
        if(animals.isEmpty()){
            logger.warn("Method uploadHealthToReport. User with id = {} is not adopter", adopterId);
            throw new IllegalParameterException("Пользователь с id = " + adopterId + " не усыновлял питомца");
        }
        String health = update.message().text();
        if(health.isBlank() || health.isEmpty()){
            logger.warn("Method uploadHealthToReport. The description of health should not be an empty string. User id = {}", user.getTelegramUserId());
            sendMessage(user.getTelegramUserId(), "Описание здоровья не должно быть пустой строкой");
            return;
        }
        Report report = reportService.findByUserAndDate(user, LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        if(report == null) {
            return;
        }
        reportService.updateHealth(report.getId(), health);

        sendMessage(user.getTelegramUserId(), "Описание здоровья вашего питомца добавлено к отчёту");
    }



    /**
     * Метод для добавления описания поведения питомца в отчёт
     * @param update - обновление от пользователя
     */
    public void uploadBehaviourToReport(Update update){
        logger.info("Processing update in method uploadBehaviourToReport ReportManager class: {}", update);
        long adopterId = update.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        List<Animal> animals = animalRepo.findByUser(user);
        if(animals.isEmpty()){
            logger.warn("Method uploadBehaviourToReport. User with id = {} is not adopter", adopterId);
            throw new IllegalParameterException("Пользователь с id = " + adopterId + " не усыновлял питомца");
        }

        String behaviour = update.message().text();
        if(behaviour.isBlank() || behaviour.isEmpty()){
            sendMessage(user.getTelegramUserId(), "Описание поведения не должно быть пустой строкой");
            return;
        }
        Report report = reportService.findByUserAndDate(user, LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        if(report == null) {
           return;
        }
        reportService.updateBehaviour(report.getId(), behaviour);
        sendMessage(user.getTelegramUserId(), "Описание поведения вашего питомца добавлено к отчёту");
    }


    /**
     * Метод для отправки сообщения пользователю
     * @param chatId - идентификатор чата
     * @param text - текст сообщения
     */
    public void sendMessage(long chatId, String text){
        logger.info("Processing method sendMessage ReportManager class: chatId: {}, text: {}", chatId, text);
        SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId,
                text,
                null);
        telegramBot.execute(sendMessage);
    }


}
