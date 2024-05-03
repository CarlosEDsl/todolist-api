package com.carloseduardo.treino.services.exceptions;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DataBindingViolationException extends DataIntegrityViolationException {

    public DataBindingViolationException(String message) {
        super(message);
    }

}
