package vitalsanity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// No es necesario agregar @EnableWebSecurity en Spring Boot 3.4.0
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configuración de autorización de peticiones
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/sign/**",           // Rutas de firma
                                "/css/**",            // Recursos estáticos CSS
                                "/js/**",             // Recursos estáticos JS
                                "/images/**",         // Recursos estáticos de imágenes
                                "/h2-console/**"      // Consola H2 (si se usa para desarrollo)
                        ).permitAll()               // Permitir acceso sin autenticación
                        .anyRequest().authenticated() // Requiere autenticación para el resto
                )
                // Configuración del formulario de login
                .formLogin(form -> form
                        .permitAll() // Permitir acceso al formulario de login a todos
                )
                // Configuración del logout
                .logout(logout -> logout
                        .permitAll() // Permitir que todos puedan cerrar sesión
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")) // Deshabilitar CSRF para H2
                );

        return http.build();
    }

    /**
     * Define un usuario en memoria para propósitos de prueba.
     *
     * Usuario:
     *   - Nombre de usuario: user
     *   - Contraseña: password
     *   - Rol: USER
     */
    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Define el encoder de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
