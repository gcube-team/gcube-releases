package org.gcube.portlet.user.userstatisticsportlet.shared;

import java.io.Serializable;

/**
 * Some information of the current user
 * 
 * @author Costantino Perciante at ISTI-CNR
 */
public class UserInformation implements Serializable {

	private static final long serialVersionUID = 6360825683813448487L;

	private boolean isRoot;
	private String urlAvatar;
	private String aslSessionUsername;
	private String actualVre;
	private boolean isOwner; 
							// this value will be set to true when the portlet is deployed in the user profile page
							// and the user that visits the page is the owner of the profile
							// if it is deployed in a vre or in the root infrastructure, it will be set to false 
							// of course, the same is true if the user that visits the profile is not the owner
	
	private boolean isProfileShowable = true; 
							// this field is used when a user visit a user profile page.
							// If the visiting user is the owner of the page, there is no proble
							// if the visiting user is not the owner and the real owner doesn't want
							// to show the portlet to the other users, we need to hide the statistics.
	
	private String currentPageLanding;
 
	public UserInformation(){
		super();	
	}

	public UserInformation(boolean isRoot, String urlAvatar,
			String aslSessionUsername, String actualVre, boolean isOwner, boolean isProfileShowable) {
		super();
		this.isRoot = isRoot;
		this.urlAvatar = urlAvatar;
		this.aslSessionUsername = aslSessionUsername;
		this.actualVre = actualVre;
		this.isOwner = isOwner;
		this.isProfileShowable = isProfileShowable;
	}
	
	public String getCurrentPageLanding() {
		return currentPageLanding;
	}

	public void setCurrentPageLanding(String currentPageLanding) {
		this.currentPageLanding = currentPageLanding;
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

	public String getAslSessionUsername() {
		return aslSessionUsername;
	}

	public void setAslSessionUsername(String aslSessionUsername) {
		this.aslSessionUsername = aslSessionUsername;
	}

	public String getActualVre() {
		return actualVre;
	}


	public void setActualVre(String actualVre) {
		this.actualVre = actualVre;
	}
	
	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
	
	public boolean isProfileShowable() {
		return isProfileShowable;
	}

	public void setProfileShowable(boolean isProfileShowable) {
		this.isProfileShowable = isProfileShowable;
	}

	@Override
	public String toString() {
		return "UserInformation [isRoot=" + isRoot + ", urlAvatar=" + urlAvatar
				+ ", aslSessionUsername=" + aslSessionUsername + ", actualVre="
				+ actualVre + ", isOwner=" + isOwner + ", isProfileShowable="
				+ isProfileShowable + ", currentPageLanding="
				+ currentPageLanding + "]";
	}
}
