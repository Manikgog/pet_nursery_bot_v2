package ru.pet.nursery.web.service;

import net.datafaker.Faker;
import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.repository.ShelterRepo;

import ru.pet.nursery.web.exception.ShelterNotFoundException;
import ru.pet.nursery.web.exception.ShelterNullException;
import ru.pet.nursery.web.service.ShelterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShelterServiceTest {
    @Mock
    private ShelterRepo shelterRepo;
    @InjectMocks
    private ShelterService shelterService;

    private final Faker faker = new Faker();

    private final List<Nursery> nurseryList = new ArrayList<>(List.of(
            new Nursery(1L, "Верный друг", "г. Томск ул. 79-гв. девизии 21/1", "8-987-765-43-21", true),
            new Nursery(2L, "Колыбель дианы", "г. Томск ул. Восточная 114", "8-899-777-82-11", false),
            new Nursery(3L, "Содружество", "г. Томск ул. Кирпчная 1", "8-987-765-43-21", true),
            new Nursery(4L, "Хвостатое братство", "г. Томск ул. Ю.Ковалева 12", "8-987-765-43-21", false),
            new Nursery(5L, "Служба отлова городских собак", "г. Томск ул. Б.Хмельнитского 58", "8-987-765-43-21", true)
    ));

    @Test
    void addShelterPositiveTest() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true);
        when(shelterRepo.save(any())).thenReturn(expected);
        nurseryList.add(expected);
        shelterService.addShelter(expected);
        when(shelterRepo.findById(expected.getId())).thenReturn(Optional.of(expected));
        assertThat(shelterService.findShelter(expected.getId())).isEqualTo(expected);
    }

    @Test
    void addShelterNegativeTest() {
        Nursery expected = new Nursery(7L, "чтото", "что то", null, true);
        lenient().when(shelterRepo.save(expected)).thenThrow(ShelterNullException.class);
        assertThatThrownBy(() -> shelterService.addShelter(expected)).isInstanceOf(ShelterNullException.class);
    }

    @Test
    void findShelterPositiveTest() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true);
        when(shelterRepo.save(expected)).thenReturn(expected);
        nurseryList.add(expected);
        shelterService.addShelter(expected);
        when(shelterRepo.findById(expected.getId())).thenReturn(Optional.of(expected));
        assertThat(shelterService.findShelter(expected.getId())).isEqualTo(expected);
    }
    @Test
    void findShelterNegativeTest(){
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true);
        lenient().when(shelterRepo.save(expected)).thenThrow(ShelterNotFoundException.class);
        assertThatThrownBy(() -> shelterService.findShelter(expected.getId())).isInstanceOf(ShelterNotFoundException.class);
    }

    @Test
    void updateShelterPositiveTest() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true);
        Nursery actual = new Nursery(6L, "Какой то приют2", "г. Н...2", "Номер телефона прежний", true);
        nurseryList.add(expected);
        when(shelterRepo.findAll()).thenReturn(nurseryList);
        assertThat(shelterRepo.findAll()).contains(expected);
        when(shelterRepo.findById(expected.getId())).thenReturn(Optional.of(actual));
        when(shelterRepo.save(actual)).thenReturn(actual);
        assertThat(shelterService.updateShelter(expected.getId(), actual)).isEqualTo(actual);
    }
    @Test
    void updateShelterNegativeTest(){
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true);
        when(shelterRepo.findById(expected.getId())).thenThrow(ShelterNotFoundException.class);
        assertThatThrownBy(() -> shelterService.updateShelter(6L, expected)).isInstanceOf(ShelterNotFoundException.class);
    }
    @Test
    void removeShelterPositiveTest() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true);
        nurseryList.add(expected);
        when(shelterRepo.save(expected)).thenReturn(expected);
        shelterService.addShelter(expected);
        when(shelterRepo.findById(expected.getId())).thenReturn(Optional.of(expected));
        assertThat(shelterService.removeShelter(expected.getId())).isEqualTo(expected);
        nurseryList.remove(expected);
        assertThat(shelterService.getAll()).doesNotContain(expected);
    }

    @Test
    void removeShelterNegativeTest() {
        when(shelterRepo.findById(7L)).thenThrow(ShelterNotFoundException.class);
        assertThatThrownBy(() -> shelterService.removeShelter(7L)).isInstanceOf(ShelterNotFoundException.class);
    }

    @Test
    void getAllShelter() {
    }

    @Test
    void getShelterForDog() {
    }
}