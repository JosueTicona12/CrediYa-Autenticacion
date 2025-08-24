package co.com.autenticacion.usecase.usuario;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioUseCase {
    private final UsuarioRepository usuarioRepository;

    public Mono<Usuario> saveUser(Usuario usuario) {return usuarioRepository.save(usuario); }

    public Mono<Usuario> updateUser(Usuario usuario, String id) {
        return usuarioRepository.findById(id) // buscar por id
                .flatMap(existe -> {
                    Usuario actualizado = new Usuario(
                            existe.getId(),
                            usuario.getNombre(),
                            usuario.getApellido(),
                            usuario.getEdad(),
                            usuario.getTipoId(),
                            usuario.getNumeroId()
                    );
                    return usuarioRepository.save(actualizado); // guardar cambios
                });
    }

    public Flux<Usuario> getAllUsers() { return usuarioRepository.findAll(); }

    public Mono<Usuario> getUserById(String id) {
        return usuarioRepository.findById(id); }

    public Mono<Void> deleteUser(String id) { return usuarioRepository.deleteById(id); }
}
