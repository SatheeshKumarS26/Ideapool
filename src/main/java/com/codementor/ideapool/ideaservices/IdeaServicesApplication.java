package com.codementor.ideapool.ideaservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Created by Vishwa Mohan , 19th April 2019
 */
@SpringBootApplication(scanBasePackages = {"com.codementor"})
public class IdeaServicesApplication {

  public static void main(String[] args) {
    SpringApplication.run(IdeaServicesApplication.class, args);
  }
}
