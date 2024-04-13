package com.example.demo.model.general;

public class SimpleResponse {
  private String message;

  public SimpleResponse(String message){
    this.message = message;
  }
  public String getMessage() {
    return this.message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
}
