package br.com.mateus.payflow.config.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.mateus.payflow.config.dto.ErrorMessageDTO;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionhandlerController {

    private MessageSource messageSource;

    public GlobalExceptionhandlerController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handlerMethodArgumentNotValidExceptioEntity(
            MethodArgumentNotValidException ex) {
        List<ErrorMessageDTO> dto = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(err -> {
            String message = messageSource.getMessage(err, LocaleContextHolder.getLocale());
            ErrorMessageDTO error = new ErrorMessageDTO(message, err.getField());
            dto.add(error);
        });

        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        List<ErrorMessageDTO> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ErrorMessageDTO(violation.getMessage(), violation.getPropertyPath().toString()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
