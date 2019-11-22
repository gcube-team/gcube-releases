package org.gcube.portlets.user.workspace.server.reader;

import java.io.Serializable;

/**
 * The Class ApplicationProfile.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jan 14, 2016
 */
public class ApplicationProfile implements Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = -8627579981996968828L;
	private String key;
	private String name;
	private String description;
	private String imageUrl;
	private String scope;
	private String url;

	/**
	 * Instantiates a new application profile.
	 */
	public ApplicationProfile() {
		super();
	}

	/**
	 * Instantiates a new application profile.
	 *
	 * @param key the key
	 * @param name the name
	 * @param description the description
	 * @param imageUrl the image url
	 * @param scope the scope
	 * @param url the url
	 */
	public ApplicationProfile(String key, String name, String description,	String imageUrl, String scope, String url) {
		super();
		this.key = key;
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
		this.scope = scope;
		this.url = url;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the image url.
	 *
	 * @return the image url
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * Sets the image url.
	 *
	 * @param imageUrl the new image url
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope the new scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ApplicationProfile [key=" + key + ", name=" + name + ", description="
				+ description + ", imageUrl=" + imageUrl + ", scope=" + scope
				+ ", url=" + url + "]";
	}
}