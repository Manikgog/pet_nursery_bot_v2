package ru.pet.nursery.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    private final Long id;

    public EntityNotFoundException(Long id) {
        this.id = id;
    }
    @Override
    public String getMessage(){
        return "Ресурс с id = " + id + " не найден";
    }
}
