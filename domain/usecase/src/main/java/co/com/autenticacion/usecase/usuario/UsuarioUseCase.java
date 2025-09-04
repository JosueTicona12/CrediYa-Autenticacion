package co.com.autenticacion.usecase.usuario;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static co.com.autenticacion.usecase.usuario.utils.UsuarioLogEnum.*;


@Log
@RequiredArgsConstructor
public class UsuarioUseCase {
    private final UsuarioRepository usuarioRepository;


    public Mono<Usuario> saveUser(Usuario usuario) {
        log.info(SAVE_USER.getMessage());

        if (usuario == null) {
            log.warning(USER_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(USER_REQUIRED.getMessage()));
        }
        if (usuario.getNombres() == null || usuario.getNombres().isBlank()) {
            log.warning(NAME_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(NAME_REQUIRED.getMessage()));
        }
        if (usuario.getApellidos() == null || usuario.getApellidos().isBlank()) {
            log.warning(LASTNAME_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(LASTNAME_REQUIRED.getMessage()));
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            log.warning(EMAIL_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(EMAIL_REQUIRED.getMessage()));
        }
        if (usuario.getSalario() == null || (usuario.getSalario() <= 0 || usuario.getSalario() > 15000000)) {
            log.warning(SALARY_INVALID.getMessage());
            return Mono.error(new UsuarioValidationException(SALARY_INVALID.getMessage()));
        }
        if (usuario.getNumDocumento() == null || usuario.getNumDocumento().isBlank()) {
            log.warning(NUMDOC_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(NUMDOC_REQUIRED.getMessage()));
        }
        final String email = usuario.getEmail().trim().toLowerCase();
        final String numDocumento = usuario.getNumDocumento().trim();
        usuario.setEmail(email);
        usuario.setNumDocumento(numDocumento);
        usuario.setActivo(1L);
        return usuarioRepository.findByEmail(email)
                .flatMap(existing -> Mono.<Usuario>error(
                        new UsuarioException("Ya existe un usuario registrado con el email: " + email)
                ))
                .switchIfEmpty(Mono.defer(() ->
                                usuarioRepository.findByNumDocumento(numDocumento)
                                    .flatMap(existing -> Mono.<Usuario>error(
                                        new UsuarioException("Ya existe un usuario registrado con el documento: " + numDocumento)
                                ))
                                        .switchIfEmpty(usuarioRepository.save(usuario))
                ))
                .doOnSuccess(u -> log.info(SAVE_USER_SUCCESS.getMessage()))
                .doOnError(e -> log.severe(ERROR_SAVE_USER.getMessage() + e.getMessage()));
    }

    public Mono<Usuario> updateUser(Usuario usuario, Long id) {

        log.info(UPDATE_USER.getMessage() + id);


        if (id == null) {
            log.warning(ID_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(ID_REQUIRED.getMessage()));
        }
        if (usuario == null) {
            log.warning(USER_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(USER_REQUIRED.getMessage()));
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
                .doOnSuccess(u -> log.info(UPDATE_USER_SUCCESS.getMessage() + id))
                .doOnError(e -> log.severe(ERROR_UPDATE_USER.getMessage() + id));
    }

    public Flux<Usuario> getAllUsers() {
        log.info(GET_ALL_USERS.getMessage());

        return usuarioRepository.findAll()
                .switchIfEmpty(Flux.error(new UsuarioException("No se encontraron usuarios")))
                .doOnComplete(() -> log.info(GET_ALL_USERS_COMPLETE.getMessage()))
                .doOnError(e -> log.severe(ERROR_GET_ALL_USERS.getMessage() + e));
    }

    public Mono<Usuario> getUserById(Long id) {
        log.info(GET_USER_BY_ID.getMessage() + id);

        if (id == null) {
            log.warning(ID_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(ID_REQUIRED.getMessage()));
        }

        return usuarioRepository.findById(id)
                .switchIfEmpty(Mono.error(new UsuarioNotFoundException(id)))
                .doOnSuccess(u -> log.info(USER_FOUND.getMessage() + u))
                .doOnError(e -> log.severe(ERROR_GET_USER_BY_ID.getMessage() + id + ": " + e));
    }

    public Mono<Void> deleteUser(Long id) {
        log.info(DELETE_USER.getMessage() + id);
        if (id == null) {
            log.warning(ID_REQUIRED.getMessage());
            return Mono.error(new UsuarioValidationException(ID_REQUIRED.getMessage()));
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
                .doOnSuccess(u -> log.info(DELETE_USER_SUCCESS.getMessage() + id))
                .doOnError(e -> {
                    log.severe(ERROR_DELETE_USER.getMessage() + id + ": " + e);
                    throw new UsuarioDeleteException(id);
                })
                .then();
    }

    public Mono<Usuario> findByEmail(String email) {
        log.info(FIND_BY_EMAIL.getMessage());
        return usuarioRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UsuarioException("El campo email esta vacio" + email)))
                .doOnSuccess(u -> log.info(USER_FOUND.getMessage() + u))
                .doOnError(e -> log.severe(ERROR_FIND_BY_EMAIL.getMessage() + email + ": " + e));
    }

    public Mono<Usuario> findByNumDoc(String numDocumento) {
        log.info(FIND_BY_NUMDOC.getMessage());
        return usuarioRepository.findByNumDocumento(numDocumento)
                .filter(u -> u.getActivo() != null && u.getActivo() == 1L)
                .switchIfEmpty(Mono.error(new UsuarioException("No existe usuario activo con documento ingersado")))
                .doOnSuccess(u -> log.info(USER_FOUND.getMessage() + u.getId()))
                .doOnError(e -> log.severe(ERROR_FIND_BY_NUMDOC.getMessage() + numDocumento + ": " + e.getMessage()));
    }
}
