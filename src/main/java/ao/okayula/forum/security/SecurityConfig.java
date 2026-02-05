package ao.okayula.forum.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Injeta o filtro JWT por meio do construtor
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configura o AuthenticationManager, usado para autenticar usuários.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração da cadeia de filtros de segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desativa a proteção contra CSRF (não necessária para APIs RESTful)
                .csrf(csrf -> csrf.disable())
                /*  configura a política de sessão para STATELESS, ideal para APIs que usam tokens e não mantêm estado entre as requisições. */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configura as regras de autorização
                .authorizeHttpRequests(authorize -> authorize
                // Define os endpoints públicos
                .requestMatchers(
                        "/api/v1/users/register",
                        "/api/v1/users/login",
                        "/api/v1/users/create",
                        "/api/faturacao/**")
                .permitAll()
                // Todas as outras solicitações requerem autenticação
                .anyRequest().authenticated()
                )
                // Desativa o formulário de login padrão e autenticação HTTP básica
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                // Adiciona o filtro JWT antes do UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
