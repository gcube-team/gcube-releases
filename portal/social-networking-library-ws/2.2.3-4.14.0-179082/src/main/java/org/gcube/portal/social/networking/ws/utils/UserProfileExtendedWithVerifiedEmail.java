package org.gcube.portal.social.networking.ws.utils;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileExtendedWithVerifiedEmail {

	@JsonProperty("id")
	private String username;

	@JsonProperty("roles")
	private List<String> roles;

	@JsonProperty("picture")
	private String avatar;

	@JsonProperty("name")
	private String fullname;
	
	@JsonProperty("middle_name")
	private String middleName;

	@JsonProperty("male")
	private boolean male;

	@JsonProperty("location_industry")
	private String locationIndustry;

	@JsonProperty("given_name")
	private String firstName;

	@JsonProperty("email")
	private String email;

	@JsonProperty("job_title")
	private String jobTitle;

	@JsonProperty("family_name")
	private String lastName;

	@JsonProperty("verified_email")
	private boolean verifiedEmail = true;

	public UserProfileExtendedWithVerifiedEmail() {
		super();
	}

	/**
	 * @param username
	 * @param roles
	 * @param avatar
	 * @param fullname
	 */
	public UserProfileExtendedWithVerifiedEmail(String username, List<String> roles, String avatar, String fullname) {
		this.username = username;
		this.roles = roles;
		this.avatar = avatar;
		this.fullname = fullname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getLocationIndustry() {
		return locationIndustry;
	}

	public void setLocationIndustry(String locationIndustry) {
		this.locationIndustry = locationIndustry;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isVerifiedEmail() {
		return verifiedEmail;
	}

	public void setVerifiedEmail(boolean verifiedEmail) {
		this.verifiedEmail = verifiedEmail;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserProfileExtendedWithVerifiedEmail [username=");
		builder.append(username);
		builder.append(", roles=");
		builder.append(roles);
		builder.append(", avatar=");
		builder.append(avatar);
		builder.append(", fullname=");
		builder.append(fullname);
		builder.append(", middleName=");
		builder.append(middleName);
		builder.append(", male=");
		builder.append(male);
		builder.append(", locationIndustry=");
		builder.append(locationIndustry);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", email=");
		builder.append(email);
		builder.append(", jobTitle=");
		builder.append(jobTitle);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", verifiedEmail=");
		builder.append(verifiedEmail);
		builder.append("]");
		return builder.toString();
	}

	

	
}
