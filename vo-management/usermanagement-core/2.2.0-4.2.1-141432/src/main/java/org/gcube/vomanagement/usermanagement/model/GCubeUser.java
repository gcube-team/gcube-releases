package org.gcube.vomanagement.usermanagement.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * @author Massimiliano Assante, CNR-ISTI
 * 
 */
@SuppressWarnings("serial")
public class GCubeUser implements Serializable {
	private long userId;
	private String username;
	private String email;
	private String firstName;
	private String middleName;
	private String lastName;
	private String fullname;	
	private long registrationDate;
	private String userAvatarURL;
	private boolean male;
	private String jobTitle;
	private String location_industry;
	private Map<String,String> customAttrsMap;
	private List<Email> emailAddresses;
	/**
	 * constructor without custom attrs
	 * @param userId
	 * @param username
	 * @param email
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param fullname
	 * @param registrationDate
	 * @param userAvatarId
	 * @param male
	 * @param jobTitle
	 * @param customAttrsMap
	 * @param emailAddresses
	 */
	public GCubeUser(long userId, String username, String email,
			String firstName, String middleName, String lastName,
			String fullname, long registrationDate, String userAvatarURL,
			boolean male, String jobTitle, Map<String, String> customAttrsMap,
			List<Email> emailAddresses) {
		super();
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.fullname = fullname;
		this.registrationDate = registrationDate;
		this.userAvatarURL = userAvatarURL;
		this.male = male;
		this.jobTitle = jobTitle;
		this.customAttrsMap = customAttrsMap;
		this.emailAddresses = emailAddresses;
	}
	/**
	 * constructor without custom attrs
	 * @param userId
	 * @param username
	 * @param email
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param fullname
	 * @param registrationDate
	 * @param userAvatarId
	 * @param male
	 * @param jobTitle
	 * @param emailAddresses
	 */
	public GCubeUser(long userId, String username, String email,
			String firstName, String middleName, String lastName,
			String fullname, long registrationDate, String userAvatarId,
			boolean male, String jobTitle,
			List<Email> emailAddresses) {
		super();
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.fullname = fullname;
		this.registrationDate = registrationDate;
		this.userAvatarURL = userAvatarId;
		this.male = male;
		this.jobTitle = jobTitle;
		this.emailAddresses = emailAddresses;
	}
	/**
	 * constructor with location/industry also
	 * @param userId
	 * @param username
	 * @param email
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param fullname
	 * @param registrationDate
	 * @param userAvatarId
	 * @param male
	 * @param jobTitle
	 * @param emailAddresses
	 */
	public GCubeUser(long userId, String username, String email,
			String firstName, String middleName, String lastName,
			String fullname, long registrationDate, String userAvatarId,
			boolean male, String jobTitle, String location_Industry,
			List<Email> emailAddresses) {
		super();
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.fullname = fullname;
		this.registrationDate = registrationDate;
		this.userAvatarURL = userAvatarId;
		this.male = male;
		this.jobTitle = jobTitle;
		this.emailAddresses = emailAddresses;
		this.location_industry = location_Industry;
	}
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @deprecated
     * please use getUsername
	 * @return the username
	 */
	public String getScreenName() {
		return username;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(long registrationDate) {
		this.registrationDate = registrationDate;
	}
	@Deprecated
	public String getUserAvatarId() {
		return userAvatarURL;
	}
	@Deprecated
	public void setUserAvatarId(String userAvatarId) {
		this.userAvatarURL = userAvatarId;
	}
	/**
	 * @return the absolute path of the avatar URL comprising security token e.g. /image/user_male_portrait?img_id=12345&img_id_token=0GJ5WkeDV9F9bDZGkqzb7LahygM%3D&t=1457799199747
	 */
	public String getUserAvatarURL() {
		return userAvatarURL;
	}

	public void setUserAvatarURL(String userAvatarURL) {
		this.userAvatarURL = userAvatarURL;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public Map<String, String> getCustomAttrsMap() {
		return customAttrsMap;
	}
	public void setCustomAttrsMap(Map<String, String> customAttrsMap) {
		this.customAttrsMap = customAttrsMap;
	}
	public List<Email> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(List<Email> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	
	public String getLocation_industry() {
		return location_industry;
	}
	public void setLocation_industry(String location_industry) {
		this.location_industry = location_industry;
	}
	
	@Override
	public String toString() {
		return "GCubeUser [userId=" + userId + ", username=" + username
				+ ", email=" + email + ", firstName=" + firstName
				+ ", middleName=" + middleName + ", lastName=" + lastName
				+ ", fullname=" + fullname + ", registrationDate="
				+ registrationDate + ", userAvatarURL=" + userAvatarURL
				+ ", male=" + male + ", jobTitle=" + jobTitle
				+ ", location_industry=" + location_industry
				+ ", customAttrsMap=" + customAttrsMap + ", emailAddresses="
				+ emailAddresses + "]";
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GCubeUser other = (GCubeUser) obj;
		if (userId != other.userId)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}