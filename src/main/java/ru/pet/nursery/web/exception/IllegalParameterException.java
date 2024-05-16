package ru.pet.nursery.web.exception;

public class IllegalParameterException extends RuntimeException {
    private final String message;

    public IllegalParameterException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
