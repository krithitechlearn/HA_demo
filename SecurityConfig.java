package com.shoreexcursions.app.config;

import com.shoreexcursions.app.security.JwtAuthenticationFilter;
import com.shoreexcursions.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6 configuration using lambda DSL.
 * Configures JWT stateless auth, CORS, CSRF, and role-based access.
 *
 * @see US-037, US-038, US-039, US-040, US-041, US-042, US-043, LLD Section 9
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                // Actuator metrics - DB_ADMIN only
                .requestMatchers("/actuator/metrics/**").hasRole("DB_ADMIN")
                // User management - DB_ADMIN only
                .requestMatchers("/api/v1/users/**").hasRole("DB_ADMIN")
                // Audit logs - DB_ADMIN only
                .requestMatchers("/api/v1/audit-logs/**").hasRole("DB_ADMIN")
                // Port/Tour write operations - EDITOR or DB_ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/ports/**").hasAnyRole("EDITOR", "DB_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/ports/**").hasAnyRole("EDITOR", "DB_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/ports").hasRole("DB_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/tours/**").hasAnyRole("EDITOR", "DB_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/tours/**").hasAnyRole("EDITOR", "DB_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/tours").hasRole("DB_ADMIN")
                // All other authenticated requests
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
