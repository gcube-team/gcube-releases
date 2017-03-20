package it.eng.rdlab.um.user.beans;

import it.eng.rdlab.um.beans.GenericModel;



/**
 * Minimal User Bean: it should be extended to manage the specific features
 * 
 */

public class UserModel extends GenericModel
{

	private String fullname;
	private char [] password;

	public UserModel ()
	{
		super ();
		this.fullname = "";
		this.password = new char []{' '};
	}

	public UserModel(String userId, String fullname) 
	{
		super (userId);
		this.fullname= fullname;		
	}
	
	public String getUserId() 
	{
		String userId = super.getId();
		return userId != null ? userId : "";
	}
	
	public void setUserId(String userId) 
	{
		super.setId(userId);
	}

	public String getFullname() 
	{
		return fullname;
	}
	
	public void setFullname(String fullname) 
	{
		this.fullname = fullname;
	}

	public void setPassword (char [] password)
	{
		this.password = password;
	}
	
	public char [] getPassword ()
	{
		return this.password;
	}
	


}