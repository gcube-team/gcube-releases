package org.gcube.portlets.user.vremembers.shared;

import java.io.Serializable;
import java.util.Comparator;

@SuppressWarnings("serial")
public class BelongingUser implements Serializable, Comparable<BelongingUser> {
	private String username;
	private String fullName;
	private String avatarId;
	private String headline;
	private String institution;
	private String profileLink;
	private boolean hasPhoto;

	public BelongingUser(String username, String fullName, String avatarId,
			String headline, String institution, String profileLink, boolean hasPhoto) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.avatarId = avatarId;
		this.headline = headline;
		this.institution = institution;
		this.hasPhoto = hasPhoto;
		this.profileLink = profileLink;

	}

	public BelongingUser() { 
		super();
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public String getAvatarId() {
		return avatarId;
	}


	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}


	public String getHeadline() {
		return headline;
	}


	public void setHeadline(String headline) {
		this.headline = headline;
	}


	public String getInstitution() {
		return institution;
	}


	public void setInstitution(String institution) {
		this.institution = institution;
	}


	public boolean hasPhoto() {
		return hasPhoto;
	}

	public void setHasPhoto(boolean hasPhoto) {
		this.hasPhoto = hasPhoto;
	}



	public String getProfileLink() {
		return profileLink;
	}

	public void setProfileLink(String profileLink) {
		this.profileLink = profileLink;
	}


	@Override
	public int compareTo(BelongingUser o) {
		if (this.hasHeadline() && !o.hasHeadline()) {
			return -1;	
		}
		if (!this.hasHeadline() && o.hasHeadline())
			return 1;
		
		if ( (this.hasHeadline() && o.hasHeadline()) || ((!this.hasHeadline() && !o.hasHeadline())) ) {
			if (this.hasPhoto && !o.hasPhoto)
				return -1;
			if (!this.hasPhoto && o.hasPhoto)
				return 1;
			return 0;
		}		
		return 0;			
	}


	private boolean hasHeadline() {
		return (headline != null && headline.compareTo("") != 0);
	}

}
