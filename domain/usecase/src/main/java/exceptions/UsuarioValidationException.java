package exceptions;

public class UsuarioValidationException extends RuntimeException {
    public UsuarioValidationException(String message) {
        super("Error de validación: " + message);
    }
}

