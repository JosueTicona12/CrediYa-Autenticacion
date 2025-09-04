package co.com.autenticacion.api;

import co.com.autenticacion.api.handler.AuthHandler;
import co.com.autenticacion.api.router.AuthRouter;
import co.com.autenticacion.jwtsigner.util.JwtUtil;
import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.usecase.login.LoginUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {AuthRouter.class, AuthHandler.class})
@ContextConfiguration(classes = {
        AuthRouter.class,
        AuthHandler.class,
        AuthRouterTest.TestBeans.class
})
class AuthRouterTest {

    @MockitoBean
    LoginUseCase loginUseCase;

    @MockitoBean
    JwtUtil jwtUtil;

    @Autowired
    WebTestClient webTestClient;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    static class TestBeans {
        @Bean
        SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                    .build();
        }
    }

    @Test
    void login_ok() {
        Usuario usuario = Usuario.builder()
                .email("user@test.com")
                .password(encoder.encode("secret"))
                .rolId(1L)
                .build();
        when(loginUseCase.findByEmail("user@test.com")).thenReturn(Mono.just(usuario));
        when(jwtUtil.generateToken("user@test.com", List.of("1"))).thenReturn("token123");

        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", "user@test.com", "password", "secret"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("token123");
    }

    @Test
    void login_invalidCredentials() {
        Usuario usuario = Usuario.builder()
                .email("user@test.com")
                .password(encoder.encode("secret"))
                .rolId(1L)
                .build();
        when(loginUseCase.findByEmail("user@test.com")).thenReturn(Mono.just(usuario));

        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", "user@test.com", "password", "wrong"))
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
