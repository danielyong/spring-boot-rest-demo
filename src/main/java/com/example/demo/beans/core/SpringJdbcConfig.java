package com.example.demo.beans.core;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ConfigurationProperties(prefix = "spring.datasource")
@PropertySource("classpath:application.properties")
public class SpringJdbcConfig {
  private String name;
  private String url;
  private String username;
  private String password;
  private String driverClassName;

  @Bean
  public DataSource dataSource() throws NamingException {
    DataSource dataSource = DataSourceBuilder.create().driverClassName(driverClassName).url(url)
          .username(username).password(password).build();
    return dataSource;
  }

  @Bean
  @Autowired
  @DependsOn({ "dataSource" })
  public NamedParameterJdbcTemplate namedParameterJdbcTemplate(final DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean
  @Autowired
  @DependsOn({ "dataSource" })
  public JdbcTemplate JdbcTemplate(final DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }
}
