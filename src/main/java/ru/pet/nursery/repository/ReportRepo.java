package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepo extends JpaRepository<Report, Long> {
    List<Report> findByUser(User user);
    Report findByUserAndReportDate(User user, LocalDateTime reportDateTime);
    List<Report> findByReportDate(LocalDateTime reportDate);
    List<Report> findByNextReportDate(LocalDateTime localDateTime);
}
