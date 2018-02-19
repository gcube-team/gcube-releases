package gr.cite.regional.data.collection.dataaccess.dsd;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Code {
	
	@JsonProperty("id")
	private String id;
	
	private Map<String, String> fields = new HashMap<>();
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@JsonAnyGetter
	public Map<String, String> getFields() {
		return fields;
	}
	
	@JsonAnySetter
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
}
