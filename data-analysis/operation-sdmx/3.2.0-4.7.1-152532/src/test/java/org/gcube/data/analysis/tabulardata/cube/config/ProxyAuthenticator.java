package org.gcube.data.analysis.tabulardata.cube.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuthenticator extends Authenticator
{
	private String 	proxyHost,
					proxyPort,	
					proxyUserName,
					proxyPassword;
	
	private PasswordAuthentication passwordAuthentication;
	
	
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
	
	
	public void configure ()
	{
		if (this.proxyHost != null) System.setProperty("http.proxyHost", this.proxyHost);
		
		if (this.proxyPort != null) System.setProperty("http.proxyPort", this.proxyPort);
		
		if (this.proxyUserName != null && this.proxyPassword != null)
		{
			this.passwordAuthentication = new PasswordAuthentication(proxyUserName, proxyPassword.toCharArray());
		}
		
	}
	
	
	public boolean isActive ()
	{
		return this.passwordAuthentication != null;
	}
	

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		
		if (this.passwordAuthentication != null) return this.passwordAuthentication;
		else return super.getPasswordAuthentication();
	}

}
