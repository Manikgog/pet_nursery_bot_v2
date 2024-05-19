package ru.pet.nursery.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReportIsExistException extends RuntimeException {
    private final String message;

    public ReportIsExistException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
