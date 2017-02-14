package org.gcube.datacatalogue.ckanutillibrary.server.models;

import java.io.Serializable;

/**
 * A CKan user.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CKanUserWrapper implements Serializable{

	private static final long serialVersionUID = 6264706263035722775L;
	
	private String id;
	private String name;
	private String apiKey;
	private long creationTimestamp;
	private String about;
	private String openId;
	private String fullName;
	private String email;
	private boolean isAdmin;


	public CKanUserWrapper() {
		super();
	}

	/** Create a ckan user object.
	 * @param id
	 * @param name
	 * @param apiKey
	 * @param creationTimestamp
	 * @param about
	 * @param openId
	 * @param fullName
	 * @param email
	 * @param isAdmin
	 */
	public CKanUserWrapper(String id, String name, String apiKey,
			long creationTimestamp, String about, String openId,
			String fullName, String email, boolean isAdmin) {
		super();
		this.id = id;
		this.name = name;
		this.apiKey = apiKey;
		this.creationTimestamp = creationTimestamp;
		this.about = about;
		this.openId = openId;
		this.fullName = fullName;
		this.email = email;
		this.isAdmin = isAdmin;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public String toString() {
		return "CKanUserExtended [id=" + id + ", name=" + name + ", apiKey=" + apiKey.substring(0, 5) + "****************"
				+ ", creationTimestamp=" + creationTimestamp + ", about="
				+ about + ", openId=" + openId + ", fullName=" + fullName
				+ ", email=" + email + ", isAdmin=" + isAdmin + "]";
	}

}
