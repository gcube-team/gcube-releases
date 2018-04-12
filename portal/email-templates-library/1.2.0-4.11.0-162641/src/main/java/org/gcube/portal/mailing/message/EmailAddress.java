package org.gcube.portal.mailing.message;

/**
 * Represents an email address.
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class EmailAddress {
	/**
	 * The email address part (is of the form "user@domain.host")
	 */
	private String address;

	/**
	 * The user name part (can be anything)
	 */
	private String personal;
	/**
	 * Initialize the address with the email address and the personal parts.
	 * 
	 * @param address the email address part, it is of the form "user@domain.host"
	 * @param personal the personal part
	 */
	public EmailAddress(String address, String personal) {
		super();
		this.address = address;
		this.personal = personal;
	}
	/**
	 * Initialize the address with only the email address part. 
	 * It is of the form "anyuser@anydomain.anyhost".
	 * 
	 * @param address the email address part
	 */
	public EmailAddress(String address) {
		this(address, null);
	}	

	public String getAddress() {
		return address;
	}

	public String getPersonal() {
		return personal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (personal != null && !personal.isEmpty()) {
			builder.append(personal).append(" ");
		}
		builder.append("<").append(address).append(">");
		return builder.toString();
	}
}
