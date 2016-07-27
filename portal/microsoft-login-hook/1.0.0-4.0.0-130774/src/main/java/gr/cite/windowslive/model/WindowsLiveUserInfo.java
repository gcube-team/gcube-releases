package gr.cite.windowslive.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author mnikolopoulos
 *
 */
public class WindowsLiveUserInfo {
	
	public static class Emails{
		
		private String preferred;
		private String account;
		
		public Emails(String preferred, String account) {
			super();
			this.preferred = preferred;
			this.account = account;
		}
		
		public String getPreferred() {
			return preferred;
		}
		
		public void setPreferred(String preferred) {
			this.preferred = preferred;
		}
		
		public String getAccount() {
			return account;
		}
		
		public void setAccount(String account) {
			this.account = account;
		}
		
	}
	
	private String id;
	
	@SerializedName("first_name")
	private String firstName;
	
	@SerializedName("last_name")
	private String lastName;
	
	private Emails emails;
	
	
	
	public WindowsLiveUserInfo(String id, String firstName, String lastName, Emails emails) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emails = emails;
	}

	public Emails getEmails() {
		return emails;
	}

	public void setEmails(Emails emails) {
		this.emails = emails;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
