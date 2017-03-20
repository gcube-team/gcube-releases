package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * RefersToBean are used into aggregated records but not into the sources.
 * To check if a product is a source or an original record.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefersToBean {
	
	@JsonProperty("url")
	@NotNull(message="url of field refers_to cannot be null")
	String url;
	
	@JsonProperty("id")
	@NotNull(message="id of field refers_to cannot be null")
	String id;

	public RefersToBean() {
		super();
	}

	/** Create a refers to bean
	 * @param url
	 * @param id
	 */
	public RefersToBean(String url, String id) {
		super();
		this.url = url;
		this.id = id;
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

	@Override
	public String toString() {
		return "url=" + url + ", id=" + id;
	}

}
