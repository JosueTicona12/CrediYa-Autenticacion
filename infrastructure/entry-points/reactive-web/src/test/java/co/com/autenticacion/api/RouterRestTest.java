package co.com.autenticacion.api;

import co.com.autenticacion.api.config.UsuarioPath;
import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.usecase.usuario.UsuarioUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@EnableConfigurationProperties(UsuarioPath.class)
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;

    private final String baseUri = "/api/v1/usuarios";

    private final String usuarios = "/api/v1/usuarios";
    private final String usuariosById = "/api/v1/usuarios";

    private final Usuario usuario1 = Usuario.builder()
            .id(1L)
            .nombres("Juan")
            .apellidos("Perez")
            .numDocumento("73657869")
            .nacimiento(LocalDate.ofEpochDay(2000-6-15))
            .direccion("Calle ejemplo 123")
            .telefono("123456789")
            .email("correo123@hotmail.com")
            .salario(3500)
            .rolId(1L)
            .build();

    private final Usuario usuario2 = Usuario.builder()
            .id(2L)
            .nombres("Marta")
            .apellidos("Diaz")
            .numDocumento("73657869")
            .nacimiento(LocalDate.ofEpochDay(2000-6-15))
            .direccion("Calle ejemplo 1234")
            .telefono("123456789")
            .email("correo1234@hotmail.com")
            .salario(3500)
            .rolId(1L)
            .build();

    @Autowired
    private UsuarioPath usuarioPath;

    @Test
    void testGetAllUsuarios() {
        when(usuarioUseCase.getAllUsers()).thenReturn(Flux.just(usuario1, usuario2));

        webTestClient.get()
                .uri(baseUri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Usuario.class)
                .hasSize(2)
                .value(list -> {
                    Assertions.assertThat(list.get(0).getId()).isEqualTo(1L);
                    Assertions.assertThat(list.get(1).getId()).isEqualTo(2L);
                });
    }

    @Test
    void testGetUsuarioById() {
        when(usuarioUseCase.getUserById(1L)).thenReturn(Mono.just(usuario1));

        webTestClient.get()
                .uri(baseUri + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .value(user -> Assertions.assertThat(user.getId()).isEqualTo(1L));
    }

    @Test
    void testSaveUsuario() {
        when(usuarioUseCase.saveUser(any(Usuario.class))).thenReturn(Mono.just(usuario1));

        webTestClient.post()
                .uri(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usuario1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .value(user -> Assertions.assertThat(user.getEmail()).isEqualTo("correo123@hotmail.com"));
    }

    @Test
    void testUpdateUsuario() {
        Usuario usuarioActualizado = usuario1.toBuilder().apellidos("Maldonado").build();
        when(usuarioUseCase.updateUser(any(Usuario.class), anyLong())).thenReturn(Mono.just(usuarioActualizado));

        webTestClient.put()
                .uri(baseUri + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usuarioActualizado)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .value(user -> Assertions.assertThat(user.getApellidos()).isEqualTo("Maldonado"));
    }

    @Test
    void testDeleteUsuario() {
        when(usuarioUseCase.deleteUser(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(baseUri + "/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
