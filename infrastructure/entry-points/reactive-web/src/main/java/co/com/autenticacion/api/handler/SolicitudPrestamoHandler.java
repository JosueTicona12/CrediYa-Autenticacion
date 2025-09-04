package co.com.autenticacion.api.handler;

import co.com.autenticacion.api.dto.SolicitudPrestamoRequestDTO;
import co.com.autenticacion.api.helper.SolicitudPrestamoLogEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SolicitudPrestamoHandler {
    public Mono<ServerResponse> registrarSolicitud(ServerRequest request) {
        log.trace(SolicitudPrestamoLogEnum.REGISTRAR_SOLICITUD.getMessage());

        return request.principal()
                .cast(Authentication.class)
                .flatMap(auth -> request.bodyToMono(SolicitudPrestamoRequestDTO.class)
                        .flatMap(body -> {
                            String authenticatedClientId = auth.getName();
                            boolean hasRoleCliente = auth.getAuthorities().stream()
                                    .anyMatch(a -> "3".equals(a.getAuthority()));
                            if (!hasRoleCliente) {
                                return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                            }
                            return ServerResponse.ok().build();
                        }))
                .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build())
                .onErrorResume(e -> {
                    log.error(SolicitudPrestamoLogEnum.ERROR_REGISTRAR_SOLICITUD.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}
