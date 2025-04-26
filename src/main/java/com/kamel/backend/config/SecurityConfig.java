package com.kamel.backend.config;

import com.kamel.backend.security.JwtAuthenticationFilter;
import com.kamel.backend.security.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final MyUserDetailsService _myUserDetailsService;
    private final JwtAuthenticationFilter _jwtAuthenticationFilter;
    @Autowired
    public SecurityConfig(MyUserDetailsService myUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        _myUserDetailsService = myUserDetailsService;
        _jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("api/auth/**").permitAll()
                        .requestMatchers("/api/auth/secure").authenticated()
                        .requestMatchers("/api/user/**").hasRole("BOSS")
                        .requestMatchers("/api/token/**").hasRole("BOSS")
                        .requestMatchers("/api/product").permitAll()
                        .requestMatchers("/api/product/create").hasRole("SELLER")
                        .requestMatchers("/api/product/update/**").hasRole("SELLER")
                        .requestMatchers("/api/product/delete/**").hasRole("SELLER")
                        .requestMatchers("/api/order/create").hasRole("BUYER")
                        .requestMatchers("/api/order/update").hasRole("BUYER")
                        .requestMatchers("/api/order/delete").hasRole("BUYER")
                        .requestMatchers("/api/cart/init").hasRole("BUYER")
                        .requestMatchers("/api/cart/update").hasRole("BUYER")
                        .requestMatchers("/api/cart/delete").hasRole("BUYER")
                        .requestMatchers("/api/cart/item").hasRole("BUYER")
                        .requestMatchers("/api/cart/items").hasRole("BUYER")
                        .anyRequest().permitAll())
                .formLogin(f -> f.disable())
                .userDetailsService(_myUserDetailsService)
                .addFilterBefore(_jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManager = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManager
                .userDetailsService(_myUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManager.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173") // Your React frontend
                        .allowedMethods("*")
                        .allowCredentials(true);
            }
        };
    }
}
