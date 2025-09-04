package co.com.autenticacion.api.config;

import co.com.autenticacion.api.handler.UsuarioHandler;
import co.com.autenticacion.api.router.RouterRest;
import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, UsuarioHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class, ConfigTest.TestBeans.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase; // requerido por Handler

    static final Usuario U1 = Usuario.builder()
            .id(1L).nombres("Juan").apellidos("Perez")
            .numDocumento("73657869").nacimiento(LocalDate.of(1995,6,15))
            .direccion("Calle 123").telefono("999999999")
            .email("juan@test.com").salario(3500).rolId(1L).activo(1L)
            .build();

    @Test
    void corsConfigurationShouldAllowOriginsAndSecurityHeaders() {
        when(usuarioUseCase.getAllUsers()).thenReturn(Flux.just(U1));

        webTestClient.get()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                // Encabezados de SecurityHeadersConfig
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    static class TestBeans {
        @Bean
        public UsuarioPath usuarioPath() {
            var p = new UsuarioPath();
            p.setUsuarios("/api/v1/usuarios");
            p.setUsuariosById("/api/v1/usuarios/{id}");
            return p;
        }
    }

}