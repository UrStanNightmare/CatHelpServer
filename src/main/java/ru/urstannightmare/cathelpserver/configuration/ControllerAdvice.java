package ru.urstannightmare.cathelpserver.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.urstannightmare.cathelpserver.task.TaskNotFoundException;
import ru.urstannightmare.cathelpserver.task.dto.ErrorResponse;

@RestControllerAdvice
public class ControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler(value = {
            DataRetrievalFailureException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidRequestArgumentException(
            Exception ex,
            WebRequest request) {

        log.error("Handled invalid request. {}", ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(
            value = TaskNotFoundException.class
    )
    public ResponseEntity<ErrorResponse> dataRetrievalFailureException(
            TaskNotFoundException ex,
            WebRequest  request
    ){
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }


    @ExceptionHandler(
            value = MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(
            Exception ex,
            WebRequest request) {
        var e = ((MethodArgumentNotValidException) ex);

        var errors = e.getDetailMessageArguments();
        StringBuilder builder = new StringBuilder();

        for (Object error : errors) {
            builder.append(error).append(" ");
        }

        log.error("Handled validation error. {}", ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), builder.toString()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(
            value = DuplicateKeyException.class
    )
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(
            Exception ex,
            WebRequest request) {

        log.error("Handled duplicated key. {}", ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Key must be unique!"),
                HttpStatus.BAD_REQUEST
        );
    }
}
