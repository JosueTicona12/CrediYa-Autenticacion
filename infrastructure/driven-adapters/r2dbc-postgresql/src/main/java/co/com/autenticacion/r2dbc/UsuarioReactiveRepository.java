package co.com.autenticacion.r2dbc;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.r2dbc.entity.UsuarioEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioEntity, Long>, ReactiveQueryByExampleExecutor<UsuarioEntity> {
    Mono<UsuarioEntity> findByEmail(String email);
    Flux<UsuarioEntity> findByActivo(Integer activo);
    Mono<UsuarioEntity> findByNumDocumentoAndActivo(String numDocumento, Long activo);


}
