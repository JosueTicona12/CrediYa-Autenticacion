package co.com.autenticacion.model.login;
import lombok.Builder;

@Builder(toBuilder = true)
public record Login(String email, String password) {
}
