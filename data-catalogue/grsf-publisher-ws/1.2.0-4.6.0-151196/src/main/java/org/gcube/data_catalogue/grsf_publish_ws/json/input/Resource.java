package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Sources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A resource object bean. The generic argument T applies to the resource's name.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 * @param <T> the resource's name type
 */
@JsonIgnoreProperties(ignoreUnknown = true) // ignore in serialization/deserialization
public class Resource<T> {

	@JsonProperty("url")
	@NotNull(message="'url' field of a resource cannot be null")
	@Size(min=1, message="'url' field of a resource cannot be empty")
	private String url;

	@JsonProperty("description")
	private String description;

	@JsonProperty("name")
	@NotNull(message="'name' field of a resource cannot be null")
	private T name;

	public Resource() {
		super();
	}

	public Resource(String url, String description, T name) {
		super();
		this.url = url;
		this.description = description;
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public T getName() {
		return name;
	}

	public void setName(T name) {
		this.name = name;
	}

	@Override
	public String toString() {

		// in case of @Tag/@Group, we check the class of the element Name
		Class<? extends Object> nameClass = name.getClass();

		if(nameClass.equals(Sources.class))
			return name.toString();

		return "Resource [url=" + url + ", description=" + description
				+ ", name=" + name + "]";
	}	

}
