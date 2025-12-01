package isa.restaurante.restaurante.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class DataBaseWebSecurity {

    // ===== ROLES (para no repetir cadenas) =====
    public static final String ADMIN     = "ADMIN";
    public static final String SUPERVISOR= "SUPERVISOR";
    public static final String CAJERO    = "CAJERO";
    public static final String MESERO    = "MESERO";
    public static final String COCINERO  = "COCINERO";
    public static final String CLIENTE   = "CLIENTE";

    // ===== ENCODER (modo práctica: contraseñas en texto plano) =====
    // Ojo: esto es solo para pruebas, no para producción real.
    @Bean
    PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder enc =
                (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        enc.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return enc;
    }

    // ===== USER DETAILS (JDBC) =====
    @Bean
    UserDetailsManager usuarios(DataSource dataSource) {
        JdbcUserDetailsManager jdbc = new JdbcUserDetailsManager(dataSource);

        jdbc.setUsersByUsernameQuery(
                "select u.username, u.password, u.estatus " +
                "from usuario u " +
                "where u.username = ?"
        );

        jdbc.setAuthoritiesByUsernameQuery(
                "select u.username, concat('ROLE_', p.perfil) as authority " +
                "from usuario u " +
                "inner join usuarioperfil up on up.idusuario = u.id " +
                "inner join perfil p        on p.id        = up.idperfil " +
                "where u.username = ?"
        );
        return jdbc;
    }

    // ===== AUTH PROVIDER =====
    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsManager uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    // ===== REGLAS HTTP =====
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider provider) throws Exception {
        http
            .authenticationProvider(provider)
            .authorizeHttpRequests(auth -> auth

                // --- RUTAS PÚBLICAS ---
                .requestMatchers(
                    "/", "/inicio", "/home",
                    "/login", "/logout-confirmado",
                    "/menu/**", "/registro"
                ).permitAll()
                .requestMatchers(
                    "/css/**", "/js/**", "/imagen/**", "/webjars/**"
                ).permitAll()

                // --- ADMIN ---
                .requestMatchers("/usuarios/**", "/perfiles/**")
                    .hasRole(ADMIN)

                // --- COMPLETAR PERFIL EMPLEADO (cualquier rol operativo) ---
                .requestMatchers("/empleados/completar-perfil", "/empleados/guardar-perfil")
                    .hasAnyRole(ADMIN, SUPERVISOR, CAJERO, MESERO, COCINERO)

                // --- EMPLEADOS / MESAS / PRODUCTOS (gestión) ---
                .requestMatchers("/empleados/**")
                    .hasAnyRole(ADMIN, SUPERVISOR)
                .requestMatchers("/mesas/**")
                    .hasAnyRole(ADMIN, SUPERVISOR)
                .requestMatchers("/admin/productos/**")
                    .hasAnyRole(ADMIN, SUPERVISOR)

                // --- CLIENTE: completar su propio perfil ---
                .requestMatchers("/clientes/completar-perfil", "/clientes/guardar-perfil")
                    .hasRole(CLIENTE)

                // --- CLIENTES: gestión/consulta (ADMIN y CAJERO) ---
                .requestMatchers("/clientes/**", "/busquedas/clientes/**")
                    .hasAnyRole(ADMIN, CAJERO)

                // --- RESERVAS: ADMIN, CLIENTE, CAJERO ---
                .requestMatchers("/reservas/**")
                    .hasAnyRole(ADMIN, CLIENTE, CAJERO)

                // --- PEDIDOS / ATENDER: operativos de venta ---
                .requestMatchers("/pedidos/**", "/atender/**")
                    .hasAnyRole(ADMIN, CAJERO, MESERO, COCINERO)

                // CUALQUIER OTRA COSA: autenticado
                .anyRequest().authenticated()
            )
            // LOGIN / LOGOUT
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout-confirmado")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(Customizer.withDefaults())
            .csrf(Customizer.withDefaults());

        return http.build();
    }
}

