package co.com.autenticacion.api.router;

import co.com.autenticacion.api.config.LoginPath;
import co.com.autenticacion.api.handler.AuthHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class AuthRouter {
    private final AuthHandler authHandler;
    private final LoginPath loginPath;
    public static final String LOGIN = "/api/v1/login";

    @Bean
    @RouterOperation(
            path = LOGIN,
            method = RequestMethod.POST,
            beanClass = AuthHandler.class,
            beanMethod = "login",
            operation = @Operation(
                    operationId = "login",
                    summary = "Genera un token JWT",
                    requestBody = @RequestBody(
                            required = true,
                            description = "Credenciales de usuario",
                            content = @Content(schema = @Schema(implementation = AuthHandler.LoginRequest.class))
                    ),
                    responses = @ApiResponse(
                            responseCode = "200",
                            description = "Token generado",
                            content = @Content(schema = @Schema(implementation = AuthHandler.TokenResponse.class))
                    ),
                    security = {}
            )
    )
    public RouterFunction<ServerResponse> authRoutes() {
        return route(POST(LOGIN), authHandler::login);
    }
}
