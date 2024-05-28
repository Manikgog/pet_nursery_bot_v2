package ru.pet.nursery.web.validator;

import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.exception.ReportIsExistException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
     * @param user - усыновитель из таблицы user_table
     */
    public void validate(User user){
        validateIsAdopter(user);
        // проверяется, составлялся ли отчёт для этого пользователя сегодня
        if(isReportInDataBase(user)){
            throw new ReportIsExistException("Отчет за сегодняшний день уже есть в базе данных");
        }
    }

    /**
     * Проверяется наличие отчёта для этого пользователя на сегодня
     * @return true - отчёт на сегодня уже составлен, false - отчёта на сегодня ещё нет
     */
    public boolean isReportInDataBase(User user){
        return reportRepo.findByUserAndReportDate(user, LocalDateTime.now()) != null;
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
    public void validateIsAdopter(User user){
        List<Animal> adoptedAnimalsByUser = animalRepo.findByUser(user);
        if(adoptedAnimalsByUser.isEmpty()){
            throw new IllegalParameterException("Пользователь с id = " + user.getTelegramUserId() + " не усыновлял питомца");
        }
    }

}
