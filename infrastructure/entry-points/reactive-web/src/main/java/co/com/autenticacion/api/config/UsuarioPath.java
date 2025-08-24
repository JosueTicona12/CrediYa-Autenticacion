package co.com.autenticacion.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "routes.paths")
public class UsuarioPath {
    private String usuarios;
    private String usuariosById;
}
