package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean to be used for input of delete-product methods
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(ignoreUnknown = true) // ignore in serialization/deserialization
public class DeleteProductBean {

	@JsonProperty("id")
	@NotNull(message="id cannot be null")
	@Size(min=1, message="id cannot be empty")
	private String id;

	public DeleteProductBean() {
		super();
	}

	/**
	 * Create a product deleted bean for the product that had the id 'id'
	 * @param id
	 */
	public DeleteProductBean(String id) {
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
