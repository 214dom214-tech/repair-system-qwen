package com.example.repairsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                // Отключаем CSRF только для REST API (фронтенд использует fetch)
                .ignoringRequestMatchers("/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                // Статика и страница логина — без авторизации
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                // H2 console — только для разработки
                .requestMatchers("/h2-console/**").permitAll()

                // === Заявки ===
                // Создать заявку
                .requestMatchers(HttpMethod.POST, "/api/requests").hasAnyAuthority("ROLE_CREATOR", "ROLE_ADMIN")
                // Принять в работу
                .requestMatchers(HttpMethod.PATCH, "/api/requests/*/accept").hasAnyAuthority("ROLE_WORKER", "ROLE_ADMIN")
                // Закрыть (после ремонта)
                .requestMatchers(HttpMethod.PATCH, "/api/requests/*/close").hasAnyAuthority("ROLE_CLOSER", "ROLE_ADMIN")
                // Подтвердить ремонт
                .requestMatchers(HttpMethod.PATCH, "/api/requests/*/confirm").hasAnyAuthority("ROLE_CONFIRMER", "ROLE_ADMIN")
                // Удалить заявку
                .requestMatchers(HttpMethod.DELETE, "/api/requests/**").hasAnyAuthority("ROLE_DELETER", "ROLE_ADMIN")
                // Чтение заявок — любой авторизованный
                .requestMatchers(HttpMethod.GET, "/api/requests/**").authenticated()

                // === Оборудование — любой авторизованный ===
                .requestMatchers("/api/equipment/**").authenticated()

                // === Управление пользователями — только ADMIN ===
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                // Основная страница
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Для H2 console
            .headers(headers -> headers.frameOptions(f -> f.sameOrigin()));

        return http.build();
    }
}
