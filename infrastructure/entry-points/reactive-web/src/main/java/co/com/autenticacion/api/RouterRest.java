package co.com.autenticacion.api;

import co.com.autenticacion.api.config.UsuarioPath;
import co.com.autenticacion.model.usuario.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final UsuarioPath usuarioPath;
    private final Handler usuarioHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/usuarios", // usuarioPath.getUsuarios() se aplica en runtime
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveUsuario",
                    operation = @Operation(
                            operationId = "saveUsuario",
                            summary = "Crea un nuevo usuario",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Usuario creado",
                                    content = @Content(schema = @Schema(implementation = Usuario.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/usuarios/{id}", // usuarioPath.getUsuariosById()
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "listenUpdateUsuario",
                    operation = @Operation(
                            operationId = "updateUsuario",
                            summary = "Actualiza un usuario existente",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Usuario actualizado",
                                    content = @Content(schema = @Schema(implementation = Usuario.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/usuarios/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = Handler.class,
                    beanMethod = "listenDeleteUsuario",
                    operation = @Operation(
                            operationId = "deleteUsuario",
                            summary = "Elimina un usuario por id",
                            responses = @ApiResponse(responseCode = "204", description = "Usuario eliminado")
                    )
            ),
            @RouterOperation(
                    path = "/usuarios",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenGetAllUsuarios",
                    operation = @Operation(
                            operationId = "getAllUsuarios",
                            summary = "Obtiene todos los usuarios",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Lista de usuarios",
                                    content = @Content(schema = @Schema(implementation = Usuario.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/usuarios/{id}",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenUsuarioById",
                    operation = @Operation(
                            operationId = "getUsuarioById",
                            summary = "Obtiene un usuario por id",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = Usuario.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(usuarioPath.getUsuarios()), usuarioHandler::listenSaveUsuario)
                .andRoute(PUT(usuarioPath.getUsuariosById()), usuarioHandler::listenUpdateUsuario)
                .andRoute(DELETE(usuarioPath.getUsuariosById()), usuarioHandler::listenDeleteUsuario)
                .andRoute(GET(usuarioPath.getUsuarios()), usuarioHandler::listenGetAllUsuarios)
                .andRoute(GET(usuarioPath.getUsuariosById()), usuarioHandler::listenUsuarioById);
    }
}
