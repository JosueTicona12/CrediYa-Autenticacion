package co.com.autenticacion.api.handler;

import co.com.autenticacion.api.config.ErrorResponse;
import co.com.autenticacion.jwtsigner.util.JwtUtil;
import co.com.autenticacion.model.usuario.Usuario;
import co.com.autenticacion.usecase.login.LoginUseCase;

import co.com.autenticacion.usecase.login.utils.LoginEnum;
import exceptions.AuthExceptions;
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
        log.trace(LoginEnum.LOGIN_REQUEST.getMessage());
        return request.bodyToMono(LoginRequest.class)
                .flatMap(auth -> loginUseCase.findByEmail(auth.getEmail())
                        .flatMap(usuario -> validarPasswordYGenerarToken(auth.getPassword(), usuario)))
                .onErrorResume(AuthExceptions.InvalidCredentialsException.class, e -> {
                    log.warn(LoginEnum.INVALID_CREDENTIALS.getMessage());
                    return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ErrorResponse.builder()
                                    .status(HttpStatus.UNAUTHORIZED.value())
                                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                                    .message(e.getMessage())
                                    .path(request.path())
                                    .build());
                })
                .onErrorResume(e -> {
                    log.error(LoginEnum.ERROR_LOGIN.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ErrorResponse.builder()
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                    .message("Error interno al procesar la autenticación")
                                    .path(request.path())
                                    .build());
                });
    }

    private Mono<ServerResponse> validarPasswordYGenerarToken(String rawPassword, Usuario usuario) {
        log.trace(LoginEnum.TOKEN_GENERATED.getMessage());
        if (usuario.getPassword() != null && passwordEncoder.matches(rawPassword, usuario.getPassword())) {
            String token = jwtUtil.generateToken(
                    usuario.getEmail(),
                    List.of(String.valueOf(usuario.getRolId())));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new TokenResponse(token));
        }
        log.warn(LoginEnum.INVALID_CREDENTIALS.getMessage());
        return Mono.error(new AuthExceptions.InvalidCredentialsException());
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @AllArgsConstructor
    @Getter
    public static class TokenResponse {
        private final String token;
    }
}
