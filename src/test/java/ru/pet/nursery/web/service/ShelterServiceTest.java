package ru.pet.nursery.web.service;

import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.ShelterRepo;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.exception.ShelterNotFoundException;
import ru.pet.nursery.web.exception.ShelterNullException;
import java.util.ArrayList;
import java.util.Collection;
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
    @Mock
    private AnimalRepo animalRepo;
    @InjectMocks
    private ShelterService shelterService;

    private final Faker faker = new Faker();

    private final List<Nursery> nurseryList = new ArrayList<>(List.of(
            new Nursery(1L, "Верный друг", "г. Томск ул. 79-гв. девизии 21/1", "8-987-765-43-21", true, null),
            new Nursery(2L, "Колыбель дианы", "г. Томск ул. Восточная 114", "8-899-777-82-11", false, null),
            new Nursery(3L, "Содружество", "г. Томск ул. Кирпчная 1", "8-987-765-43-21", true, null),
            new Nursery(4L, "Хвостатое братство", "г. Томск ул. Ю.Ковалева 12", "8-987-765-43-21", false, null),
            new Nursery(5L, "Служба отлова городских собак", "г. Томск ул. Б.Хмельницкого 58", "8-987-765-43-21", true, null)
    ));

    @Test
    void addShelterPositiveTest() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true, null);
        when(shelterRepo.save(any())).thenReturn(expected);
        nurseryList.add(expected);
        shelterService.addShelter(expected);
        when(shelterRepo.findById(expected.getId())).thenReturn(Optional.of(expected));
        assertThat(shelterService.findShelter(expected.getId())).isEqualTo(expected);
    }

    @Test
    void addShelterNegativeTest() {
        Nursery expected = new Nursery(7L, "чтото", "что то", null, true, null);
        lenient().when(shelterRepo.save(expected)).thenThrow(ShelterNullException.class);
        assertThatThrownBy(() -> shelterService.addShelter(expected)).isInstanceOf(ShelterNullException.class);
    }
    @Test
    void addShelterNegative_ifNullAddressTest() {
        Nursery expected = new Nursery(7L, "чтото", null, "4654465456", true, null);
        lenient().when(shelterRepo.save(expected)).thenThrow(ShelterNullException.class);
        assertThatThrownBy(() -> shelterService.addShelter(expected)).isInstanceOf(ShelterNullException.class);
    }
    @Test
    void addShelterNegative_ifNullNameShelterTest() {
        Nursery expected = new Nursery(7L, null, "что то", "4654465456", true, null);
        lenient().when(shelterRepo.save(expected)).thenThrow(ShelterNullException.class);
        assertThatThrownBy(() -> shelterService.addShelter(expected)).isInstanceOf(ShelterNullException.class);
    }

    @Test
    void findShelterPositiveTest() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true, null);
        when(shelterRepo.save(expected)).thenReturn(expected);
        nurseryList.add(expected);
        shelterService.addShelter(expected);
        when(shelterRepo.findById(expected.getId())).thenReturn(Optional.of(expected));
        assertThat(shelterService.findShelter(expected.getId())).isEqualTo(expected);
    }
    @Test
    void findShelterNegativeTest(){
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true, null);
        lenient().when(shelterRepo.save(expected)).thenThrow(ShelterNotFoundException.class);
        assertThatThrownBy(() -> shelterService.findShelter(expected.getId())).isInstanceOf(ShelterNotFoundException.class);
    }

    @Test
    void updateShelterPositiveTest() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true, null);
        Nursery actual = new Nursery(6L, "Какой то приют2", "г. Н...2", "Номер телефона прежний", true, null);
        nurseryList.add(expected);
        when(shelterRepo.findAll()).thenReturn(nurseryList);
        assertThat(shelterRepo.findAll()).contains(expected);
        when(shelterRepo.findById(expected.getId())).thenReturn(Optional.of(actual));
        when(shelterRepo.save(actual)).thenReturn(actual);
        assertThat(shelterService.updateShelter(expected.getId(), actual)).isEqualTo(actual);
    }
    @Test
    void updateShelterNegativeTest(){
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true, null);
        when(shelterRepo.findById(expected.getId())).thenThrow(ShelterNotFoundException.class);
        assertThatThrownBy(() -> shelterService.updateShelter(6L, expected)).isInstanceOf(ShelterNotFoundException.class);
    }
    @Test
    void removeShelterPositiveTest() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true, null);
        nurseryList.add(expected);
        when(shelterRepo.save(expected)).thenReturn(expected);
        shelterService.addShelter(expected);
        when(shelterRepo.findById(expected.getId())).thenReturn(Optional.of(expected));
        when(animalRepo.findByNursery(expected)).thenReturn(new ArrayList<>());
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
    void getAllShelterTest() {
        int limit = 2;
        int finalPage = 1;
        int finalSize = 2;
        List<Nursery> list = nurseryList.stream().limit(limit).toList();
        Page<Nursery> page = new PageImpl<>(list);
        when(shelterRepo.findAll(any(Pageable.class))).thenReturn(page);
        Collection<Nursery> actual = shelterService.getAllShelter(finalPage, finalSize);
        assertThat(actual).isNotNull().containsExactlyInAnyOrderElementsOf(list);
        assertThat(actual.size()).isEqualTo(limit);
    }

    @Test
    void getShelterForDog_ifForDog() {
        int limit = 2;
        int finalPage = 1;
        int finalSize = 2;
        boolean forDog = true;
        List<Nursery> list = nurseryList.stream().filter(kindOfAnimal -> kindOfAnimal.isForDog()==forDog).limit(limit).toList();
        Page<Nursery> page = new PageImpl<>(list);
        when(shelterRepo.findAll(any(Pageable.class))).thenReturn(page);
        Collection<Nursery> actual = shelterService.getShelterForDog(forDog,finalPage, finalSize);
        assertThat(actual).isNotNull().containsExactlyInAnyOrderElementsOf(list);
        assertThat(actual.size()).isEqualTo(limit);
    }
    @Test
    void getShelterForDog_ifForCat() {
        int limit = 2;
        int finalPage = 1;
        int finalSize = 2;
        boolean forDog = false;
        List<Nursery> list = nurseryList.stream().filter(kindOfAnimal -> kindOfAnimal.isForDog()==forDog).limit(limit).toList();
        Page<Nursery> page = new PageImpl<>(list);
        when(shelterRepo.findAll(any(Pageable.class))).thenReturn(page);
        Collection<Nursery> actual = shelterService.getShelterForDog(forDog,finalPage, finalSize);
        assertThat(actual).isNotNull().containsExactlyInAnyOrderElementsOf(list);
        assertThat(actual.size()).isEqualTo(limit);
    }

    @Test
    void updateMap() {
        Nursery expected = new Nursery(6L, "Какой то приют", "г. Н...", "Номер телефона прежний", true, null);
        when(shelterRepo.findById(any())).thenReturn(Optional.of(expected));
        String link = "link";
        expected.setMapLink(link);
        when(shelterRepo.save(expected)).thenReturn(expected);

        assertThat(shelterService.updateMap(1L, link)).isEqualTo(expected);
    }


    @Test
    void updateMapNegativeByEmptyLink() {
        String emptyLink = "";
        assertThatThrownBy(() -> shelterService.updateMap(1L, emptyLink)).isInstanceOf(IllegalParameterException.class);

        String  blancLink = "   ";
        assertThatThrownBy(() -> shelterService.updateMap(1L, blancLink)).isInstanceOf(IllegalParameterException.class);

        String nullLink = null;
        assertThatThrownBy(() -> shelterService.updateMap(1L, nullLink)).isInstanceOf(IllegalParameterException.class);
    }


    @Test
    void updateMapNegativeByNotValidId() {
        when(shelterRepo.findById(any())).thenThrow(ShelterNotFoundException.class);
        String link = "link";

        assertThatThrownBy(() -> shelterService.updateMap(1L, link)).isInstanceOf(ShelterNotFoundException.class);
    }
}