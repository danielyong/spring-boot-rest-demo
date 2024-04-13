package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class AuthenticationService {
  @Autowired
  private JwtVerificationService jwtVerificationService;

  private String getAuthToken(HttpServletRequest request) {
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    if(token == null){
      Cookie[] cookies = request.getCookies();
      for (int i = 0;cookies != null && i < cookies.length; i++) {
        if(cookies[i].getName().equals("authCookie")){
          token = cookies[i].getValue();
          break;
        }
      }
    }
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return token;
  }

  public Authentication getAuthentication(HttpServletRequest request) throws Exception {
    String AuthToken = getAuthToken(request);
    if (AuthToken == null) {
      throw new BadCredentialsException("Authorization Token not provided");
    }
    return jwtVerificationService.validateClaims(AuthToken);
  }
}
