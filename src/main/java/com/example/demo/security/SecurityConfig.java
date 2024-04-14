package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Autowired
  private SpringSecurityFilterConfig springSecurityFilterConfig;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(csrf -> csrf.disable())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(springSecurityFilterConfig, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            auth -> auth
              .requestMatchers("/.well-known/**", "/web/login", "/web/register", "/web/weather", "/api/weather").permitAll()
              .requestMatchers("/web/index", "/api/**").authenticated()
              //Can do roles and authorities tbh
              .anyRequest().permitAll());
    return httpSecurity.build();
  }
}
