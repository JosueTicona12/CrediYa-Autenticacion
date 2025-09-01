package co.com.autenticacion.api;

import co.com.autenticacion.api.dto.SolicitudPrestamoRequest;
import co.com.autenticacion.api.helper.SolicitudPrestamoLogEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SolicitudPrestamoHandler {
    public Mono<ServerResponse> registrarSolicitud(ServerRequest request) {
        log.trace(SolicitudPrestamoLogEnum.REGISTRAR_SOLICITUD.getMessage());
        String authenticatedClientId = request.headers().firstHeader("X-Client-Id");

        return request.bodyToMono(SolicitudPrestamoRequest.class)
                .flatMap(body -> {
                    if (authenticatedClientId == null || !authenticatedClientId.equals(body.getClientId())) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                    }
                    return ServerResponse.ok().build();
                })
                .onErrorResume(e -> {
                    log.error(SolicitudPrestamoLogEnum.ERROR_REGISTRAR_SOLICITUD.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}
