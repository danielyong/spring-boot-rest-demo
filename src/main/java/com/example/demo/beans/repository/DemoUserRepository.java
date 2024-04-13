package com.example.demo.beans.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.entities.DemoUser;

public interface DemoUserRepository extends CrudRepository<DemoUser, UUID> {
  public Optional<DemoUser> findByUsername(String username);
}
