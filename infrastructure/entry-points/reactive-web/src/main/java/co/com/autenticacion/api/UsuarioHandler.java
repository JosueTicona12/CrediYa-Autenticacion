package co.com.autenticacion.api;

import co.com.autenticacion.api.config.ErrorResponse;
import co.com.autenticacion.api.config.SuccessResponse;
import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.usecase.usuario.UsuarioUseCase;
import exceptions.UsuarioDeleteException;
import exceptions.UsuarioNotFoundException;
import exceptions.UsuarioUpdateException;
import exceptions.UsuarioValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioHandler {

    private final UsuarioUseCase usuarioUseCase;

    public Mono<ServerResponse> listenSaveUsuario(ServerRequest request) {
        log.trace("Handler - Recibida petición de guardado para usuario");

        return request.bodyToMono(Usuario.class)
                .flatMap(usuarioUseCase::saveUser)
                .flatMap(u -> {
                    SuccessResponse response = SuccessResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .status(200)
                            .message("Usuario creado correctamente")
                            .build();
                    return ServerResponse.status(200)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                });
    }

    public Mono<ServerResponse> listenUpdateUsuario(ServerRequest request) {
        String id = request.pathVariable("id");
        log.trace("Handler - Recibida petición de actualización para usuario con id={}", id);

        return request.bodyToMono(Usuario.class)
                .flatMap(usuario -> usuarioUseCase.updateUser(usuario, Long.valueOf(id)))
                .flatMap(u -> {
                    SuccessResponse response = SuccessResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.OK.value())
                            .message("Usuario actualizado correctamente")
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .onErrorResume(UsuarioNotFoundException.class,
                        e -> buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request))
                .onErrorResume(UsuarioUpdateException.class,
                        e -> buildErrorResponse(HttpStatus.CONFLICT, e.getMessage(), request));
    }


    public Mono<ServerResponse> listenGetAllUsuarios(ServerRequest request) {
        log.trace("Handler - Recibida petición de obtener todos los usuarios");

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(usuarioUseCase.getAllUsers(), Usuario.class)
                .onErrorResume(Exception.class,
                        e -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request));
    }

    public Mono<ServerResponse> listenUsuarioById(ServerRequest request) {
        String id = request.pathVariable("id");
        log.trace("Handler - Recibida petición de obtener usuario con id={}", id);

        return usuarioUseCase.getUserById(Long.valueOf(id))
                .flatMap(usuario -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuario))
                .onErrorResume(UsuarioNotFoundException.class,
                        e -> buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request));
    }

    public Mono<ServerResponse> listenUsuarioByNumDoc(ServerRequest request) {
        String numDoc = request.pathVariable("numDocumento");
        log.trace("Handler - Recibida petición de obtener usuario con numDocumento={}", numDoc);

        return usuarioUseCase.findByNumDoc(numDoc)
                .flatMap(u -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(u))
                .onErrorResume(UsuarioNotFoundException.class,
                        e -> ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())))
                .onErrorResume(UsuarioValidationException.class,
                        e -> ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> listenDeleteUsuario(ServerRequest request) {
        String id = request.pathVariable("id");
        log.trace("Handler - Recibida petición de eliminar usuario con id={}", id);

        return usuarioUseCase.deleteUser(Long.valueOf(id))
                .then(ServerResponse.noContent().build())
                .onErrorResume(UsuarioNotFoundException.class,
                        e -> buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request))
                .onErrorResume(UsuarioDeleteException.class,
                        e -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request));
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String message, ServerRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.path())
                .build();

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }
}