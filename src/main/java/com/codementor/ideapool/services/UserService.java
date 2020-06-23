package com.codementor.ideapool.services;

import com.codementor.ideapool.beans.AppUser;
import com.codementor.ideapool.exception.CustomException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class UserService implements UserDetailsService {

  @Autowired
  private BCryptPasswordEncoder encoder;

  // hard coding the users. All passwords must be encoded.
  private List<AppUser> users;

  private Map<String, AppUser> refreshTokenUserMap;

  private List<String> tokenStore;

  private Map<String, String> refreshTokenAccessTokenMap;

  private Map<String, AppUser> accessTokenUserMap;

  @PostConstruct
  public void init() {
    users = new LinkedList<AppUser>(
        Arrays.asList(new AppUser("vimohan@linkedin.com", "omar", encoder.encode("12345"), "USER"),
            new AppUser("kankvish@gmail.com", "admin", encoder.encode("12345"), "ADMIN")));

    refreshTokenUserMap = new HashMap<>();
    tokenStore = new ArrayList<>();
    refreshTokenAccessTokenMap = new HashMap<>();
    accessTokenUserMap = new HashMap<>();
  }

  public void updateAccessTokenUserMap(String accessTokenPrev, String accessTokenNew, AppUser appUser) {
    if (accessTokenUserMap.containsKey(accessTokenPrev)) {
      AppUser user = accessTokenUserMap.get(accessTokenPrev);
      accessTokenUserMap.remove(accessTokenPrev);
      accessTokenUserMap.put(accessTokenNew, user);
    } else {
      accessTokenUserMap.put(accessTokenNew, appUser);
    }
  }

  public AppUser getUserFromAccessToken(String accessToken) {
    return accessTokenUserMap.get(accessToken);
  }

  public void removeUserFromAccessTokenMap(String accessToken) {
    if (accessTokenUserMap.containsKey(accessToken)) {
      accessTokenUserMap.remove(accessToken);
    }
  }

  public boolean isTokenPresent(String token) {
    return tokenStore.contains(token);
  }

  public void removeTokenIfPresent(String token) {
    if (tokenStore.contains(token)) {
      tokenStore.remove(token);
    }
  }

  public void updateRefreshTokenAccessTokenMap(String refreshToken, String accessToken) {
    this.refreshTokenAccessTokenMap.put(refreshToken, accessToken);
  }

  public void removeRefreshTokenAccessTokenMap(String refreshToken) {
    if (this.refreshTokenAccessTokenMap.containsKey(refreshToken)) {
      this.refreshTokenAccessTokenMap.remove(refreshToken);
    }
  }

  public String getCurrentAccessTokenFromRefreshToken(String refreshToken) {
    return this.refreshTokenAccessTokenMap.get(refreshToken);
  }

  public void addToken(String token) {
    tokenStore.add(token);
  }

  public void removeRefreshToken(String refreshToken) {
    if (refreshTokenUserMap.containsKey(refreshToken)) {
      refreshTokenUserMap.remove(refreshToken);
    }
  }

  public AppUser getUserfromRefreshToken(String refreshToken) {
    return refreshTokenUserMap.get(refreshToken);
  }

  public String getRefreshTokenForUser(String emailId) {

    AppUser appUser = null;
    for (AppUser user : users) {
      if (user.getEmailId().equals(emailId)) {
        appUser = user;
        break;
      }
    }

    if (appUser == null) { //User is not present
      return null;
    }
    for (Map.Entry<String, AppUser> entry : refreshTokenUserMap.entrySet()) {
      if (entry.getValue().equals(appUser)) {
        return entry.getKey();
      }
    }

    return null;
  }

  public void addRefreshToken(String refreshToken, AppUser user) {
    this.refreshTokenUserMap.put(refreshToken, user);
  }

  @Override
  public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {

    for (AppUser appUser : users) {
      if (appUser.getEmailId().equals(emailId)) {

        List<GrantedAuthority> grantedAuthorities =
            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + appUser.getRole());

        // The "User" class is provided by Spring and represents a model class for user to be returned by UserDetailsService
        // And used by auth manager to verify and check user authentication.
        return new User(appUser.getUsername(), appUser.getPassword(), grantedAuthorities);
      }
    }

    // If user not found. Throw this exception.
    throw new UsernameNotFoundException("Username: " + emailId + " not found");
  }

  public void addUser(String emailId, String userName, String password, String refreshToken, String accessToken)
      throws Exception {

    for (AppUser appUser : users) {
      if (appUser.getEmailId().equals(emailId)) {
        throw new CustomException("EmailId " + emailId + " already registered", HttpStatus.UNPROCESSABLE_ENTITY);
      }
    }

    AppUser user = new AppUser(emailId, userName, encoder.encode(password), "USER");
    users.add(user);
    addRefreshToken(refreshToken, user);
    this.tokenStore.add(accessToken);

    updateRefreshTokenAccessTokenMap(refreshToken, accessToken);
    updateAccessTokenUserMap("", accessToken, user);
  }

  public AppUser getUserByEmailId(String emailId) {

    for (AppUser appUser : users) {
      if (appUser.getEmailId().equals(emailId)) {
        return appUser;
      }
    }

    return null;
  }

  public List<String> getTokenStore() {
    return tokenStore;
  }

  public void setTokenStore(List<String> tokenStore) {
    this.tokenStore = tokenStore;
  }
}



