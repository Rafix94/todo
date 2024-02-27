package com.recruitment.useragent.exception;

import com.recruitment.useragent.dto.ErrorDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorDto> handleGlobalException(Exception exception,
//                                                          WebRequest webRequest) {
//        ErrorDto errorDto = ErrorDto.builder()
//                .invokedPath(webRequest.getDescription(false))
//                .message(exception.getMessage())
//                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {

        List<ObjectError> validationErrorList =
                methodArgumentNotValidException.getBindingResult().getAllErrors();

        HashMap<String, String> validationErrorsMap =
                validationErrorList.stream().collect(
                        Collectors.toMap(
                                objectError -> ((FieldError) objectError).getField(),
                                DefaultMessageSourceResolvable::getDefaultMessage,
                                (existingValue, newValue) -> existingValue,
                                HashMap::new)
                );

        return new ResponseEntity<>(validationErrorsMap, HttpStatus.BAD_REQUEST);
    }
}
