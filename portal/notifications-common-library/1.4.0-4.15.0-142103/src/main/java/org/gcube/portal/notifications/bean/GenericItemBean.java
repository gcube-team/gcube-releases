package org.gcube.portal.notifications.bean;
import java.io.Serializable;

@SuppressWarnings("serial")
public class GenericItemBean implements Serializable {
	private String id;
	private String name;
	private String alternativeName;
	private String thumbnailURL;
	
	public GenericItemBean() {
		super();
	}
	public GenericItemBean(String id, String username, String fullName, String thumbnailURL) {
		super();
		this.id = id;
		this.name = username;
		this.alternativeName = fullName;
		this.thumbnailURL = thumbnailURL;
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
	public String getAlternativeName() {
		return alternativeName;
	}
	public void setAlternativeName(String altname) {
		this.alternativeName = altname;
	}
	
	public String getThumbnailURL() {
		return thumbnailURL;
	}
	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}
	@Override
	public String toString() {
		return "GenericItemBean [id=" + id + ", name=" + name
				+ ", alternativeName=" + alternativeName + ", thumbnailURL=" + thumbnailURL
				+ "]";
	}
	
}
