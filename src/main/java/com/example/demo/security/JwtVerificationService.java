package com.example.demo.security;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

@Configuration
public class JwtVerificationService {
  @CacheEvict(value = "jwt-authentication", allEntries = true)
  @Scheduled(fixedRate = 300000)
  public void emptyCache() {
    // Its an example, just clear it every 5 minutes lol 
  }

  @Cacheable("jwt-authentication")
  public Authentication validateClaims(String AuthToken) throws Exception {
    SignedJWT unverifiedJwt = SignedJWT.parse(AuthToken);

    // continue with header and claims extraction...
    JWSHeader header = unverifiedJwt.getHeader();
    JWTClaimsSet jwtClaimsSet = unverifiedJwt.getJWTClaimsSet();

    String jwtIssuer = jwtClaimsSet.getIssuer();
    // The expected JWS algorithm of the access tokens (agreed out-of-band)
    JWSAlgorithm expectedJWSAlg = (JWSAlgorithm) header.getAlgorithm();

    String jwkSource = jwtIssuer + "/.well-known/openid-configuration";

    // Create a JWT processor for the access tokens
    ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

    // Set the required "typ" header "at+jwt" for access tokens
    jwtProcessor.setJWSTypeVerifier(
        new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("jwt")));

    // The public RSA keys to validate the signatures will be sourced from the
    // OAuth 2.0 server's JWK set URL. The key source will cache the retrieved
    // keys for 5 minutes. 30 seconds prior to the cache's expiration the JWK
    // set will be refreshed from the URL on a separate dedicated thread.
    // Retrial is added to mitigate transient network errors.
    JWKSource<SecurityContext> keySource = JWKSourceBuilder
        .create((new URI(jwkSource)).toURL())
        .retrying(true)
        .build();

    // Configure the JWT processor with a key selector to feed matching public
    // RSA keys sourced from the JWK set URL
    JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
        expectedJWSAlg,
        keySource);
    jwtProcessor.setJWSKeySelector(keySelector);

    // Set the required JWT claims for access tokens
    jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
        new JWTClaimsSet.Builder().issuer(jwtIssuer).build(),
        new HashSet<>(Arrays.asList(
            JWTClaimNames.SUBJECT,
            JWTClaimNames.ISSUED_AT,
            JWTClaimNames.EXPIRATION_TIME,
            JWTClaimNames.JWT_ID))));

    // Process the token
    SecurityContext ctx = null; // optional context parameter, not required here
    JWTClaimsSet claimsSet = jwtProcessor.process(AuthToken, ctx);

    // Default to no authority claims
    List<GrantedAuthority> grantedAuth = AuthorityUtils.NO_AUTHORITIES;
    try {
      // Assigning custom authorization claims
      List<String> authClaimList = new ArrayList<>();
      if (claimsSet.getStringListClaim("authorization_details") != null) {
        authClaimList.addAll(authClaimList);
      }
      String[] authClaimArray = authClaimList.stream().toArray(String[]::new);
      grantedAuth = AuthorityUtils.createAuthorityList(authClaimArray);
    } catch (Exception e) {
      // I don't really care about the error tbh
    }
    return new CustomExampleApiAuthentication(claimsSet, grantedAuth);
  }
}
