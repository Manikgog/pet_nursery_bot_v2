package ru.pet.nursery.manager.report;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.pet.nursery.data.MessageData;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.validator.ReportValidator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static ru.pet.nursery.data.CallbackData.*;
import static ru.pet.nursery.data.MessageData.*;

@Component
public class ReportManager extends AbstractManager {
    private final String reportPhoto = "report_photo";
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final ReportValidator reportValidator;
    private final UserRepo userRepo;
    private final AnimalRepo animalRepo;
    private final ReportService reportService;

    public ReportManager(AnswerMethodFactory answerMethodFactory,
                         KeyboardFactory keyboardFactory,
                         TelegramBot telegramBot,
                         ReportValidator reportValidator,
                         UserRepo userRepo,
                         AnimalRepo animalRepo,
                         ReportService reportService) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.reportValidator = reportValidator;
        this.userRepo = userRepo;
        this.animalRepo = animalRepo;
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
                                "здоровье питомца",
                                "назад"),
                        List.of(1, 1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START)
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
        if(!reportValidator.isReportInDataBase(user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        // записываем статус отчёта для правильной реакции на следующее сообщение от этого пользователя
        MessageData.putToChatId_reportStatusMap(adopterId, PHOTO_STATUS);

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

    public void answerHealth(CallbackQuery callbackQuery){
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
        if(!reportValidator.isReportInDataBase(user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        // записываем статус отчёта для правильной реакции на следующее сообщение от этого пользователя
        MessageData.putToChatId_reportStatusMap(adopterId, HEALTH_STATUS);

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

    public void answerDiet(CallbackQuery callbackQuery){
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
        if(!reportValidator.isReportInDataBase(user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        // записываем статус отчёта для правильной реакции на следующее сообщение от этого пользователя
        MessageData.putToChatId_reportStatusMap(adopterId, DIET_STATUS);

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

    public void answerBehaviour(CallbackQuery callbackQuery){
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
        if(!reportValidator.isReportInDataBase(user)){
            // если отчёта в базе данных на сегодня ещё нет, то создаём его
            reportService.upload(adopterId);
        }
        // записываем статус отчёта для правильной реакции на следующее сообщение от этого пользователя
        MessageData.putToChatId_reportStatusMap(adopterId, BEHAVIOUR_STATUS);

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

    public void answerUserIsNotAdopter(CallbackQuery callbackQuery){
        String answerMessage = """
                  Вы не усыновляли нашего питомца
                """;
        SendMessage sendMessage = answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                answerMessage,
                null);
        telegramBot.execute(sendMessage);
    }

    public void uploadPhotoToReport(Update update) throws IOException {
        long adopterId = update.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        List<Animal> animals = animalRepo.findByUser(user);
        if(animals.isEmpty()){
            throw new IllegalParameterException("Пользователь с id = " + adopterId + " не усыновлял питомца");
        }

        Animal animalOld = animals.get(0);

        if(update.message().photo() != null){
            List<PhotoSize> photos = Arrays.stream(update.message().photo()).toList();
            PhotoSize photo = photos.get(0);
            String fileId = photo.fileId();
            String file = photo.toString();

            URL url = new URL("https://api.telegram.org/bot" + telegramBot.getToken() + "/getFile?file_id=" + fileId);

            String strPath = System.getProperty("user.dir");
            if(strPath.contains("\\")){
                strPath += "\\";
            }else{
                strPath += "/";
            }
            strPath += reportPhoto;
            Path path = Path.of(strPath);
            Path filePath = Path.of(path.toString(), animalOld.getId() + LocalDate.now().toString() + "."/* + getExtension(Objects.requireNonNull(animalPhoto.getOriginalFilename()))*/);
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);

            try(BufferedInputStream bis = new BufferedInputStream(url.openStream(), 1024);
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
            ){
                bis.transferTo(bos);
            }
        }

    }

    public void uploadDietToReport(Update update){
        long adopterId = update.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        List<Animal> animals = animalRepo.findByUser(user);
        if(animals.isEmpty()){
            throw new IllegalParameterException("Пользователь с id = " + adopterId + " не усыновлял питомца");
        }
        String diet = update.message().text();
        Report report = reportService.findByUserAndDate(user, LocalDate.now());
        if(report == null) {
            return;
        }
        reportService.updateDiet(report.getId(), diet);

        answerSuccessUpdate(user.getTelegramUserId(), "Описание диеты вашего питомца добавлено к отчёту");
    }



    public void uploadHealthToReport(Update update){
        long adopterId = update.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        List<Animal> animals = animalRepo.findByUser(user);
        if(animals.isEmpty()){
            throw new IllegalParameterException("Пользователь с id = " + adopterId + " не усыновлял питомца");
        }
        String health = update.message().text();
        Report report = reportService.findByUserAndDate(user, LocalDate.now());
        if(report == null) {
            return;
        }
        reportService.updateHealth(report.getId(), health);

        answerSuccessUpdate(user.getTelegramUserId(), "Описание здоровья вашего питомца добавлено к отчёту");
    }


    public void uploadBehaviourToReport(Update update){
        long adopterId = update.message().chat().id();
        User user = userRepo.findById(adopterId).orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        List<Animal> animals = animalRepo.findByUser(user);
        if(animals.isEmpty()){
            throw new IllegalParameterException("Пользователь с id = " + adopterId + " не усыновлял питомца");
        }

        String behaviour = update.message().text();
        Report report = reportService.findByUserAndDate(user, LocalDate.now());
        if(report == null) {
           return;
        }
        reportService.updateBehaviour(report.getId(), behaviour);
        answerSuccessUpdate(user.getTelegramUserId(), "Описание поведения вашего питомца добавлено к отчёту");
    }

    public void answerSuccessUpdate(long chatId, String text){
        SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId,
                text,
                null);
        telegramBot.execute(sendMessage);
    }


}
