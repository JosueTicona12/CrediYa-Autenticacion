package co.com.autenticacion.r2dbc;

import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.autenticacion.r2dbc.entity.UsuarioEntity;
import co.com.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository

public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioEntity,
        String,
        UsuarioReactiveRepository
> implements UsuarioRepository {
    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository, ObjectMapper mapper) {

        super(repository, mapper, entity -> mapper.map(entity, Usuario.class));
    }

    @Override
    public Mono<Usuario> save(Usuario usuario) {
        return super.save(usuario);
    }

    @Override
    public Flux<Usuario> findAll() {
        return super.findAll();
    }

    @Override
    public Mono<Usuario> findById(String id) {
        return super.findById(id);
    }
    @Override
    public Mono<Void> deleteById(String id) {
        return super.repository.deleteById(id);
    }
}
