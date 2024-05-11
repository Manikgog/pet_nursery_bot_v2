package ru.pet.nursery.enumerations;

public enum AnimalType {
    CAT("cat"),DOG("dog");

    private final String type;

    AnimalType(String type) {
        this.type = type;
    }

    public String getAnimalType() {
        return type;
    }
}
