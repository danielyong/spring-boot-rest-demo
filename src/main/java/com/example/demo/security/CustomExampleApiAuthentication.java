package com.example.demo.security;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.nimbusds.jwt.JWTClaimsSet;

public class CustomExampleApiAuthentication extends AbstractAuthenticationToken {
  private final JWTClaimsSet claimsSet;

  public CustomExampleApiAuthentication(JWTClaimsSet claimsSet, UUID subject,
      Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.claimsSet = claimsSet;
    setAuthenticated(true);
  }

  public CustomExampleApiAuthentication(JWTClaimsSet claimsSet, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.claimsSet = claimsSet;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return claimsSet;
  }

  @Override
  public Object getPrincipal() {
    return claimsSet.getSubject();
  }

  public JWTClaimsSet getClaimsSet() {
    return claimsSet;
  }
}
