package ru.pet.nursery.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ShelterNullException extends RuntimeException {
    public ShelterNullException(String message) {
        super(message);
    }
}
