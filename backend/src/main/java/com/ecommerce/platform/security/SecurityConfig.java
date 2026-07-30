package com.ecommerce.platform.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    private final UserSyncFilter userSyncFilter;



    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {


        http

                .csrf(AbstractHttpConfigurer::disable)


                .cors(Customizer.withDefaults())


                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        authenticationEntryPoint()
                                )
                                .accessDeniedHandler(
                                        accessDeniedHandler()
                                )
                )


                .authorizeHttpRequests(auth -> auth


                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()


                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()



                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/**"
                        ).permitAll()



                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/*/products/**"
                        ).authenticated()



                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/*/categories/**"
                        ).permitAll()



                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories/**"
                        ).permitAll()



                        .requestMatchers(
                                "/api/*/users/sync"
                        ).authenticated()



                        .requestMatchers(
                                "/api/*/orders/**"
                        ).authenticated()



                        .requestMatchers(
                                "/api/*/favourites/**"
                        ).authenticated()



                        .requestMatchers(
                                "/api/*/users/**"
                        ).authenticated()



                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/*/products/**"
                        ).hasRole("ADMIN")



                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/*/products/**"
                        ).hasRole("ADMIN")



                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/*/products/**"
                        ).hasRole("ADMIN")



                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/*/categories/**"
                        ).hasRole("ADMIN")



                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/*/categories/**"
                        ).hasRole("ADMIN")



                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/*/categories/**"
                        ).hasRole("ADMIN")



                        .anyRequest()
                                .authenticated()

                )



                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                )


                .addFilterAfter(
                        userSyncFilter,
                        BearerTokenAuthenticationFilter.class
                );



        return http.build();

    }



    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {

        return (request, response, exception) -> {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication is required."
            );

        };

    }



    @Bean
    public AccessDeniedHandler accessDeniedHandler() {

        return (request, response, exception) -> {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied."
            );

        };

    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {


        CorsConfiguration configuration =
                new CorsConfiguration();



        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );



        configuration.setAllowedMethods(
                List.of("*")
        );



        configuration.setAllowedHeaders(
                List.of("*")
        );



        configuration.setAllowCredentials(true);



        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();



        source.registerCorsConfiguration(
                "/**",
                configuration
        );



        return source;

    }



    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

}