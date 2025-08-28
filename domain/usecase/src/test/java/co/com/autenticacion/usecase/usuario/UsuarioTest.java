package co.com.autenticacion.usecase.usuario;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import exceptions.UsuarioDeleteException;
import exceptions.UsuarioException;
import exceptions.UsuarioNotFoundException;
import exceptions.UsuarioValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioUseCase useCase;

    private Usuario usuarioBase;

    @BeforeEach
    void init() {
        usuarioBase = Usuario.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .numDocumento("73657869")
                .nacimiento(LocalDate.of(1995, 6, 15))
                .direccion("Calle 123")
                .telefono("999999999")
                .email("Juan@Test.com ") // para validar normalización
                .salario(3500)
                .rolId(1L)
                .activo(1L)
                .build();
    }

    // -------- saveUser --------

    @Test
    void saveUser_ok() {
        // email normalizado y documento trim
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Mono.empty());
        when(usuarioRepository.findByNumDocumento("73657869")).thenReturn(Mono.empty());
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(inv -> Mono.just((Usuario) inv.getArgument(0)));

        StepVerifier.create(useCase.saveUser(usuarioBase))
                .expectNextMatches(u ->
                        u.getEmail().equals("juan@test.com") && // lower + trim
                                u.getNumDocumento().equals("73657869") &&
                                u.getActivo() == 1L)
                .verifyComplete();

        verify(usuarioRepository).findByEmail("juan@test.com");
        verify(usuarioRepository).findByNumDocumento("73657869");
        verify(usuarioRepository).save(any(Usuario.class));
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void saveUser_errorUsuarioNull() {
        StepVerifier.create(useCase.saveUser(null))
                .expectError(UsuarioValidationException.class)
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void saveUser_errorFaltanCampos() {
        Usuario u = Usuario.builder().build();

        StepVerifier.create(useCase.saveUser(u))
                .expectError(UsuarioValidationException.class)
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void saveUser_errorSalarioInvalido() {
        Usuario u = usuarioBase.toBuilder().salario(0).build();

        StepVerifier.create(useCase.saveUser(u))
                .expectError(UsuarioValidationException.class)
                .verify();

        verifyNoInteractions(usuarioRepository);
    }


    @Test
    void updateUser_ok() {
        Usuario cambios = usuarioBase.toBuilder()
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .email("nuevo@test.com")
                .salario(5000)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Mono.just(usuarioBase));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(inv -> Mono.just((Usuario) inv.getArgument(0)));

        StepVerifier.create(useCase.updateUser(cambios, 1L))
                .expectNextMatches(u ->
                        u.getNombres().equals("Juan Carlos") &&
                                u.getApellidos().equals("Pérez Gómez") &&
                                u.getEmail().equals("nuevo@test.com") &&
                                u.getSalario() == 5000)
                .verifyComplete();

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void updateUser_errorIdNull() {
        StepVerifier.create(useCase.updateUser(usuarioBase, null))
                .expectError(UsuarioValidationException.class)
                .verify();
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void updateUser_errorUsuarioNull() {
        StepVerifier.create(useCase.updateUser(null, 1L))
                .expectError(UsuarioValidationException.class)
                .verify();
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void updateUser_errorNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateUser(usuarioBase, 99L))
                .expectError(UsuarioNotFoundException.class)
                .verify();

        verify(usuarioRepository).findById(99L);
        verifyNoMoreInteractions(usuarioRepository);
    }



    @Test
    void getAllUsers_ok() {
        when(usuarioRepository.findAll()).thenReturn(Flux.just(usuarioBase));

        StepVerifier.create(useCase.getAllUsers())
                .expectNext(usuarioBase)
                .verifyComplete();

        verify(usuarioRepository).findAll();
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void getAllUsers_vacioError() {
        when(usuarioRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(useCase.getAllUsers())
                .expectError(UsuarioException.class)
                .verify();

        verify(usuarioRepository).findAll();
        verifyNoMoreInteractions(usuarioRepository);
    }

    // -------- getUserById --------

    @Test
    void getUserById_ok() {
        when(usuarioRepository.findById(1L)).thenReturn(Mono.just(usuarioBase));

        StepVerifier.create(useCase.getUserById(1L))
                .expectNext(usuarioBase)
                .verifyComplete();

        verify(usuarioRepository).findById(1L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void getUserById_errorIdNull() {
        StepVerifier.create(useCase.getUserById(null))
                .expectError(UsuarioValidationException.class)
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void getUserById_noExiste() {
        when(usuarioRepository.findById(77L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getUserById(77L))
                .expectError(UsuarioNotFoundException.class)
                .verify();

        verify(usuarioRepository).findById(77L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    // -------- deleteUser (soft delete) --------

    @Test
    void deleteUser_okSoftDelete() {
        when(usuarioRepository.findById(1L)).thenReturn(Mono.just(usuarioBase));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(inv -> Mono.just((Usuario) inv.getArgument(0)));

        StepVerifier.create(useCase.deleteUser(1L))
                .verifyComplete();

        // Capturar el usuario guardado para verificar activo=0
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario guardado = captor.getValue();
        // activo debe quedar 0
        org.assertj.core.api.Assertions.assertThat(guardado.getActivo()).isEqualTo(0L);

        verify(usuarioRepository).findById(1L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void deleteUser_errorIdNull() {
        StepVerifier.create(useCase.deleteUser(null))
                .expectError(UsuarioValidationException.class)
                .verify();

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void deleteUser_noExiste() {
        when(usuarioRepository.findById(55L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteUser(55L))
                .expectError(UsuarioDeleteException.class) // <- cambia la expectativa
                .verify();

        verify(usuarioRepository).findById(55L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    // -------- findByEmail --------

    @Test
    void findByEmail_ok() {
        when(usuarioRepository.findByEmail("a@b.com")).thenReturn(Mono.just(usuarioBase));

        StepVerifier.create(useCase.findByEmail("a@b.com"))
                .expectNext(usuarioBase)
                .verifyComplete();

        verify(usuarioRepository).findByEmail("a@b.com");
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void findByEmail_vacioError() {
        when(usuarioRepository.findByEmail("x@y.com")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findByEmail("x@y.com"))
                .expectError(UsuarioException.class)
                .verify();

        verify(usuarioRepository).findByEmail("x@y.com");
        verifyNoMoreInteractions(usuarioRepository);
    }

    // -------- findByNumDoc --------

    @Test
    void findByNumDoc_okActivo() {
        when(usuarioRepository.findByNumDocumento("73657869"))
                .thenReturn(Mono.just(usuarioBase)); // activo=1

        StepVerifier.create(useCase.findByNumDoc("73657869"))
                .expectNext(usuarioBase)
                .verifyComplete();

        verify(usuarioRepository).findByNumDocumento("73657869");
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void findByNumDoc_inactivoError() {
        Usuario inactivo = usuarioBase.toBuilder().activo(0L).build();
        when(usuarioRepository.findByNumDocumento("111"))
                .thenReturn(Mono.just(inactivo));

        StepVerifier.create(useCase.findByNumDoc("111"))
                .expectError(UsuarioException.class)
                .verify();

        verify(usuarioRepository).findByNumDocumento("111");
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void findByNumDoc_noExiste() {
        when(usuarioRepository.findByNumDocumento("222")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findByNumDoc("222"))
                .expectError(UsuarioException.class)
                .verify();

        verify(usuarioRepository).findByNumDocumento("222");
        verifyNoMoreInteractions(usuarioRepository);
    }
}
