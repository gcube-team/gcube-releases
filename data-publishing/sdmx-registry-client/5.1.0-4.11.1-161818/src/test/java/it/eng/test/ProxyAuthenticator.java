package it.eng.test;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuthenticator extends Authenticator {

	
	private String proxyUserName,
	proxyPassword;
	
	private PasswordAuthentication passwordAuthentication;
	


	
	public boolean isActive ()
	{
		return this.passwordAuthentication != null;
	}
	

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		
		if (this.passwordAuthentication != null) return this.passwordAuthentication;
		else return super.getPasswordAuthentication();
	}
	
	
	public void configure ()
	{
		
		if (this.proxyUserName != null && this.proxyPassword != null)
		{
			this.passwordAuthentication = new PasswordAuthentication(proxyUserName, proxyPassword.toCharArray());
		}
		
	}
	

	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
	
}
