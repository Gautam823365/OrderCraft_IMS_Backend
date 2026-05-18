package com.boot.ordercraft.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class NoResourceAvailableException extends RuntimeException {
    public NoResourceAvailableException(String message) {
        super(message);
    }
}
