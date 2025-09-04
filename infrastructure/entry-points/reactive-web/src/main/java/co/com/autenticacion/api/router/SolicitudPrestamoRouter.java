package co.com.autenticacion.api.router;


import co.com.autenticacion.api.handler.SolicitudPrestamoHandler;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class SolicitudPrestamoRouter {
    private final SolicitudPrestamoHandler handler;

    @Bean
    @RouterOperation(path = "/api/v1/solicitudes", method = RequestMethod.POST,
            beanClass = SolicitudPrestamoHandler.class, beanMethod = "registrarSolicitud")
    public RouterFunction<ServerResponse> routerSolicitud() {
        return route(POST("/api/v1/solicitudes"), handler::registrarSolicitud);
    }
}
