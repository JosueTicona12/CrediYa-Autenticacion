package co.com.autenticacion.api.config;

import exceptions.UsuarioDeleteException;
import exceptions.UsuarioNotFoundException;
import exceptions.UsuarioUpdateException;
import exceptions.UsuarioValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private Mono<ResponseEntity<ErrorResponse>> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            ServerHttpRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getURI().getPath())
                .build();

        return Mono.just(ResponseEntity.status(status).body(error));
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(
            UsuarioNotFoundException ex,
            ServerHttpRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UsuarioValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(
            UsuarioValidationException ex,
            ServerHttpRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UsuarioUpdateException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUpdate(
            UsuarioUpdateException ex,
            ServerHttpRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(UsuarioDeleteException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDelete(
            UsuarioDeleteException ex,
            ServerHttpRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class) // fallback para errores no controlados
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(
            Exception ex,
            ServerHttpRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
