package co.com.autenticacion.api;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {
    //private  final UseCase useCase;
//private  final UseCase2 useCase2;
    private final UsuarioUseCase usuarioUseCase;

    public Mono<ServerResponse> listenSaveUsuario(ServerRequest serverRequest) {
        log.trace("Handler - Recibida petición de guardado para usuario");
        return serverRequest.bodyToMono(Usuario.class)
                .flatMap(usuarioUseCase::saveUser)
                .flatMap(savedUsuario -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUsuario));
    }

    public Mono<ServerResponse> listenUpdateUsuario(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.trace("Handler - Recibida petición de actualización para usuario con id={}", id);
        return serverRequest.bodyToMono(Usuario.class)
                .flatMap(usuario -> usuarioUseCase.updateUser(usuario,id))
                .flatMap(updatedUsuario -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedUsuario))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> listenGetAllUsuarios(ServerRequest serverRequest) {
        log.trace("Handler - Recibida petición de obtener todos los usuarios");
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(usuarioUseCase.getAllUsers(), Usuario.class);
    }

    public Mono<ServerResponse> listenUsuarioById(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");
        log.trace("Handler - Recibida petición de obtener para usuario con id={}", id);

        return usuarioUseCase.getUserById(id)
                .flatMap(usuario -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuario))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listenDeleteUsuario(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.trace("Handler - Recibida petición de eliminar usuario con id={}", id);
        return usuarioUseCase.deleteUser(id)
                .then(ServerResponse.noContent().build());
    }
}