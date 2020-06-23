package com.codementor.ideapool.controllers;

import com.codementor.ideapool.beans.Idea;
import com.codementor.ideapool.exception.CustomException;
import com.codementor.ideapool.security.jwt.InvalidJwtAuthenticationException;
import com.codementor.ideapool.services.IdeaService;
import com.codementor.ideapool.services.UserService;
import io.jsonwebtoken.JwtException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by Vishwa Mohan, 18th April 2019
 */

@Controller
public class IdeasServiceController {

  @Autowired
  UserService userService;

  @Autowired
  IdeaService ideaService;

  @RequestMapping(method = RequestMethod.GET, value = "/greet/")
  @ResponseBody
  public String helloWorld() {
    return "Hello World";
  }

  @RequestMapping(method = RequestMethod.POST, value = "/ideas")
  @ResponseBody
  public ResponseEntity saveIdea(@RequestHeader(value = "X-Access-Token") String accessToken,
      @RequestBody IdeaRequest data) {
    try {
      String emailId = userService.getUserFromAccessToken(accessToken.trim()).getEmailId();

      final String id = UUID.randomUUID().toString().replace("-", "");

      Idea idea = new Idea(id, data.getContent(), data.getImpact(), data.getEase(), data.getConfidence());

      String content = data.getContent();
      float impact = data.getImpact();
      float ease = data.getEase();
      float confidence = data.getConfidence();
      Map<String, Object> model = new HashMap<>();

      if(StringUtils.isEmpty(content)){
        model.put("Error", "content is empty");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }

      if(impact<1 || impact>10){
        model.put("Error", "Valid impact values are between 1-10");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }

      if(ease<1 || ease>10){
        model.put("Error", "Valid ease values are between 1-10");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }

      if(confidence<1 || confidence>10){
        model.put("Error", "Valid confidence values are between 1-10");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }


      ideaService.saveIdea(emailId, idea);

      model.put("id", id);
      model.put("content", content);
      model.put("impact", impact);
      model.put("ease", ease);
      model.put("confidence", confidence);
      model.put("average_score", idea.getAverage_score());
      model.put("created_at", idea.getCreated_at());

      return ResponseEntity.status(HttpStatus.CREATED).body(model);
    } catch (Exception e) {
      throw new CustomException("Error in adding ideas", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/ideas")
  @ResponseBody
  public ResponseEntity getIdeas(@RequestHeader(value = "X-Access-Token") String accessToken, @RequestParam int page) {
    try {
      String emailId = userService.getUserFromAccessToken(accessToken.trim()).getEmailId();

      List<Idea> ideas = ideaService.getIdeas(emailId, page * 10);

      return new ResponseEntity<List<Idea>>(ideas, HttpStatus.OK);
    } catch (Exception e) {
      throw new CustomException("Error in getting ideas", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/ideas/{id}")
  @ResponseBody
  public ResponseEntity deleteIdea(@RequestHeader(value = "X-Access-Token") String accessToken,
      @PathVariable("id") String id) {
    try {
      String emailId = userService.getUserFromAccessToken(accessToken.trim()).getEmailId();

      ideaService.deleteIdea(emailId, id);

      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    } catch (Exception e) {
      throw new CustomException("Error in deleting ideas", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/ideas/{id}")
  @ResponseBody
  public ResponseEntity updateIdea(@RequestHeader(value = "X-Access-Token") String accessToken,
      @PathVariable("id") String id, @RequestBody IdeaRequest data) {
    try {

      String content = data.getContent();
      float impact = data.getImpact();
      float ease = data.getEase();
      float confidence = data.getConfidence();
      Map<String, Object> model = new HashMap<>();

      if(StringUtils.isEmpty(content)){
        model.put("Error", "content is empty");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }

      if(impact<1 || impact>10){
        model.put("Error", "Valid impact values are between 1-10");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }

      if(ease<1 || ease>10){
        model.put("Error", "Valid ease values are between 1-10");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }

      if(confidence<1 || confidence>10){
        model.put("Error", "Valid confidence values are between 1-10");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(model);
      }

      String emailId = userService.getUserFromAccessToken(accessToken.trim()).getEmailId();


      Idea idea = ideaService.updateIdea(emailId, id, data.getContent(), data.getImpact(), data.getEase(),
          data.getConfidence());

      return new ResponseEntity<Idea>(idea, HttpStatus.OK);
    } catch (Exception e) {
      throw new CustomException("Error in updating idea", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }
}
