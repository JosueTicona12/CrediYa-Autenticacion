package co.com.autenticacion.api.helper;

import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
public class ClientIdFilter implements WebFilter {
    private static final String CLIENT_ID_HEADER = "X-Client-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(clientId -> {
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header(CLIENT_ID_HEADER, clientId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
