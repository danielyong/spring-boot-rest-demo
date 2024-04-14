package com.example.demo.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class SpringSecurityFilterConfig extends GenericFilterBean {
  @Autowired
  private AuthenticationService authenticationService;

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    try {
      Authentication authentication = authenticationService.getAuthentication((HttpServletRequest) request);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception exp) {
    }
    filterChain.doFilter(request, response);
  }
}
