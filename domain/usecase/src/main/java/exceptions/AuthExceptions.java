package exceptions;

public class AuthExceptions {
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() { super("Credenciales inválidas"); }
    }


    public static class RemoteServiceException extends RuntimeException {
        public RemoteServiceException(String msg) { super(msg); }
    }
}
