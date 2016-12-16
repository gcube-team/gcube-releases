package it.eng.rdlab.soa3.config.beans;

public class DefaultValues 
{
	private String caCert,
					assertionSignatureValidation,
					assertionTimeValidation,
					assertionSourceUrl;

	public String getCaCert() {
		return caCert;
	}

	public void setCaCert(String caCert) {
		this.caCert = caCert;
	}

	public String getAssertionSignatureValidation() {
		return assertionSignatureValidation;
	}

	public void setAssertionSignatureValidation(String assertionSignatureValidation) {
		this.assertionSignatureValidation = assertionSignatureValidation;
	}

	public String getAssertionTimeValidation() {
		return assertionTimeValidation;
	}

	public void setAssertionTimeValidation(String assertionTimeValidation) {
		this.assertionTimeValidation = assertionTimeValidation;
	}

	public String getAssertionSourceUrl() {
		return assertionSourceUrl;
	}

	public void setAssertionSourceUrl(String assertionSourceUrl) {
		this.assertionSourceUrl = assertionSourceUrl;
	}
	
	
					

}
