package ru.pet.nursery.web.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ShelterNotFoundException extends RuntimeException {
    public final String message;

    public ShelterNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
