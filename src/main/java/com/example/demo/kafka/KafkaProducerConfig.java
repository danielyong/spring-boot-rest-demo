package com.example.demo.kafka;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.model.general.WeatherData;

import jakarta.annotation.PostConstruct;

@Component
public class KafkaProducerConfig {
  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;
  @Value(value = "${demo.kafka.topic}")
  private String kafkaTopic;

  private final Float MinTemperature = 17f;
  private final Float MaxTemperature = 35f;

  private KafkaTemplate<String, WeatherData> template;

  private UUID randomULID(Instant seed) {
    StringBuilder time_component = new StringBuilder(Long.toHexString(seed.toEpochMilli()));
    for (int i = time_component.length(); i < 12; i++) {
      time_component.insert(0, '0');
    }
    return UUID
        .fromString(time_component.substring(0, 8) + "-"
            + time_component.substring(8)
            + UUID.randomUUID().toString().substring(13));
  }

  public ProducerFactory<String, WeatherData> weatherDataProducerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @PostConstruct
  public void initKafkaTemplate(){
    template = new KafkaTemplate<>(this.weatherDataProducerFactory());
  }

  @Scheduled(fixedRate = 3000)
  public void sendMessage() {
    Float temperature = (float)(Math.random() * (MaxTemperature - MinTemperature) + MinTemperature);
    String summary = "-";
    if(temperature < 20.0f){
      summary = "Cool";
    }else if (temperature >= 20.0f && temperature < 27.0f){
      summary = "Temperate";
    }else{
      summary = "Hot";
    }

    WeatherData data = new WeatherData();
    data.setDataId(randomULID(Instant.now()));
    data.setCreatedAt(new Date());
    data.setTemperature(temperature); 
    data.setSummary(summary);

    CompletableFuture<SendResult<String, WeatherData>> future = template.send(kafkaTopic, data);
    future.whenComplete((result, ex) -> {
      if (ex == null) {
        System.out.println("Sent message=[" + data.getDataId().toString() + "] with offset=[" + result.getRecordMetadata().offset() + "]");
      } else {
        System.out.println("Unable to send message=["+ data.getDataId().toString() +"] due to : " + ex.getMessage());
      }
    });
  }
}
