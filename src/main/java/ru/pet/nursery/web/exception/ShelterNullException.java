package ru.pet.nursery.web.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ShelterNullException extends RuntimeException {
    public ShelterNullException(String message) {
        super(message);
    }
}
