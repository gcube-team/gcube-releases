package org.gcube.portal.databook.shared;

import java.io.Serializable;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Dec 2012
 *
 */
@SuppressWarnings("serial")
public class ApplicationProfile implements Serializable {
	
	private String key;
	private String name;
	private String description;
	private String imageUrl;
	private String scope;
	private String url;

	public ApplicationProfile() {
		super();
	}
	
	public ApplicationProfile(String key, String name, String description,	String imageUrl, String scope, String url) {
		super();
		this.key = key;
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
		this.scope = scope;
		this.url = url;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}	
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "ApplicationProfile [key=" + key + ", name=" + name + ", description="
				+ description + ", imageUrl=" + imageUrl + ", scope=" + scope
				+ ", url=" + url + "]";
	}	
}
