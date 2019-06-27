package org.gcube.portal.mailing.message;

/**
 * Represents an email recipient. The recipient contains:
 * <ul>
 * <li>The email address (see {@link EmailAddress})</li>
 * <li>The recipient type (the field of the email: to, cc, bcc)</li>
 * </ul>
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 *
 */
public class Recipient  {
	/**
	 * The recipient type
	 */
	private RecipientType type;
	
	/**
	 * The recipient address
	 */
	private EmailAddress address;

	/**
	 * The address is of the form "anyuser@anyhost.anydomain" or "Name LastName &lt;user@host.domain&gt;". 
	 * The recipient type is set to {@link RecipientType#TO}.
	 * 
	 * @param address the email address as described above
	 */
	public Recipient(String address) {
		this(new EmailAddress(address));
	}

	/**
	 * Initialize the recipient with the provided address. The recipient type is
	 * set to {@link RecipientType#TO}.
	 * 
	 * @param address
	 *            the email address
	 */
	public Recipient(EmailAddress address) {
		this(address, RecipientType.TO);
	}

	/**
	 * Initialize the recipient with the provided address and for the provided type.
	 * 
	 * @param address the email address
	 * @param type the recipient type see {@link RecipientType#TO}.
	 */
	public Recipient(EmailAddress address, RecipientType type) {
		super();
		this.address = address;
		this.type = type;
	}

	public EmailAddress getAddress() {
		return address;
	}

	public RecipientType getType() {
		return type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(address).append("(").append(type).append(")");
		return builder.toString();
	}
}
