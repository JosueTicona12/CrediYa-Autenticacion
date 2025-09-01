package co.com.autenticacion.usecase.login;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor

public class LoginUseCase {
    private final UsuarioRepository usuarioRepository;
    public Mono<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
