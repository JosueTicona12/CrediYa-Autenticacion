package co.com.autenticacion.config;

import co.com.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.autenticacion.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {


            UsuarioUseCase uc = context.getBean(UsuarioUseCase.class);
            assertTrue(uc != null, "UsuarioUseCase no fue creado");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public UsuarioRepository usuarioRepository() {

            return Mockito.mock(UsuarioRepository.class);
        }
    }
}