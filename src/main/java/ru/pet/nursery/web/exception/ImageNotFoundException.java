package ru.pet.nursery.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageNotFoundException extends RuntimeException {
    private final String message;

    public ImageNotFoundException(String message) {
        this.message = message;
    }
    @Override
    public String getMessage(){
        return message;
    }
}
