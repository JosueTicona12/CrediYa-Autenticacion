package exceptions;

public class UsuarioUpdateException extends RuntimeException {
    public UsuarioUpdateException(Long id) {
        super("No se pudo actualizar el usuario con id " + id);
    }
}