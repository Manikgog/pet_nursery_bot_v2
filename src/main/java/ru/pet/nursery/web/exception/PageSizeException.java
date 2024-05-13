package ru.pet.nursery.web.exception;

public class PageSizeException extends RuntimeException {
    private final String message;

    public PageSizeException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
