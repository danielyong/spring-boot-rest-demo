package com.example.demo.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.demo.beans.repository.WeatherDataRepository;
import com.example.demo.model.general.WeatherData;

@Component
public class KafkaListenerConfig {
  @Autowired
  private WeatherDataRepository weatherDataRepository;

  @KafkaListener(topics = "${demo.kafka.topic}", containerFactory = "weatherDataListenerContainerFactory")
  public void weatherDataListner(WeatherData data) {
    System.out.println("Received weather data [" + data.toString() + "]");
    try{
      weatherDataRepository.save(data);
    }catch(Exception e){
      System.out.println("Failed to save weather data [" + e.getMessage() + "]");
    }
  }
}
