package org.gcube.portal.trainingmodule.dao;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.eclipse.persistence.annotations.CascadeOnDelete;


@Entity
@CascadeOnDelete
public class TrainingVideo implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3347996712390736940L;

	/** The interna id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long internalId;
	
	private String title;
	
	@Lob
	private String description;
	
	private String url;
	
	
	public TrainingVideo() {
	}


	public TrainingVideo(long internalId, String title, String description, String url) {
		super();
		this.internalId = internalId;
		this.title = title;
		this.description = description;
		this.url = url;
	}


	public long getInternalId() {
		return internalId;
	}


	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}


	public String getTitle() {
		return title;
	}


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


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingVideoDTO [internalId=");
		builder.append(internalId);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
	
	

}
