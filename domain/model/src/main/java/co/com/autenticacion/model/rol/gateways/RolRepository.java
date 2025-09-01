package co.com.autenticacion.model.rol.gateways;

import co.com.autenticacion.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> findById(Long id);
}
