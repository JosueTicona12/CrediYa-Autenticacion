package co.com.autenticacion.api;

import co.com.autenticacion.api.config.UsuarioPath;
import co.com.autenticacion.model.usuario.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    private final UsuarioHandler usuarioHandler;
    public static final String USUARIOS_BY_DOCUMENTO = "/api/v1/usuarios/by-documento/{numDocumento}";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/usuarios",
                    method = RequestMethod.POST,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listenSaveUsuario",
                    operation = @Operation(
                            operationId = "saveUsuario",
                            summary = "Crea un nuevo usuario",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos del usuario a crear",
                                    content = @Content(schema = @Schema(implementation = Usuario.class))
                            ),
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Usuario creado",
                                    content = @Content(schema = @Schema(implementation = Usuario.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/usuarios/{id}",
                    method = RequestMethod.PUT,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listenUpdateUsuario",
                    operation = @Operation(
                            operationId = "updateUsuario",
                            summary = "Actualiza un usuario existente",
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "ID del usuario a actualizar",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "long")
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos actualizados del usuario",
                                    content = @Content(schema = @Schema(implementation = Usuario.class))
                            ),
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
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listenDeleteUsuario",
                    operation = @Operation(
                            operationId = "deleteUsuario",
                            summary = "Elimina un usuario por id",
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "ID del usuario a eliminar",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "long")
                                    )
                            },
                            responses = @ApiResponse(responseCode = "204", description = "Usuario eliminado")
                    )
            ),
            @RouterOperation(
                    path = "/usuarios",
                    method = RequestMethod.GET,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listenGetAllUsuarios",
                    operation = @Operation(
                            operationId = "getAllUsuarios",
                            summary = "Obtiene todos los usuarios",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Lista de usuarios",
                                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class)))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/usuarios/{id}",
                    method = RequestMethod.GET,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listenUsuarioById",
                    operation = @Operation(
                            operationId = "getUsuarioById",
                            summary = "Obtiene un usuario por id",
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "ID del usuario a consultar",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "long")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = Usuario.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = USUARIOS_BY_DOCUMENTO,
                    method = RequestMethod.GET,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listenUsuarioByDocumento",
                    operation = @Operation(
                            operationId = "getUsuarioByDocumento",
                            summary = "Obtiene un usuario activo por número de documento",
                            parameters = @Parameter(
                                    name = "numDocumento",
                                    in = ParameterIn.PATH,
                                    description = "Número de documento del usuario",
                                    required = true,
                                    schema = @Schema(type = "string")
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = Usuario.class))),
                                    @ApiResponse(responseCode = "400", description = "Documento inválido"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado o inactivo")
                    }
            )
    )
    })

    public RouterFunction<ServerResponse> routerFunction(UsuarioHandler usuarioHandler) {
        return route(POST(usuarioPath.getUsuarios()), this.usuarioHandler::listenSaveUsuario)
                .andRoute(PUT(usuarioPath.getUsuariosById()), this.usuarioHandler::listenUpdateUsuario)
                .andRoute(DELETE(usuarioPath.getUsuariosById()), this.usuarioHandler::listenDeleteUsuario)
                .andRoute(GET(usuarioPath.getUsuarios()), this.usuarioHandler::listenGetAllUsuarios)
                .andRoute(GET(usuarioPath.getUsuariosById()), this.usuarioHandler::listenUsuarioById)
                .andRoute(GET(USUARIOS_BY_DOCUMENTO), this.usuarioHandler::listenUsuarioByNumDoc);
    }
}
