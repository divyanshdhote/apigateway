package com.divyansh.apigateway.filter;

import com.divyansh.apigateway.auth.JwtUtil;
import com.divyansh.apigateway.ratelimiter.service.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RateLimitingFilter implements WebFilter {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String userId = extractUser(exchange);

        // fallback if no token
        if (userId == null) {
            userId = "anonymous";
        }

        boolean allowed = rateLimiter.allowRequest(userId);

        if (!allowed) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private String extractUser(ServerWebExchange exchange) {
        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");

        return userId != null ? userId : "anonymous";
    }
}