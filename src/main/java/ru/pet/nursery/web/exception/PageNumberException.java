package ru.pet.nursery.web.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class PageNumberException extends RuntimeException {
    private final String message;

    public PageNumberException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
