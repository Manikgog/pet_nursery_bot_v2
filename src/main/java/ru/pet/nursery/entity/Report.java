package ru.pet.nursery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="report_table")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @ManyToOne()
    @JoinColumn(name = "telegram_user_id")
    private User user;

    @Column(columnDefinition="TEXT", name="path_to_photo")
    private String pathToPhoto;

    @Column(columnDefinition="TEXT", name="diet")
    private String diet;

    @Column(columnDefinition="TEXT", name="health")
    private String health;

    @Column(columnDefinition="TEXT", name="behaviour")
    private String behaviour;

    @Column(name = "photo_is_accepted", nullable = false)
    private boolean photoIsAccepted;

    @Column(name = "diet_is_accepted", nullable = false)
    private boolean dietIsAccepted;

    @Column(name = "health_is_accepted", nullable = false)
    private boolean healthIsAccepted;

    @Column(name = "behaviour_is_accepted", nullable = false)
    private boolean behaviourIsAccepted;

    @Column(name = "all_items_is_accepted", nullable = false)
    private boolean allItemsIsAccepted;
}
