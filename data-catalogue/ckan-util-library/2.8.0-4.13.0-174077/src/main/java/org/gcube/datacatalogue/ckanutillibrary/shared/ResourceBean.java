package org.gcube.datacatalogue.ckanutillibrary.shared;

import java.io.Serializable;

/**
 * A bean that resembles the CKanResource bean object
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResourceBean implements Serializable {

	private static final long serialVersionUID = -5275448097250176185L;
	private String url;
	private String name;
	private String description;
	private String id;
	private String owner;
	private String datasetId;
	private String mimeType;

	public ResourceBean(){
		super();
	}

	/**
	 * @param url
	 * @param name
	 * @param description
	 * @param id
	 * @param owner
	 * @param datasetId
	 * @param mimeType
	 */
	public ResourceBean(String url, String name, String description, String id,
			String owner, String datasetId, String mimeType) {
		super();
		this.url = url;
		this.name = name;
		this.description = description;
		this.id = id;
		this.owner = owner;
		this.datasetId = datasetId;
		this.mimeType = mimeType;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the datasetId
	 */
	public String getDatasetId() {
		return datasetId;
	}

	/**
	 * @param datasetId the datasetId to set
	 */
	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResourceBean [url=" + url + ", name=" + name + ", description="
				+ description + ", id=" + id + ", owner=" + owner
				+ ", datasetId=" + datasetId + ", mimeType=" + mimeType + "]";
	}
}
