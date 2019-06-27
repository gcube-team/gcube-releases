package gr.cite.geoanalytics.web.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserProfile {
    private String username;
    private String fullname;
    private String email;
    private String uri;
    private List<String> roles;

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getFullname() { return fullname; }

    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getUri() { return uri; }

    public void setUri(String uri) { this.uri = uri; }

    public List<String> getRoles() { return roles; }

    public void setRoles(List<String> roles) { this.roles = roles; }
}
