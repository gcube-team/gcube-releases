package org.gcube.portal.social.networking.ws.inputs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Recipient message bean
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true) // ignore in serialization/deserialization
@ApiModel(description="A recipient object")
public class Recipient implements Serializable{

	private static final long serialVersionUID = 1071412144446514138L;

	@JsonProperty("id")
	@NotNull(message="recipient id must not be null")
	@Size(min=1, message="recipient id must not be empty")
	@ApiModelProperty(
			example="andrea.rossi", 
			required=true,
			value="The id of the recipient")
	private String id;

	public Recipient() {
		super();
	}
	public Recipient(String id) {
		super();
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
		return "Recipient [id=" + id + "]";
	}
}
