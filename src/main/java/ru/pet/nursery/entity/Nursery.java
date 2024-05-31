package ru.pet.nursery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "nursery_table")
public class Nursery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name_shelter", columnDefinition="VARCHAR(50)")
    private String nameShelter;
    @Column(name = "address", columnDefinition="TEXT")
    private String address;
    @Column(name = "phone_number", columnDefinition="VARCHAR(20)")
    private String phoneNumber;
    /**
     * Это поле необходимо для поиска приютов по видам животных. Если true - для собак, false - для кошек.
     */
    @Column(name = "kind_of_animal")
    private boolean forDog;
    @Column(name = "map_link", columnDefinition="TEXT")
    private String mapLink;

    public Nursery() {
    }

    public Nursery(Long id, String nameShelter, String address, String phoneNumber, boolean forDog, String mapLink) {
        this.id = id;
        this.nameShelter = nameShelter;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.forDog = forDog;
        this.mapLink = mapLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nursery nursery = (Nursery) o;
        return forDog == nursery.forDog && Objects.equals(id, nursery.id) && Objects.equals(nameShelter, nursery.nameShelter) && Objects.equals(address, nursery.address) && Objects.equals(phoneNumber, nursery.phoneNumber) && Objects.equals(mapLink, nursery.mapLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameShelter, address, phoneNumber, forDog, mapLink);
    }
}
