package gr.cite.google.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author mnikolopoulos
 * 
 * This is the data we deserialize, in case we need more/different data from the google,
 * we can check their API and make the appropriate changes here and maybe to the the actual call
 * and to the scope as well if needed.
 *
 */
public class GoogleJson {

	private String email;
	@SerializedName("given_name")
	private String givenName;
	@SerializedName("family_name")
	private String familyName;
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	
}
