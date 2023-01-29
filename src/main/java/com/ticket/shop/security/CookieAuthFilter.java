package com.ticket.shop.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Cookie authentication filter
 */
public class CookieAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(CookieAuthFilter.class);
    private final UserAuthenticationProvider userAuthenticationProvider;
    public final static String COOKIE_NAME = "auth_by_cookie";

    public CookieAuthFilter(UserAuthenticationProvider userAuthenticationProvider) {
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    /**
     * Filter implementation to authenticate with cookie if provided
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        Optional<Cookie> authCookie = Stream.of(Optional.ofNullable(httpServletRequest.getCookies())
                        .orElse(new Cookie[0]))
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()) &&
                        Objects.nonNull(cookie.getValue()) &&
                        !cookie.getValue().isEmpty())
                .findFirst();

        try {
            authCookie.ifPresent(cookie -> {
                SecurityContextHolder.getContext().setAuthentication(
                        userAuthenticationProvider.validateToken(cookie.getValue())
                );
                LOGGER.info("Successfully authenticated with cookie");
            });
        } catch (RuntimeException e) {
            SecurityContextHolder.clearContext();
            LOGGER.error("Failed to validate cookie", e);
        }

        // Always call this in order to tell Spring Security to continue
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
