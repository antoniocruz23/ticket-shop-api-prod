package com.ticket.shop.security;

import com.ticket.shop.command.auth.PrincipalDto;
import com.ticket.shop.service.AuthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Authentication provider
 */
@Component
public class UserAuthenticationProvider {

    private final AuthService authService;

    public UserAuthenticationProvider(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Validate jwt token
     * @param token token
     * @return {@link Authentication} with authenticated user
     */
    public Authentication validateToken(String token) {
        // validate jwt token and get principal
        PrincipalDto principal = this.authService.validateToken(token);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList()));
    }
}
