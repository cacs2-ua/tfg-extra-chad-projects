package vitalsanity.config;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Permitimos el acceso a recursos públicos
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Configuramos el formulario de login personalizado
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/doLogin") // Endpoint personalizado para el POST de login
                        .defaultSuccessUrl("/upload", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                // Configuramos logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // Deshabilitamos CSRF para facilitar el desarrollo (¡En producción, configúralo adecuadamente!)
                .csrf(csrf -> csrf.disable());

        // Permitir acceso a la consola H2 (sólo en desarrollo)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    // Exponer AuthenticationManager para uso en el controlador de login personalizado
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Definir un PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
