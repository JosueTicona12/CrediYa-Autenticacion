package co.com.autenticacion.usecase.usuario.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UsuarioLogEnum {
    USER_REQUIRED("El usuario no puede ser nulo"),
    NAME_REQUIRED("El nombre es obligatorio"),
    LASTNAME_REQUIRED("El apellido es obligatorio"),
    EMAIL_REQUIRED("El correo electronico es obligatorio"),
    SALARY_INVALID("El salario base esta vacio o fuera de rango numerico"),
    NUMDOC_REQUIRED("El número de documento es obligatorio"),
    ID_REQUIRED("El id no puede ser nulo"),
    SAVE_USER("UseCase - Guardando usuario"),
    SAVE_USER_SUCCESS("Usuario guardado con éxito"),
    ERROR_SAVE_USER("Error guardando usuario: "),
    UPDATE_USER("UseCase - Actualizando usuario con id "),
    UPDATE_USER_SUCCESS("Usuario actualizado: "),
    ERROR_UPDATE_USER("Error actualizando usuario con id "),
    GET_ALL_USERS("UseCase - Buscar todos los usuarios"),
    GET_ALL_USERS_COMPLETE("Consulta de usuarios completada"),
    ERROR_GET_ALL_USERS("Error consultando todos los usuarios: "),
    GET_USER_BY_ID("UseCase - Buscar usuario por id "),
    USER_FOUND("Usuario encontrado: "),
    ERROR_GET_USER_BY_ID("Error buscando usuario con id "),
    DELETE_USER("UseCase - Eliminando (soft delete) usuario con id "),
    DELETE_USER_SUCCESS("Usuario marcado como inactivo con id "),
    ERROR_DELETE_USER("Error al marcar usuario como inactivo con id "),
    FIND_BY_EMAIL("UseCase - Busqueda de usuario por correo"),
    ERROR_FIND_BY_EMAIL("Error buscando usuario con email "),
    FIND_BY_NUMDOC("UseCase - Busqueda de usuario por documento"),
    ERROR_FIND_BY_NUMDOC("Error buscando por documento "),
    HANDLER_SAVE_REQUEST("Handler - Recibida petición de guardado para usuario"),
    HANDLER_UPDATE_REQUEST("Handler - Recibida petición de actualización para usuario con id={}"),
    HANDLER_GET_ALL_REQUEST("Handler - Recibida petición de obtener todos los usuarios"),
    HANDLER_GET_BY_ID_REQUEST("Handler - Recibida petición de obtener usuario con id={}"),
    HANDLER_GET_BY_NUMDOC_REQUEST("Handler - Recibida petición de obtener usuario con numDocumento={}"),
    HANDLER_DELETE_REQUEST("Handler - Recibida petición de eliminar usuario con id={}");

    private final String message;
}
