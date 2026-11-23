package com.saasplatform.common.exception;

import com.saasplatform.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice // Uygulamadaki Controller'larda oluşan hataları merkezi olarak yakalamamızı sağlar
public class GlobalExceptionHandler {

    // Şirket veya kaynak bulunamadığında
    // ExceptionHandler -> Hangi hata türünün hangi metot tarafından yakalanacağını belirler
    // ResponseEntity -> API yanıtının hem HTTP durum kodunu hem de gövdesini kontrol etmemizi sağlar.
    // HttpStatus.NOT_FOUND -> HTTP 404 durum kodudur. Aranan kayıt bulunamadığında kullanılır.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // Aynı e-posta gibi tekrar eden kayıt durumunda
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException exception) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // DTO doğrulaması başarısız olduğunda
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Gönderilen veriler geçersiz.");

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // İş kurallarına veya yetki sınırlarına aykırı işlem yapıldığında
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException exception) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
}
