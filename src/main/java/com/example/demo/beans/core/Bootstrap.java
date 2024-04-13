package com.example.demo.beans.core;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import jakarta.annotation.PostConstruct;

@Component
public class Bootstrap {
  @Autowired
  private NamedParameterJdbcTemplate namedJdbcTemplate;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @PostConstruct
  public void runAfterDeploy() {
    Integer ec_key_count = 0;
    Integer rsa_key_count = 0;
    try {
      ec_key_count = ((Number) jdbcTemplate
          .queryForMap(
              "SELECT COUNT(*) as key_count, key_type FROM security_keys WHERE key_type = 'EC' GROUP BY key_type")
          .get("key_count")).intValue();
    } catch (DataAccessException e) {
      System.out.println("No EC key found, will auto generate");
    }

    try {
      rsa_key_count = ((Number) jdbcTemplate
        .queryForMap("SELECT COUNT(*) as key_count, key_type FROM security_keys WHERE key_type = 'RSA' GROUP BY key_type")
        .get("key_count")).intValue();
    } catch (DataAccessException e) {
      System.out.println("No RSA key found, will auto generate");
    }

    for (; rsa_key_count < 5; rsa_key_count++) {
      try {
        UUID key_id = UUID.randomUUID();
        RSAKey jwk = new RSAKeyGenerator(4096).keyID(key_id.toString()).generate();
        String sql = "INSERT INTO security_keys (id, private_key, public_key, key_type) VALUES (:keyid, :private_key, :public_key, 'RSA');";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("keyid", key_id);
        params.addValue("public_key", jwk.toPublicJWK().toJSONString());
        params.addValue("private_key", jwk.toJSONString());
        namedJdbcTemplate.update(sql, params);
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }
    }

    for (; ec_key_count < 5; ec_key_count++) {
      try {
        UUID key_id = UUID.randomUUID();
        ECKey jwk = new ECKeyGenerator(Curve.P_384)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(key_id.toString())
            .generate();

        String sql = "INSERT INTO security_keys (id, private_key, public_key, key_type) VALUES (:keyid, :private_key, :public_key, 'EC');";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("keyid", key_id);
        params.addValue("public_key", jwk.toPublicJWK().toJSONString());
        params.addValue("private_key", jwk.toJSONString());
        namedJdbcTemplate.update(sql, params);
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }
    }
  }
}
