package com.ticket.shop.configuration;

import com.ticket.shop.security.AuthorizationValidatorService;
import com.ticket.shop.security.CookieAuthFilter;
import com.ticket.shop.security.JwtAuthFilter;
import com.ticket.shop.security.UserAuthenticationEntryPoint;
import com.ticket.shop.security.UserAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Security Config class
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final UserAuthenticationProvider userAuthenticationProvider;


    public SecurityConfig(UserAuthenticationEntryPoint userAuthenticationEntryPoint, UserAuthenticationProvider userAuthenticationProvider) {
        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
        this.userAuthenticationProvider = userAuthenticationProvider;
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
                .exceptionHandling().authenticationEntryPoint(this.userAuthenticationEntryPoint)
                .and()
                .addFilterBefore(new JwtAuthFilter(this.userAuthenticationProvider), BasicAuthenticationFilter.class)
                .addFilterBefore(new CookieAuthFilter(this.userAuthenticationProvider), JwtAuthFilter.class)
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

    @Bean
    public AuthorizationValidatorService authorized() {
        return new AuthorizationValidatorService();
    }

}
