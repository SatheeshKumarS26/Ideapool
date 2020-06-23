package com.codementor.ideapool.controllers;

import com.codementor.ideapool.beans.AppUser;
import com.codementor.ideapool.exception.CustomException;
import com.codementor.ideapool.security.jwt.JwtTokenProvider;
import com.codementor.ideapool.services.UserService;
import com.codementor.ideapool.utils.ValidatorUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by Vishwa Mohan, 19th April 2019
 */
@Controller
public class AuthController {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Autowired
  UserService userService;

  /**
   *
   * @param data
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = "/access-tokens")
  @ResponseBody
  public ResponseEntity signIn(@RequestBody AuthenticationRequest data) {

    try {

      String emailId = data.getEmail();
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailId, data.getPassword()));
      String token = jwtTokenProvider.createToken(emailId);

      String refreshToken = userService.getRefreshTokenForUser(emailId);
      if (refreshToken == null) {
        refreshToken = jwtTokenProvider.createRefreshToken(emailId);
        userService.addRefreshToken(refreshToken, userService.getUserByEmailId(emailId));
        userService.updateAccessTokenUserMap("", token, userService.getUserByEmailId(emailId));
        userService.addToken(token);

        userService.updateRefreshTokenAccessTokenMap(refreshToken, token);
      } else {
        //Remove the current access token
        userService.updateAccessTokenUserMap(userService.getCurrentAccessTokenFromRefreshToken(refreshToken), token,
            null);
        userService.removeTokenIfPresent(userService.getCurrentAccessTokenFromRefreshToken(refreshToken));
        userService.updateRefreshTokenAccessTokenMap(refreshToken, token);
        userService.addToken(token);
      }
      Map<String, String> model = new HashMap<>();
      model.put("jwt", token);
      model.put("refresh_token", refreshToken);
      return ResponseEntity.ok(model);
    } catch (AuthenticationException e) {

      throw new BadCredentialsException("Invalid username/password supplied");
    }
  }

  /**
   *
   * @param data
   * @return
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/access-tokens")
  @ResponseBody
  public ResponseEntity signOut(@RequestBody RefreshTokenRequest data) {

    try {

      String refreshToken = data.getRefresh_token();
      String accessToken = userService.getCurrentAccessTokenFromRefreshToken(refreshToken);
      userService.removeUserFromAccessTokenMap(userService.getCurrentAccessTokenFromRefreshToken(refreshToken));
      userService.removeRefreshTokenAccessTokenMap(refreshToken);
      userService.removeRefreshToken(refreshToken);
      userService.removeTokenIfPresent(accessToken);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    } catch (AuthenticationException e) {

      throw new CustomException("Refresh token not valid", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  /**
   *
   * @param data
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = "/access-tokens/refresh")
  @ResponseBody
  public ResponseEntity refreshToken(@RequestBody RefreshTokenRequest data) {

    try {

      String refreshToken = data.getRefresh_token();

      if (userService.getUserfromRefreshToken(refreshToken) == null) {
        throw new CustomException("Refresh token not valid", HttpStatus.UNPROCESSABLE_ENTITY);
      }

      String token = jwtTokenProvider.createToken(userService.getUserfromRefreshToken(refreshToken).getEmailId());
      userService.updateAccessTokenUserMap(userService.getCurrentAccessTokenFromRefreshToken(refreshToken), token,
          null);
      userService.removeTokenIfPresent(userService.getCurrentAccessTokenFromRefreshToken(refreshToken));
      userService.updateRefreshTokenAccessTokenMap(refreshToken, token);
      userService.addToken(token);

      Map<String, String> model = new HashMap<>();
      model.put("jwt", token);
      return ResponseEntity.ok(model);
    } catch (AuthenticationException e) {

      throw new CustomException("Refresh token not valid", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  /**
   *
   * @param data
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = "/users")
  @ResponseBody
  public ResponseEntity signUp(@RequestBody SignUpRequest data) {
    try {

      Map<String, String> model = new HashMap<>();
      //TODO Validation on the SignUpRequest
      String emailId = data.getEmail();
      String password = data.getPassword();
      if(StringUtils.isEmpty(emailId) || !ValidatorUtil.isValid(emailId)|| StringUtils.isEmpty(password)){
        model.put("Error", "Email id is invalid/ Password is empty");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }
      String userName = data.getName();


      String refreshToken = jwtTokenProvider.createRefreshToken(emailId);
      String token = jwtTokenProvider.createToken(emailId);
      userService.addUser(emailId, userName, password, refreshToken, token);


      model.put("jwt", token);
      model.put("refresh_token", refreshToken);
      return ResponseEntity.status(HttpStatus.CREATED).body(model);
    } catch (Exception e) {
      throw new CustomException("EmailId " + data.getEmail() + " already registered", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  /**
   *
   * @param accessToken
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, value = "/me")
  @ResponseBody
  public ResponseEntity getUserDetails(@RequestHeader(value = "X-Access-Token") String accessToken) {

    AppUser app = userService.getUserFromAccessToken(accessToken.trim());
    Map<String, String> model = new HashMap<>();
    model.put("email", app.getEmailId());
    model.put("name", app.getUsername());
    model.put("avatar_url", app.getAvatarUrl());

    return ResponseEntity.ok(model);
  }
}
