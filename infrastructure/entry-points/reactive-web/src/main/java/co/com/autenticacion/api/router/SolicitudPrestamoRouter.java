package co.com.autenticacion.api.router;


import co.com.autenticacion.api.dto.SolicitudPrestamoRequestDTO;
import co.com.autenticacion.api.handler.SolicitudPrestamoHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class SolicitudPrestamoRouter {
    private final SolicitudPrestamoHandler handler;
    public static final  String SOLICITUDES = "/api/v1/solicitudes";

    @Bean
    @RouterOperation(
            path = SOLICITUDES,
            method = RequestMethod.POST,
            beanClass = SolicitudPrestamoHandler.class,
            beanMethod = "registrarSolicitud",
            operation = @Operation(
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = SolicitudPrestamoRequestDTO.class))
                    ),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Solicitud registrada"),
                            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
                    }
            )
    )
    public RouterFunction<ServerResponse> routerSolicitud() {
        return route(POST(SOLICITUDES), handler::registrarSolicitud);
    }
}
