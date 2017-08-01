package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Similar record information.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SimilarRecordBean {

	@JsonProperty("url")
	String url;

	@JsonProperty("id")
	String id;

	@JsonProperty("description")
	String description;

	public SimilarRecordBean() {
		super();
	}

	/**
	 * @param url
	 * @param id
	 * @param description
	 */
	public SimilarRecordBean(String url, String id, String description) {
		super();
		this.url = url;
		this.id = id;
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		String toReturn = "";
		toReturn += url != null ? "url = " + url + ", " : "";
		toReturn += id != null ? "id = " + id + ", " : "";
		toReturn += description != null ? "description = " + description : "";
		toReturn = toReturn.endsWith(", ") ? toReturn.substring(0, toReturn.length() - 2) : toReturn;
		return toReturn;
	}
}
