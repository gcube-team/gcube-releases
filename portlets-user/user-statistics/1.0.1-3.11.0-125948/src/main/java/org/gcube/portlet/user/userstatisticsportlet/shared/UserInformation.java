package org.gcube.portlet.user.userstatisticsportlet.shared;

import java.io.Serializable;

/**
 * Some information of the current user
 * 
 * @author Costantino Perciante at ISTI-CNR
 */
public class UserInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6360825683813448487L;

	private boolean isRoot;
	private String urlAvatar;
	private String displayName;
	private String actualVre;
	private String accountURL;

	public UserInformation(){
		super();	
	}

	public UserInformation(boolean isRoot, String urlAvatar,
			String displayName, String actualVre, String accountURL) {
		super();
		this.isRoot = isRoot;
		this.urlAvatar = urlAvatar;
		this.displayName = displayName;
		this.actualVre = actualVre;
		this.accountURL = accountURL;
	}


	public boolean isRoot() {
		return isRoot;
	}


	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}


	public String getUrlAvatar() {
		return urlAvatar;
	}


	public void setUrlAvatar(String urlAvatar) {
		this.urlAvatar = urlAvatar;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public String getActualVre() {
		return actualVre;
	}


	public void setActualVre(String actualVre) {
		this.actualVre = actualVre;
	}



	public String getAccountURL() {
		return accountURL;
	}



	public void setAccountURL(String accountURL) {
		this.accountURL = accountURL;
	}



	@Override
	public String toString() {
		return "UserInformation [isRoot=" + isRoot + ", urlAvatar=" + urlAvatar
				+ ", displayName=" + displayName + ", actualVre=" + actualVre
				+ ", accountURL=" + accountURL + "]";
	}

}
