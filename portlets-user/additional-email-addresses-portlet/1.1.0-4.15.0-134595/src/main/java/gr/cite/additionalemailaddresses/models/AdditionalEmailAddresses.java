package gr.cite.additionalemailaddresses.models;

import java.util.List;

/**
 * @author mnikolopoulos
 *
 */
public class AdditionalEmailAddresses {

	private List<Email> emailAddresses;

	public List<Email> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(List<Email> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
}
