package com.example.trabalhoo_api_1.config;
import com.example.trabalhoo_api_1.repository.RoleRepository;
import com.example.trabalhoo_api_1.entity.Role;
import com.example.trabalhoo_api_1.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;

    public SecurityConfig(UserService userService, JwtUtil jwtUtil, RoleRepository roleRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login", "/users/register").permitAll() //aqui configurei para ambos poderem registrar
                        .requestMatchers("/destinations").hasAnyRole("USER", "ADMIN") // aqui tanto o user quanto o admin podem ver os destinos
                        .requestMatchers("/destinations/reserve/**").hasRole("USER")//aqui mostra que so o user pode reservar um destino
                        .requestMatchers("/destinations/**").hasRole("ADMIN") // indica que apenas usuários com o papel ADMIN podem acessar qualquer URL que comece com /destinations/
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public CommandLineRunner loadData() {
        return args -> {

            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                roleRepository.save(new Role("ROLE_USER"));
            }

            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(new Role("ROLE_ADMIN"));
            }
        };
    }



}