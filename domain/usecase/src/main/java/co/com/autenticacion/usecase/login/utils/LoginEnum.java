package co.com.autenticacion.usecase.login.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum LoginEnum {
    LOGIN_REQUEST("AuthHandler - Procesando solicitud de login"),
    FIND_USER_BY_EMAIL("LoginUseCase - Buscando usuario por email"),
    USER_FOUND("LoginUseCase - Usuario encontrado"),
    EMAIL_REQUIRED("LoginUseCase - El email es obligatorio"),
    INVALID_CREDENTIALS("AuthHandler - Credenciales inválidas"),
    TOKEN_GENERATED("AuthHandler - Token generado correctamente"),
    ERROR_LOGIN("AuthHandler - Error procesando login"),
    ERROR_FIND_BY_EMAIL("LoginUseCase - Error buscando usuario: ");

    private final String message;

}
