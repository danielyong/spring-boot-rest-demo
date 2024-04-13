package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.general.SimpleResponse;
import com.nimbusds.jose.jwk.ECKey;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Hidden
@RestController
public class RouteController implements ErrorController {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @GetMapping("/.well-known/openid-configuration")
  public Map<String, Object> getECKeys(){
    String query = "SELECT public_key FROM security_keys WHERE key_type = 'EC'";
    List<Map<String, Object>> keys = jdbcTemplate.queryForList(query);
    List<Map<String, Object>> result = new ArrayList<>();
    for (Map<String, Object> map : keys) {
      try {
        result.add(ECKey.parse((String) map.get("public_key")).toJSONObject());
      } catch (Exception e) {
      }
    }
    Map<String, Object> ecKeyList = new HashMap<>();
    ecKeyList.put("keys", result);

    return ecKeyList;
  }

  @RequestMapping(value = "/error")
  public SimpleResponse handleError(HttpServletRequest request) {
    return new SimpleResponse(request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString());
  }
}
