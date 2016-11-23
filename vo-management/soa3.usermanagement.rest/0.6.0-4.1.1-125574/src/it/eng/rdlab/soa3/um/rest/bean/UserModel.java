package it.eng.rdlab.soa3.um.rest.bean;

/**
 * This class models user information
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
public class UserModel 
{
	private String userId;
	private String fullname;
	private String password;
	private String email;
	private String screenName;
	private String firstname;
	private String lastname;
	private String certDN;
	
	
	public UserModel(){
		
	}
	
	public UserModel(String userId, String firstname, String lastname, String password){
		this.userId = userId;
		this.screenName = userId;
		this.fullname = firstname + " " +  lastname;
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
	}

	public String getUserId() 
	{
		return userId;
	}

	public void setUserId(String userId) 
	{
		this.userId = userId;
	}

	public String getFullname() 
	{
		return fullname;
	}

	public void setFullname(String fullname) 
	{
		this.fullname = fullname;
	}

	public String getEmail() 
	{
		return email;
	}

	public void setEmail(String email) 
	{
		this.email = email;
	}

	public String getScreenName() 
	{
		return screenName;
	}

	public void setScreenName(String screenName) 
	{
		this.screenName = screenName;
	}

	public String getFirstname() 
	{
		return firstname;
	}

	public void setFirstname(String firstname) 
	{
		this.firstname = firstname;
	}

	public String getLastname() 
	{
		return lastname;
	}

	public void setLastname(String lastname) 
	{
		this.lastname = lastname;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}

	public String getCertDN() {
		return certDN;
	}

	public void setCertDN(String certDN) {
		this.certDN = certDN;
	}

	@Override
	public int hashCode() 
	{
		return this.userId.hashCode();
	}
	
	
	

}
