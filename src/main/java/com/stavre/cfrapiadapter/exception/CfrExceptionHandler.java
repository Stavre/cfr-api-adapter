package com.stavre.cfrapiadapter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CfrExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CfrException.class)
    protected ResponseEntity<CfrExceptionDto> handleCfrException(CfrException ex) {
        CfrExceptionDto exceptionDto = new CfrExceptionDto(ex.getMessage(), ex.getErrors());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
}
