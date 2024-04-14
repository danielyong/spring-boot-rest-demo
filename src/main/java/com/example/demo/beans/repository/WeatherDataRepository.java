package com.example.demo.beans.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.general.WeatherData;

public interface WeatherDataRepository extends CrudRepository<WeatherData, UUID> {
}
