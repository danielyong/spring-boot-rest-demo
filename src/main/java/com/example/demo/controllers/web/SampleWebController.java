package com.example.demo.controllers.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.beans.functions.SampleSpringBean;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/web")
public class SampleWebController {
  @Autowired
  private SampleSpringBean sampleSpringBean;

  @GetMapping("/weather")
  public String weather(){
    return "weather";
  }
  @GetMapping("/index")
  public String index(Model model) {
    model.addAttribute("userList", sampleSpringBean.getAllUser());
    return "index";
  }
  @GetMapping("/login")
  public String login(Model model) {
    return "login";
  }
  @PostMapping("/register")
  public void register(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws IOException{
    if(sampleSpringBean.createUser(username, password)){
      String authenticationToken = sampleSpringBean.generateJwtToken(username, password);
      Cookie authenticationCookie = new Cookie("authCookie", authenticationToken);
      authenticationCookie.setHttpOnly(true);
      authenticationCookie.setPath("/");
      response.addCookie(authenticationCookie);
      response.sendRedirect("/web/index");
    }else{
      response.sendRedirect("/web/login");
    }
  }

  @PostMapping("/login")
  public void login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws IOException {
    // Returns null if the user does not exist or gave invalid password
    String authenticationToken = sampleSpringBean.generateJwtToken(username, password);
    if(authenticationToken != null){
      Cookie authenticationCookie = new Cookie("authCookie", authenticationToken);
      authenticationCookie.setHttpOnly(true);
      authenticationCookie.setPath("/");
      response.addCookie(authenticationCookie);
      response.sendRedirect("/web/index");
    }else{
      response.sendRedirect("/web/login");
    }
  }
}
