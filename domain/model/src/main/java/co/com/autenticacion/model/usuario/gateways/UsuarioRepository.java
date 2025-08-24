package co.com.autenticacion.model.usuario.gateways;

import co.com.autenticacion.model.usuario.Usuario;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> save(Usuario usuario);
    Flux<Usuario> findAll();
    Mono<Usuario> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<Usuario> findByEmail(String email);
}
