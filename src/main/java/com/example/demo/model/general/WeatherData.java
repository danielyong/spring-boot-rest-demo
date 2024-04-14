package com.example.demo.model.general;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class WeatherData {
  @Id
  private UUID dataId;
  private Date createdAt;
  private Float temperature;
  private String summary;

  public Date getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
  public Float getTemperature() {
    return temperature;
  }
  public void setTemperature(Float temperature) {
    this.temperature = temperature;
  }
  public String getSummary() {
    return summary;
  }
  public void setSummary(String summary) {
    this.summary = summary;
  }
  public UUID getDataId() {
    return dataId;
  }
  public void setDataId(UUID dataId) {
    this.dataId = dataId;
  }
}
