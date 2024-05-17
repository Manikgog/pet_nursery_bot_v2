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
    @JoinColumn(name = "telegramUserId")
    private User user;

    @Column(columnDefinition="TEXT", name="path_to_foto")
    private String path_to_foto;

    @Column(columnDefinition="TEXT", name="diet")
    private String diet;

    @Column(columnDefinition="TEXT", name="health")
    private String health;

    @Column(columnDefinition="TEXT", name="behaviour")
    private String behaviour;

    @Column(name = "foto_is_accepted", nullable = false)
    private boolean foto_is_accepted;

    @Column(name = "diet_is_accepted", nullable = false)
    private boolean diet_is_accepted;

    @Column(name = "health_is_accepted", nullable = false)
    private boolean health_is_accepted;

    @Column(name = "behaviour_is_accepted", nullable = false)
    private boolean behaviour_is_accepted;

    @Column(name = "all_items_is_accepted", nullable = false)
    private boolean all_items_is_accepted;
}
