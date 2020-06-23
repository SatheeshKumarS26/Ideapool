package com.codementor.ideapool.security.jwt;

import org.springframework.security.core.AuthenticationException;


/**
 * Created by Vishwa Mohan , 19th April 2019
 */
public class InvalidJwtAuthenticationException extends AuthenticationException {
  public InvalidJwtAuthenticationException(String e) {
    super(e);
  }
}

