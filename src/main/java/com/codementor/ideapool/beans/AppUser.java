package com.codementor.ideapool.beans;

import com.codementor.ideapool.utils.MD5Util;


/**
 * Created by Vishwa Mohan , 19th April 2019
 */
public class AppUser {
  private String emailId;
  private String username, password;
  private String role;
  private String avatarUrl;

  public AppUser(String emailId, String username, String password, String role) {
    this.emailId = emailId;
    this.username = username;
    this.password = password;
    this.role = role;
    this.avatarUrl = "https://www.gravatar.com/avatar/" + MD5Util.md5Hex(emailId);
  }

  public AppUser(String emailId, String username, String password) {
    this.emailId = emailId;
    this.username = username;
    this.password = password;
  }

  public String getEmailId() {
    return emailId;
  }

  public void setEmailId(String emailId) {
    this.emailId = emailId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}