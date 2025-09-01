package co.com.autenticacion.api.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SolicitudPrestamoLogEnum {
    REGISTRAR_SOLICITUD("Handler - Recibida petición de registrar solicitud de préstamo"),
    ERROR_REGISTRAR_SOLICITUD("Error registrando solicitud de préstamo: ");

    private final String message;
}
