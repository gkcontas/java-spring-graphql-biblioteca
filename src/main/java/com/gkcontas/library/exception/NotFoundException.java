package com.gkcontas.library.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, Object id) {
        super("%s not found with id %s".formatted(resource, id));
    }
}
