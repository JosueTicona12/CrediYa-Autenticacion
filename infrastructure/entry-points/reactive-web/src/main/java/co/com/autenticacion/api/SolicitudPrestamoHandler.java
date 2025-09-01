package co.com.autenticacion.api;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class SolicitudPrestamoHandler {
    public Mono<ServerResponse> registrarSolicitud(ServerRequest request) {
        return ServerResponse.ok().build();
    }
}
