package com.controle.fechamentocaixa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.controle.fechamentocaixa.security.jwt.AuthEntryPointJwt;
import com.controle.fechamentocaixa.security.jwt.AuthTokenFilter;
import com.controle.fechamentocaixa.security.services.UserDetailsServiceImpl;

/**
 * Configuração de segurança da aplicação
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  /**
   * Configuração do filtro JWT
   *
   * @return Filtro de autenticação JWT
   */
  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  /**
   * Configuração do gerenciador de autenticação
   *
   * @param authConfig Configuração de autenticação
   * @return Gerenciador de autenticação
   * @throws Exception se ocorrer um erro na configuração
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /**
   * Configuração do encoder de senha
   *
   * @return BCryptPasswordEncoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configuração da cadeia de filtros de segurança
   *
   * @param http Configuração HTTP
   * @return Configuração da cadeia de filtros
   * @throws Exception se ocorrer um erro na configuração
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Registra o UserDetailsService para a autenticação baseada em DAO (moderno)
        .userDetailsService(userDetailsService)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index.html", "/*.html", "/*.css", "/*.js").permitAll()
            .requestMatchers("/favicon.ico").permitAll()
            .requestMatchers("/public/**", "/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/test/**").permitAll()
            .requestMatchers("/test/public").permitAll()
            .requestMatchers("/test/fix-passwords").permitAll()
            .requestMatchers("/test/password-status").permitAll()
            .requestMatchers("/test/health").permitAll()
            .anyRequest().authenticated());
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
