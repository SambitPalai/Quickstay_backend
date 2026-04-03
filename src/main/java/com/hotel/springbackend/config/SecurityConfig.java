package com.hotel.springbackend.config;

import com.hotel.springbackend.security.JwtAuthFilter;
import com.hotel.springbackend.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .sessionManagement(s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        // No token or invalid token → 401 Unauthorized
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write(
                            "{\"status\": 401, \"error\": \"Unauthorized\", " +
                            "\"message\": \"Authentication required. Please login.\"}"
                        );
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        // Valid token but wrong role → 403 Forbidden
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write(
                            "{\"status\": 403, \"error\": \"Forbidden\", " +
                            "\"message\": \"You do not have permission to access this resource.\"}"
                        );
                    })
                )
            
            
            .authorizeHttpRequests(auth -> auth		
                // ----- PUBLIC ------------ 
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/rooms/all-rooms").permitAll()
                .requestMatchers(HttpMethod.GET, "/rooms/room/types").permitAll()
                .requestMatchers(HttpMethod.GET, "/rooms/room/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/bookings/confirmation/**").permitAll()
                .requestMatchers("/auth/forgot-password").permitAll()
                .requestMatchers("/auth/verify-otp").permitAll()
                .requestMatchers("/auth/reset-password").permitAll()
                // ---- OWNER ONLY ----------
                .requestMatchers("/auth/create-admin").hasRole("OWNER")
                .requestMatchers("/auth/delete-admin/**").hasRole("OWNER")
                .requestMatchers("/auth/admins").hasRole("OWNER")
                .requestMatchers("/auth/promote/**").hasRole("OWNER")
                .requestMatchers("/auth/demote/**").hasRole("OWNER")
                // ------- OWNER & ADMIN  --------
                .requestMatchers(HttpMethod.POST,   "/rooms/add/new-room").hasAnyRole("ADMIN","OWNER")
                .requestMatchers(HttpMethod.PUT,    "/rooms/update/**").hasAnyRole("ADMIN","OWNER")
                .requestMatchers(HttpMethod.DELETE, "/rooms/delete/**").hasAnyRole("ADMIN","OWNER")
                .requestMatchers(HttpMethod.GET,    "/bookings/all-bookings").hasAnyRole("ADMIN","OWNER")
                .requestMatchers(HttpMethod.GET,    "/bookings/user/bookings").hasAnyRole("ADMIN", "OWNER")
                .requestMatchers(HttpMethod.GET,  "/complaints/all").hasAnyRole("ADMIN","OWNER")
                .requestMatchers(HttpMethod.GET,  "/complaints/filter").hasAnyRole("ADMIN","OWNER")
                .requestMatchers(HttpMethod.PATCH,  "/complaints/*/status").hasAnyRole("ADMIN","OWNER")
                // ------- AUNTHENTICATED USERS -------
                .requestMatchers("/bookings/my-bookings").authenticated()
                .requestMatchers("/bookings/user/**").authenticated()
                .requestMatchers("/bookings/room/**").authenticated()
                .requestMatchers("/bookings/booking/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/complaints/raise").authenticated()
                .requestMatchers(HttpMethod.GET,  "/complaints/my-complaints").authenticated()
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)          // ← THIS was missing
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "https://quickstay-web.vercel.app"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
