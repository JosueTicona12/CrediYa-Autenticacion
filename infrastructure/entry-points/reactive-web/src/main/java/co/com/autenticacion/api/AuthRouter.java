package co.com.autenticacion.api;

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

    @Bean
    @RouterOperation(path = "/api/v1/login", method = RequestMethod.POST, beanClass = AuthHandler.class, beanMethod = "login")
    public RouterFunction<ServerResponse> authRoutes() {
        return route(POST("/api/v1/login"), authHandler::login);
    }
}
