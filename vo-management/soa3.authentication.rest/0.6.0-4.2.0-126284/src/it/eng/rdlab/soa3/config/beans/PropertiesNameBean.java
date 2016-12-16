package it.eng.rdlab.soa3.config.beans;

public class PropertiesNameBean 
{
	private  String 		ldapUrl,
							ldapBase,
							ldapDN,
							ldapPwd,
							caCert,
							assertionSignatureValidation,
							assertionTimeValidation,
							assertionSourceUrl;

	public String getLdapUrl() {
		return ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public String getLdapBase() {
		return ldapBase;
	}

	public void setLdapBase(String ldapBase) {
		this.ldapBase = ldapBase;
	}

	public String getLdapDN() {
		return ldapDN;
	}

	public void setLdapDN(String ldapDN) {
		this.ldapDN = ldapDN;
	}

	public String getLdapPwd() {
		return ldapPwd;
	}

	public void setLdapPwd(String ldapPwd) {
		this.ldapPwd = ldapPwd;
	}

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
