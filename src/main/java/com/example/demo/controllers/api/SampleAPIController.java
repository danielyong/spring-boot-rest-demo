package com.example.demo.controllers.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.beans.functions.SampleSpringBean;
import com.example.demo.model.general.SimpleResult;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Sample API", description = "Sample API Controller")
@RestController
@RequestMapping("/api")
public class SampleAPIController {
  @Autowired
  private SampleSpringBean sampleSpringBean; 

  @PutMapping("/user/{user_id}")
  public SimpleResult updatePassword(@PathVariable String user_id, @RequestParam String password){
    if(sampleSpringBean.updatePassword(UUID.fromString(user_id), password)){
      return new SimpleResult("success", "Password updated successfully");
    }else{
      return new SimpleResult("failed", "Failed to update password");
    }
  }

  @DeleteMapping("/user/{user_id}")
  public SimpleResult deleteUser(@PathVariable String user_id, @RequestParam String password){
    if(sampleSpringBean.deleteUser(UUID.fromString(user_id))){
      return new SimpleResult("success", "Deleted user");
    }else{
      return new SimpleResult("failed", "Failed to delete user");
    } 
  }
}
