package org.gcube.data_catalogue.grsf_publish_ws.json.input.others;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.datacatalogue.common.Constants;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean to be used for input of delete-product methods
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(ignoreUnknown = true) // ignore in serialization/deserialization
public class DeleteRecord {

	@JsonProperty(Constants.DELETE_RECORD_ID)
	@NotNull(message= Constants.DELETE_RECORD_ID + " cannot be null")
	@Size(min=1, message= Constants.DELETE_RECORD_ID + " cannot be empty")
	private String id;

	public DeleteRecord() {
		super();
	}

	/**
	 * Create a product deleted bean for the product that had the id 'id'
	 * @param id
	 */
	public DeleteRecord(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "DeleteProductBean [id=" + id + "]";
	}
}
