package ru.pet.nursery.web.service;

import jakarta.servlet.http.HttpServletResponse;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IReportService {
    Report updateDiet(long id, String diet);

    Report updateHealth(long id, String health);

    Report updateBehaviour(long id, String behaviour);

    Report updateIsAllItemsIsAccepted(long id, boolean isAllItemsAccepted);

    Report updatePhotoIsAccepted(long id, boolean isPhotoAccepted);

    Report updateIsDietAccepted(long id, boolean isDietAccepted);

    Report updateIsHealthAccepted(long id, boolean isHealthAccepted);

    Report updateIsBehaviourAccepted(long id, boolean isBehaviourAccepted);

    List<Report> getListOfReportByDate(LocalDate date);

    Report findByUserAndDate(User user, LocalDateTime date);

    Report updatePhotoPath(long reportId, String path);

    Report getPhotoById(long id, HttpServletResponse response);

    List<Report> findByPetReturnDate();
}
