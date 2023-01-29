package com.ticket.shop.configuration;

import com.ticket.shop.security.UserAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Config class
 */
@Configuration
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;

    public SecurityConfig(UserAuthenticationEntryPoint userAuthenticationEntryPoint) {
        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
    }

    /**
     * Method to handle with application security
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception throws this if something wrong happens
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .exceptionHandling().authenticationEntryPoint(userAuthenticationEntryPoint)
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/login", "/api/users")
                .permitAll()
                .antMatchers("/api/swagger-ui.html", "/api/swagger-ui/*", "/v3/api-docs",
                        "/v3/api-docs/*")
                .permitAll()
                .anyRequest().authenticated();

        return http.build();
    }

}
