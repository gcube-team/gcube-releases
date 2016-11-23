package org.gcube.vomanagement.occi.datamodel.security;

public class UsernamePasswordCredentials extends Credentials {

  private String username;
  private String password;

  public UsernamePasswordCredentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

}
