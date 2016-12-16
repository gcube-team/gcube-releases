package gr.cite.additionalemailaddresses.models;

import com.liferay.portal.model.EmailAddress;

/**
 * @author mnikolopoulos
 *
 */
public class Email {

	public enum Status {
		ACTIVE((short) 0), INACTIVE((short) 1), UNKOWN((short)2);

		private final short code;

		private Status(short code) {
			this.code = code;
		}

		public short code() {
			return this.code;
		}

	}

	private String Email;
	private Status status;
	//private Boolean isPrimary;
	private long id;
	
	public Email(long id, EmailAddress emailAddress, Status status) {
		this.Email = emailAddress.getAddress();
		this.status = status;
	//	this.isPrimary = isPrimary;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

//	public void setIsPrimary(Boolean isPrimary) {
//		this.isPrimary = isPrimary;
//	}
//	
//	public Boolean getIsPrimary() {
//		return isPrimary;
//	}
	
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	
}
