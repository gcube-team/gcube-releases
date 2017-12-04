package org.gcube.data_catalogue.grsf_publish_ws.json.input.others;

import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.Common;
import org.gcube.datacatalogue.common.Constants;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Similar record information.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SimilarRecordBean {

	@JsonProperty(Constants.SIMILAR_RECORDS_BEAN_FIELD_URL)
	String url;

	@JsonProperty(Constants.SIMILAR_RECORDS_BEAN_FIELD_IDENTIFIER)
	String id;

	@JsonProperty(Constants.SIMILAR_RECORDS_BEAN_FIELD_DESCRIPTION)
	String description;

	@JsonProperty(Constants.SIMILAR_RECORDS_BEAN_FIELD_NAME)
	String name;

	public SimilarRecordBean() {
		super();
	}

	/**
	 * @param url
	 * @param id
	 * @param description
	 */
	public SimilarRecordBean(String url, String id, String description, String name) {
		super();
		this.url = url;
		this.id = id;
		this.description = description;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		
		// in json format
		JSONObject obj = new JSONObject();

		if(url != null && !url.isEmpty())
			obj.put("url", url);

		if(description != null && !description.isEmpty())
			obj.put("description", description);

		if(id != null && !id.isEmpty())
			obj.put("id", Common.cleanSemanticId(id));

		if(name != null && !name.isEmpty())
			obj.put("name", name);

		return obj.toJSONString();
	}
}
