package co.com.autenticacion.api;

import co.com.autenticacion.api.config.UsuarioPath;
import co.com.autenticacion.api.handler.UsuarioHandler;
import co.com.autenticacion.api.router.RouterRest;
import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.usecase.usuario.UsuarioUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;


@WebFluxTest(controllers = {RouterRest.class, UsuarioHandler.class})
@ContextConfiguration(classes = {
        RouterRest.class,
        UsuarioHandler.class,
        RouterRestTest.TestBeans.class
})
class RouterRestTest {

    @MockitoBean
    UsuarioUseCase usuarioUseCase; // lo requiere el Handler

    @Autowired
    WebTestClient webTestClient;

    static class TestBeans {
        @Bean
        UsuarioPath usuarioPath() {
            var p = new UsuarioPath();
            p.setUsuarios("/api/v1/usuarios");
            p.setUsuariosById("/api/v1/usuarios/{id}");
            return p;
        }
        @Bean
        SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                    .build();
        }
    }

    static final Usuario U1 = Usuario.builder()
            .id(1L).nombres("Juan").apellidos("Perez").numDocumento("73657869")
            .nacimiento(LocalDate.of(1995,6,15)).direccion("Calle 123")
            .telefono("999999999").email("juan@test.com").salario(3500)
            .rolId(1L).activo(1L).build();

    static final Usuario U2 = Usuario.builder()
            .id(2L).nombres("Marta").apellidos("Diaz").numDocumento("74561234")
            .nacimiento(LocalDate.of(1993,3,10)).direccion("Av 456")
            .telefono("911111111").email("marta@test.com").salario(4200)
            .rolId(1L).activo(1L).build();


    @Test
    void getAllUsuarios_ok() {
        when(usuarioUseCase.getAllUsers()).thenReturn(Flux.just(U1, U2));

        webTestClient.get()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Usuario.class)
                .hasSize(2)
                .value(list -> Assertions.assertThat(list.get(0).getId()).isEqualTo(1L));
    }

    @Test
    void getUsuarioById_ok() {
        when(usuarioUseCase.getUserById(1L)).thenReturn(Mono.just(U1));

        webTestClient.get()
                .uri("/api/v1/usuarios/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .value(u -> Assertions.assertThat(u.getEmail()).isEqualTo("juan@test.com"));
    }

    @Test
    void postSaveUsuario_ok() {
        when(usuarioUseCase.saveUser(ArgumentMatchers.any(Usuario.class))).thenReturn(Mono.just(U1));

        webTestClient.mutateWith(csrf())
                .mutateWith(mockAuthentication(new TestingAuthenticationToken("admin", "pass", "1")))
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(U1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(200) // o el que uses en tu SuccessResponse
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    void putUpdateUsuario_ok() {
        Usuario cambios = U1.toBuilder().apellidos("Pérez Gómez").salario(5000).build();
        when(usuarioUseCase.updateUser(ArgumentMatchers.any(Usuario.class), ArgumentMatchers.eq(1L)))
                .thenReturn(Mono.just(cambios));

        webTestClient.mutateWith(csrf()).put()
                .uri("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cambios)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.message").value(msg -> Assertions.assertThat(msg).asString().isNotBlank());
    }

    @Test
    void deleteUsuario_noContent() {
        when(usuarioUseCase.deleteUser(1L)).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete()
                .uri("/api/v1/usuarios/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
