package com.vicheak.workingwithmultipartfileproject.exception;

import com.vicheak.workingwithmultipartfileproject.base.BaseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class FileErrorException {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleFileErrorEx(ResponseStatusException ex) {
        var baseError = BaseError.builder()
                .message("Something went wrong!")
                .code(7000)
                .status(false)
                .localDateTime(LocalDateTime.now())
                .errors(ex.getReason())
                .build();
        return new ResponseEntity<>(baseError, ex.getStatusCode());
    }

    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public BaseError handleFileErrorEx(MaxUploadSizeExceededException ex) {
        return BaseError.builder()
                .message("Something went wrong!")
                .code(7000)
                .status(false)
                .localDateTime(LocalDateTime.now())
                .errors(ex.getMessage())
                .build();
    }

}
