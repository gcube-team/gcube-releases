package gr.cite.regional.data.collection.dataaccess.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class Properties {
	@JsonProperty("staticFields")
	private Set<String> staticFields = new HashSet<>();
	
	public Set<String> getStaticFields() {
		return staticFields;
	}
	
	public void setStaticFields(Set<String> staticFields) {
		this.staticFields = staticFields;
	}
}
