package co.com.autenticacion.usecase.usuario;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@RequiredArgsConstructor
public class UsuarioUseCase {
    private final UsuarioRepository usuarioRepository;

    public Mono<Usuario> saveUser(Usuario usuario) {
        log.info("UseCase - Guardando usuario");

        if (usuario == null) {
            return Mono.error(new UsuarioValidationException("El usuario no puede ser nulo"));
        }
        if (usuario.getNombres() == null || usuario.getNombres().isBlank()) {
            return Mono.error(new UsuarioValidationException("El nombre es obligatorio"));
        }
        if (usuario.getApellidos() == null || usuario.getApellidos().isBlank()) {
            return Mono.error(new UsuarioValidationException("El apellido es obligatorio"));
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return Mono.error(new UsuarioValidationException("El correo electronico es obligatorio"));
        }
        if (usuario.getSalario() == null || (usuario.getSalario() <= 0 || usuario.getSalario() > 15000000)) {
            return Mono.error(new UsuarioValidationException("El salario base esta vacio o fuera de rango numerico"));
        }
        if (usuario.getNumDocumento() == null || usuario.getNumDocumento().isBlank()) {
            return Mono.error(new UsuarioValidationException("El número de documento es obligatorio"));
        }
        usuario.setActivo(1L);
        final String email = usuario.getEmail().trim().toLowerCase();
        final String numDocumento = usuario.getNumDocumento().trim();
        usuario.setEmail(email);
        usuario.setNumDocumento(numDocumento);
        usuario.setActivo(1L);
        return usuarioRepository.findByEmail(email)
                .flatMap(existing -> Mono.<Usuario>error(
                        new UsuarioException("Ya existe un usuario registrado con el email: " + email)
                ))
                .switchIfEmpty(
                        usuarioRepository.findByNumDocumento(numDocumento)
                                .flatMap(existing -> Mono.<Usuario>error(
                                        new UsuarioException("Ya existe un usuario registrado con el documento: " + numDocumento)
                                ))
                                .switchIfEmpty(
                                        // 3) Si tampoco existe el documento → guardar
                                        usuarioRepository.save(usuario)
                                )
                )
                .doOnSuccess(u -> log.info("Usuario guardado con éxito"))
                .doOnError(e -> log.severe("Error guardando usuario: " + e.getMessage()));
    }

    public Mono<Usuario> updateUser(Usuario usuario, Long id) {

        log.info("UseCase - Actualizando usuario con id {}");

        if (id == null) {
            return Mono.error(new UsuarioValidationException("El id no puede ser nulo"));
        }
        if (usuario == null) {
            return Mono.error(new UsuarioValidationException("El usuario no puede ser nulo"));
        }

        return usuarioRepository.findById(id)
                .switchIfEmpty(Mono.error(new UsuarioNotFoundException(id)))
                .flatMap(existing -> {
                    existing.setNombres(usuario.getNombres());
                    existing.setApellidos(usuario.getApellidos());
                    existing.setDireccion(usuario.getDireccion());
                    existing.setNacimiento(usuario.getNacimiento());
                    existing.setEmail(usuario.getEmail());
                    existing.setTelefono(usuario.getTelefono());
                    existing.setSalario(usuario.getSalario());
                    return usuarioRepository.save(existing);
                })
                .switchIfEmpty(Mono.error(new UsuarioUpdateException(id)))
                .doOnSuccess(u -> log.info("Usuario actualizado: {" + id + "}"))
                .doOnError(e -> log.severe("Error actualizando usuario con id {" + id + "}"));
    }

    public Flux<Usuario> getAllUsers() { log.info("UseCase - Buscar todos los usuarios");

        return usuarioRepository.findAll()
                .switchIfEmpty(Flux.error(new UsuarioException("No se encontraron usuarios")))
                .doOnComplete(() -> log.info("Consulta de usuarios completada"))
                .doOnError(e -> log.severe("Error consultando todos los usuarios: " + e)); }

    public Mono<Usuario> getUserById(Long id) {
        log.info("UseCase - Buscar usuario por id {}" + id);

        if (id == null) {
            return Mono.error(new UsuarioValidationException("El id no puede ser nulo"));
        }

        return usuarioRepository.findById(id)
                .switchIfEmpty(Mono.error(new UsuarioNotFoundException(id)))
                .doOnSuccess(u -> log.info("Usuario encontrado: {}" + u))
                .doOnError(e -> log.severe("Error buscando usuario con id {" + id + "}: " + e)); }

    public Mono<Void> deleteUser(Long id) {
        log.info("UseCase - Eliminando (soft delete) usuario con id {" + id + "}");

        if (id == null) {
            return Mono.error(new UsuarioValidationException("El id no puede ser nulo"));
        }

        return usuarioRepository.findById(id)
                .switchIfEmpty(Mono.error(new UsuarioNotFoundException(id)))
                .flatMap(existing -> {
                    if (existing == null) { // aunque en Reactor nunca debería llegar null aquí
                        return Mono.error(new UsuarioNotFoundException(id));
                    }
                    existing.setActivo(0L);
                    return usuarioRepository.save(existing);
                })
                .doOnSuccess(u -> log.info("Usuario marcado como inactivo con id {" + id + "}"))
                .doOnError(e -> {
                    log.severe("Error al marcar usuario como inactivo con id {" + id + "}: " + e);
                    throw new UsuarioDeleteException(id);
                })
                .then();
    }

    public Mono<Usuario> findByEmail(String email) {
        log.info("UseCase - Busqueda de Id por correo");
        return usuarioRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UsuarioException("El campo email esta vacio" + email)))
                .doOnSuccess(u -> log.info("Usuario encontrado: {" + u + "}"))
                .doOnError(e -> log.severe("Error buscando usuario con email {" + email + "}: " + e));
    }

    public Mono<Usuario> findByNumDoc(String numDocumento) {
        log.info("UseCase - Busqueda de Id por correo");
        return usuarioRepository.findByNumDocumento(numDocumento)
                .filter(u -> u.getActivo() != null && u.getActivo() == 1L)
                .switchIfEmpty(Mono.error(new UsuarioException("No existe usuario activo con documento ingersado")))
                .doOnSuccess(u -> log.info("Usuario encontrado {" + u.getId() + "}"))
                .doOnError(e -> log.severe("Error buscando por documento {" + numDocumento + "}: " + e.getMessage()));
    }
}
