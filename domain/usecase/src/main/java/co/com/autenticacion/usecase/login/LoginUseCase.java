package co.com.autenticacion.usecase.login;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.autenticacion.usecase.login.utils.LoginEnum;
import exceptions.AuthExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Log
public class LoginUseCase {
    private final UsuarioRepository usuarioRepository;
    public Mono<Usuario> findByEmail(String email) {
        log.info(LoginEnum.FIND_USER_BY_EMAIL.getMessage());
        if (email == null || email.isBlank()) {
            log.warning(LoginEnum.EMAIL_REQUIRED.getMessage());
            return Mono.error(new AuthExceptions.InvalidCredentialsException());
        }
        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .switchIfEmpty(Mono.error(new AuthExceptions.InvalidCredentialsException()))
                .doOnSuccess(u -> log.info(LoginEnum.USER_FOUND.getMessage()))
                .doOnError(e -> log.severe(LoginEnum.ERROR_FIND_BY_EMAIL.getMessage() + e.getMessage()));
    }
}
