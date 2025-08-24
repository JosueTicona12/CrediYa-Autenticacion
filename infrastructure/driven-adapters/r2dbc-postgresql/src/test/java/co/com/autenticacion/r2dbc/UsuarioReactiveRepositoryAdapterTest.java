package co.com.autenticacion.r2dbc;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.r2dbc.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioReactiveRepositoryAdapterTest {
    @InjectMocks
    UsuarioReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UsuarioReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final UsuarioEntity usuarioEntity = UsuarioEntity.builder()
            .id(1L)
            .nombres("Juan")
            .apellidos("Perez")
            .numDocumento("73657869")
            .nacimiento(LocalDate.ofEpochDay(2000-6-15))
            .direccion("Calle ejemplo 123")
            .build();

    private final Usuario usuario = Usuario.builder()
            .id(1L)
            .nombres("Juan")
            .apellidos("Perez")
            .numDocumento("73657869")
            .nacimiento(LocalDate.ofEpochDay(2000-6-15))
            .direccion("Calle ejemplo 123")
            .build();
    private final Usuario otherUsuario = Usuario.builder()
            .id(2L)
            .nombres("Juan")
            .apellidos("Perez")
            .numDocumento("73657869")
            .nacimiento(LocalDate.ofEpochDay(2000-6-15))
            .direccion("Calle ejemplo 123")
            .build();

    @Test
    void shouldFindUserById() {

        when(mapper.map(usuarioEntity, Usuario.class)).thenReturn(usuario);

        when(repository.findById(1L)).thenReturn(Mono.just(usuarioEntity));

        Mono<Usuario> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(t -> t.getId().equals(1L) && t.getNombres().equals("Juan") && t.getApellidos().equals("Perez"))
                .verifyComplete();
    }

    @Test
    void shouldFindAllTask() {
        when(mapper.map(usuarioEntity, Usuario.class)).thenReturn(usuario);
        when(repository.findAll()).thenReturn(Flux.just(usuarioEntity));

        Flux<Usuario> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void shouldSaveTask() {
        when(mapper.map(usuarioEntity, Usuario.class)).thenReturn(usuario);
        when(mapper.map(usuario, UsuarioEntity.class)).thenReturn(usuarioEntity);
        when(repository.save(usuarioEntity)).thenReturn(Mono.just(usuarioEntity));

        Mono<Usuario> result = repositoryAdapter.save(usuario);

        StepVerifier.create(result)
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void shouldDeleteUserById() {
        when(repository.deleteById(1L)).thenReturn(Mono.empty());

        Mono<Void> result = repositoryAdapter.deleteById(1L);

        StepVerifier.create(result)
                .verifyComplete();

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        Mono<Usuario> result = repositoryAdapter.findById(99L);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
