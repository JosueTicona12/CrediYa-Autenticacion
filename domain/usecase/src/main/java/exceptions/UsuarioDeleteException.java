package exceptions;

public class UsuarioDeleteException extends RuntimeException {
    public UsuarioDeleteException(Long id) {
        super("No se pudo eliminar el usuario con id " + id);
    }
}