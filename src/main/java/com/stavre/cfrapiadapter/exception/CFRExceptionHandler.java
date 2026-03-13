package com.stavre.cfrapiadapter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CFRExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CFRException.class)
    protected ResponseEntity<CFRExceptionDto> handleCFRException(CFRException ex) {
        CFRExceptionDto exceptionDto = new CFRExceptionDto(ex.getMessage(), ex.getErrors());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
}
