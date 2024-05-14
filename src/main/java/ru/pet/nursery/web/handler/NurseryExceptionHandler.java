package ru.pet.nursery.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.pet.nursery.web.exception.*;

import java.io.FileNotFoundException;

@RestControllerAdvice
public class NurseryExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException(ImageNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ShelterNullException.class)
    public ResponseEntity<String> handleShelterNullException(ShelterNullException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ShelterNotFoundException.class)
    public ResponseEntity<String> handleShelterNotFoundException(ShelterNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
