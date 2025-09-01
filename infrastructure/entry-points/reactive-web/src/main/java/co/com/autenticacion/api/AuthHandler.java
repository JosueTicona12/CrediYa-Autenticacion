package co.com.autenticacion.api;

import co.com.autenticacion.jwtsigner.util.JwtUtil;
import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.usecase.login.LoginUseCase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthHandler {
    private final LoginUseCase loginUseCase;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(auth -> loginUseCase.findByEmail(auth.getEmail())
                        .flatMap(usuario -> validarPasswordYGenerarToken(auth.getPassword(), usuario))
                        .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build()));
    }

    private Mono<ServerResponse> validarPasswordYGenerarToken(String rawPassword, Usuario usuario) {
        if (usuario.getPassword() != null && passwordEncoder.matches(rawPassword, usuario.getPassword())) {
            String token = jwtUtil.generateToken(
                    usuario.getEmail(),
                    List.of(String.valueOf(usuario.getRolId())));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new TokenResponse(token));
        }
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Data
    private static class LoginRequest {
        private String email;
        private String password;
    }

    @AllArgsConstructor
    @Getter
    private static class TokenResponse {
        private final String token;
    }
}
