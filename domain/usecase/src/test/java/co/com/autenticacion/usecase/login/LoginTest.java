package co.com.autenticacion.usecase.login;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import exceptions.AuthExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private Usuario usuario;

    @BeforeEach
    void init() {
        usuario = Usuario.builder()
                .id(1L)
                .email("user@test.com")
                .build();
    }

    @Test
    void findByEmail_ok() {
        when(usuarioRepository.findByEmail("user@test.com")).thenReturn(Mono.just(usuario));

        StepVerifier.create(loginUseCase.findByEmail("  User@Test.com  "))
                .expectNext(usuario)
                .verifyComplete();

        verify(usuarioRepository).findByEmail("user@test.com");
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void findByEmail_nullError() {
        StepVerifier.create(loginUseCase.findByEmail(null))
                .expectError(AuthExceptions.InvalidCredentialsException.class)
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void findByEmail_blankError() {
        StepVerifier.create(loginUseCase.findByEmail("   "))
                .expectError(AuthExceptions.InvalidCredentialsException.class)
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void findByEmail_notFoundError() {
        when(usuarioRepository.findByEmail("no@exist.com")).thenReturn(Mono.empty());

        StepVerifier.create(loginUseCase.findByEmail("no@exist.com"))
                .expectError(AuthExceptions.InvalidCredentialsException.class)
                .verify();

        verify(usuarioRepository).findByEmail("no@exist.com");
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void findByEmail_repositoryError() {
        when(usuarioRepository.findByEmail("user@test.com")).thenReturn(Mono.error(new RuntimeException("db down")));

        StepVerifier.create(loginUseCase.findByEmail("user@test.com"))
                .expectError(RuntimeException.class)
                .verify();

        verify(usuarioRepository).findByEmail("user@test.com");
        verifyNoMoreInteractions(usuarioRepository);
    }
}
