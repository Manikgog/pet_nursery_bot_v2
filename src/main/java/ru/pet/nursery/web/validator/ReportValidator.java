package ru.pet.nursery.web.validator;

import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.exception.ReportIsExistException;

import java.time.LocalDate;

@Component
public class ReportValidator {
    private final UserRepo userRepo;
    private final ReportRepo reportRepo;
    private final AnimalRepo animalRepo;

    public ReportValidator(UserRepo userRepo,
                           ReportRepo reportRepo,
                           AnimalRepo animalRepo) {
        this.userRepo = userRepo;
        this.reportRepo = reportRepo;
        this.animalRepo = animalRepo;
    }

    /**
     * Метод для валидации объекта класса Report
     * @param adopterId - идентификатор усыновителя из таблицы user_table
     */
    public void validate(long adopterId){
        // проверяется есть ли данный пользователь в базе данных
        User user = userRepo.findById(adopterId)
                .orElseThrow(() -> new IllegalFieldException("Идентификатор пользователя " + adopterId + " отсутствует в базе данных"));
        validateIsAdopter(adopterId);
        // проверяется, составлялся ли отчёт для этого пользователя сегодня
        if(isReportInDataBase(adopterId, user)){
            throw new ReportIsExistException("Отчет за сегодняшний день уже есть в базе данных");
        }
    }

    /**
     * Проверяется наличие отчёта для этого пользователя на сегодня
     * @return true - отчёт на сегодня уже составлен, false - отчёта на сегодня ещё нет
     */
    public boolean isReportInDataBase(long adopteId, User user){
        return reportRepo.findByUserAndReportDate(user, LocalDate.now()) != null;
    }

    /**
     * Метод для проверки даты на null
     * @param date - дата отчёта
     */
    public void validateDate(LocalDate date){
        if(date == null){
            throw new IllegalFieldException("Поле даты не должно быть равным null");
        }
    }

    /**
     * Метод для проверки усыновлял ли животное из наших
     * приютов человек с переданным telegramUserId
     */
    public void validateIsAdopter(long telegramUserId){
        User user = userRepo.findById(telegramUserId).orElseThrow(() -> new EntityNotFoundException(telegramUserId));
        if(user.getTelegramUserId() == 1 || animalRepo.findByUser(user) == null){
            throw new IllegalParameterException("Пользователь с id = " + telegramUserId + " не усыновлял питомца");
        }
    }

}
