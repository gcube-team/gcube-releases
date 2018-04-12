package org.gcube.data_catalogue.grsf_publish_ws.json.output;

import org.gcube.datacatalogue.common.Constants;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean used to reply to a product creation/patch method.
 * @author Costantino Perciante at ISTI-CNR
 */
public class ResponseCreationBean {

	@JsonProperty(Constants.RESPONSE_CREATE_PATCH_ID)
	private String id;
	
	@JsonProperty(Constants.RESPONSE_CREATE_KNOWLEDGE_BASE_ID)
	private String kbUuid; // the original uuid given by the KB

	@JsonProperty(Constants.RESPONSE_CREATE_PRODUCT_URL)
	private String itemUrl;

	@JsonProperty(Constants.RESPONSE_CREATE_ERROR_MESSAGE)
	private String error; // in case of error

	public ResponseCreationBean() {
		super();
	}

	/**
	 * @param id
	 * @param kbUuid
	 * @param productUrl
	 * @param error
	 */
	public ResponseCreationBean(String id, String kbUuid, String itemUrl,
			String error) {
		super();
		this.id = id;
		this.kbUuid = kbUuid;
		this.itemUrl = itemUrl;
		this.error = error;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getKbUuid() {
		return kbUuid;
	}

	public void setKbUuid(String kbUuid) {
		this.kbUuid = kbUuid;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}

	@Override
	public String toString() {
		return "ResponseCreationBean [id=" + id + ", kbUuid=" + kbUuid
				+ ", itemUrl=" + itemUrl + ", error=" + error + "]";
	}

}