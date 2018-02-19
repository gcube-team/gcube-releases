package gr.cite.regional.data.collection.dataaccess.dsd;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Codelist {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("fields")
	private List<String> fields;
	
	@JsonProperty("values")
	private List<List<String>> values;
	
	@JsonProperty
	private List<Code> codes;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public List<String> getFields() {
		return fields;
	}
	
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	public List<List<String>> getValues() {
		return values;
	}
	
	public void setValues(List<List<String>> values) {
		this.values = values;
	}
	
	public List<Code> getCodes() {
		return codes;
	}
	
	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}
}
