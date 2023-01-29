package com.ticket.shop.security;

import com.ticket.shop.command.auth.PrincipalDto;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationValidatorService {
    public boolean hasRole(String role) {
        return getPrincipal().getRoles().stream()
                .anyMatch(r -> r.name().equals(role));
    }

    public boolean isUser(Long userId) {
        return userId.equals(getPrincipal().getUserId());
    }

    private PrincipalDto getPrincipal() {
        return (PrincipalDto) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
