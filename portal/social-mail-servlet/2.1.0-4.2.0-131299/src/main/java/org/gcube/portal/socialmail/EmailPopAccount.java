package org.gcube.portal.socialmail;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EmailPopAccount implements Serializable {
	private String portalName; 
	private String pop3Server; 
	private String pop3user;
	private String pop3password;
	
	
	public EmailPopAccount() { 
		this.portalName = "";
		this.pop3Server = "";
		this.pop3user = "";
		this.pop3password = "";
	}
	
	public EmailPopAccount(String portalName, String pop3Server,
			String pop3user, String pop3password) {
		super();
		this.portalName = portalName;
		this.pop3Server = pop3Server;
		this.pop3user = pop3user;
		this.pop3password = pop3password;
	}
	public String getPortalName() {
		return portalName;
	}
	public void setPortalName(String portalName) {
		this.portalName = portalName;
	}
	public String getPop3Server() {
		return pop3Server;
	}
	public void setPop3Server(String pop3Server) {
		this.pop3Server = pop3Server;
	}
	public String getPop3user() {
		return pop3user;
	}
	public void setPop3user(String pop3user) {
		this.pop3user = pop3user;
	}
	public String getPop3password() {
		return pop3password;
	}
	public void setPop3password(String pop3password) {
		this.pop3password = pop3password;
	}
	
}
