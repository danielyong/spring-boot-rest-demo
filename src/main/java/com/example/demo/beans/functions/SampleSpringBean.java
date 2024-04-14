package com.example.demo.beans.functions;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.demo.beans.repository.DemoUserRepository;
import com.example.demo.model.entities.DemoUser;
import com.example.demo.model.entities.SecurityKeys;
import com.example.demo.model.general.WeatherData;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Configuration
public class SampleSpringBean {
  @Autowired
  private DemoUserRepository demoUserRepository;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private SecurityKeys getRandomECKey(){
    String query = """
        SELECT id, private_key FROM security_keys WHERE key_type = 'EC' ORDER BY random() LIMIT 1;
      """;
    ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper.convertValue(jdbcTemplate.queryForMap(query), SecurityKeys.class);
  }

  public Iterable<DemoUser> getAllUser(){
    return demoUserRepository.findAll();
  }

  public boolean deleteUser(UUID userId){
    String query = "DELETE FROM demo_user WHERE id = :user_id";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("user_id", userId);
    return namedParameterJdbcTemplate.update(query, params) > 0;
  }

  public boolean updatePassword(UUID userId, String password){
    Optional<DemoUser> user = demoUserRepository.findById(userId);
    if(user.isPresent()){
      DemoUser toUpdate = user.get();
      toUpdate.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
      demoUserRepository.save(toUpdate);
      return true;
    }
    return false;
  }

  public boolean createUser(String username, String password){
    Optional<DemoUser> user = demoUserRepository.findByUsername(username);
    if(user.isEmpty()){
      System.out.println("User does not exist, creating");
      String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
      String query = """
          INSERT INTO demo_user (id, username, password)
          VALUES
          (:id, :username, :password);
        """;

      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("id", UUID.randomUUID());
      params.addValue("username", username);
      params.addValue("password", passwordHash);

      int inserted = namedParameterJdbcTemplate.update(query, params);
      System.out.println("User inserted: " + inserted);
      return inserted > 0;
    }
    return false;
  }

  public String generateJwtToken(String username, String password){
    Optional<DemoUser> user = demoUserRepository.findByUsername(username);
    if(user.isEmpty()){
      System.out.println("User does not exist, bye bye");
      return null;
    }
    Date now = new Date();
    Date expiry = new Date(now.getTime() + 7776000000L);
    String issuerName = "http://127.0.0.1:8081";
    if (BCrypt.checkpw(password, user.get().getPassword())) {
      try {
        SecurityKeys signing_key = this.getRandomECKey();
          ECKey key = ECKey.parse(signing_key.getPrivateKey());
          JWSSigner signer = (JWSSigner)new ECDSASigner(key);
          JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .jwtID(UUID.randomUUID().toString())
            .subject(username)
            .issuer(issuerName)
            .issueTime(now)
            .expirationTime(expiry)
            .build();
          JWSHeader headerSet = new JWSHeader.Builder(JWSAlgorithm.ES384)
              .type(JOSEObjectType.JWT)
              .keyID(key.getKeyID())
              .build();
          SignedJWT signedJWT = new SignedJWT(headerSet, claimsSet);
          signedJWT.sign(signer);
          return signedJWT.serialize();
      } catch (ParseException e) {
        e.printStackTrace();
      } catch (JOSEException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public List<WeatherData> getLatestWeatherData() {
    try {
      String query = "SELECT * FROM weather_data ORDER BY created_at DESC LIMIT 10";
      return jdbcTemplate.query(query, new BeanPropertyRowMapper<WeatherData>(WeatherData.class));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
