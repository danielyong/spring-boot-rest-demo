package com.example.demo.beans.core;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "${origin.application.module}", version = "${origin.application.version}"))
public class ApiDocumentation {

}
