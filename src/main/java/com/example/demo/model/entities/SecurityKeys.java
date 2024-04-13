package com.example.demo.model.entities;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SecurityKeys implements Serializable {
  @Id
  private UUID id = UUID.randomUUID();
  @Column(columnDefinition = "TEXT")
  private String publicKey;
  @Column(columnDefinition = "TEXT")
  private String privateKey;
  private String keyType;

  public SecurityKeys() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  public String getKeyType() {
    return keyType;
  }

  public void setKeyType(String keyType) {
    this.keyType = keyType;
  }
}
