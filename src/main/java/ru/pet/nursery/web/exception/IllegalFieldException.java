package ru.pet.nursery.web.exception;

public class IllegalFieldException extends RuntimeException {
    private final String message;

    public IllegalFieldException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
