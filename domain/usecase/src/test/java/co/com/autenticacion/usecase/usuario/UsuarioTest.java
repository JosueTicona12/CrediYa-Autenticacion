package co.com.autenticacion.usecase.usuario;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import exceptions.UsuarioException;
import exceptions.UsuarioNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombres("Juan");
        usuario.setApellidos("Perez");
        usuario.setEmail("juan@test.com");
        usuario.setSalario(2000);
        usuario.setNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setActivo(1L);
    }

    @Test
    void saveUser_success() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Mono.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.saveUser(usuario))
                .expectNextMatches(u -> u.getEmail().equals("juan@test.com"))
                .verifyComplete();
    }

    @Test
    void updateUser_success() {
        when(usuarioRepository.findById(1L)).thenReturn(Mono.just(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.updateUser(usuario, 1L))
                .expectNextMatches(u -> u.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void updateUser_notFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.updateUser(usuario, 1L))
                .expectError(UsuarioNotFoundException.class)
                .verify();
    }

    @Test
    void getAllUsers_success() {
        when(usuarioRepository.findAll()).thenReturn(Flux.just(usuario));

        StepVerifier.create(usuarioUseCase.getAllUsers())
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void getAllUsers_empty() {
        when(usuarioRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(usuarioUseCase.getAllUsers())
                .expectError(UsuarioException.class)
                .verify();
    }

    @Test
    void getUserById_success() {
        when(usuarioRepository.findById(1L)).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.getUserById(1L))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void getUserById_notFound() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.getUserById(1L))
                .expectError(UsuarioNotFoundException.class)
                .verify();
    }

    @Test
    void deleteUser_success() {
        when(usuarioRepository.findById(1L)).thenReturn(Mono.just(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.deleteUser(1L))
                .verifyComplete();
    }

    @Test
    void findByEmail_success() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.findByEmail("juan@test.com"))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void findByEmail_notFound() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.findByEmail("juan@test.com"))
                .expectError(UsuarioException.class)
                .verify();
    }
}
