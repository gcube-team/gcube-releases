package org.gcube.portlets.user.shareupdates.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
/**
 * This class is used for link preview (and when a file is attached)
 * @author Costantino Perciante at ISTI-CNR
 *
 */
public class LinkPreview implements Serializable {
	
	private String title; //in case of a file, it is the name of the file itself
	private String description;
	private String url;
	private String host;
	private ArrayList<String> imageUrls; // in case of a file, the first element is the image related to the format (pdf, jpg..)
	
	
	public LinkPreview() {
		super();
	}
	
	public LinkPreview(String title, String description, String url,
			String host, ArrayList<String> imageUrls) {
		super();
		this.title = title;
		this.description = description;
		this.url = url;
		this.host = host;
		this.imageUrls = imageUrls;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ArrayList<String> getImageUrls() {
		return imageUrls;
	}
	public void setImageUrls(ArrayList<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	@Override
	public String toString() {
		return "LinkPreview [title=" + title + ", description=" + description
				+ ", url=" + url + ", host=" + host + ", imageUrls="
				+ imageUrls + "]";
	}	
	
	
}
