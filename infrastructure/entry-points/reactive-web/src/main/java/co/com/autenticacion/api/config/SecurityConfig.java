package co.com.autenticacion.api.config;

import co.com.autenticacion.jwtsigner.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtUtil jwtUtil) {
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(new JwtAuthenticationManager(jwtUtil));
        jwtFilter.setServerAuthenticationConverter(exchange -> {
            String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
            }
            return Mono.empty();
        });
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(ex -> ex.authenticationEntryPoint((exchange, e) ->
                        Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/login",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**").permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private static class JwtAuthenticationManager implements ReactiveAuthenticationManager {
        private final JwtUtil jwtUtil;

        JwtAuthenticationManager(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        public Mono<Authentication> authenticate(Authentication authentication) {
            String token = authentication.getCredentials().toString();
            return Mono.fromCallable(() -> jwtUtil.validateToken(token))
                    .map(claims -> buildAuthentication(claims, token))
                    .onErrorResume(e -> Mono.empty());
        }

        private Authentication buildAuthentication(Claims claims, String token) {
            List<String> roles = claims.get("roles", List.class);
            List<GrantedAuthority> authorities = roles == null ? List.of() :
                    roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
        }
    }
}
