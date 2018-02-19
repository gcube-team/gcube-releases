package gr.cite.regional.data.collection.application.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FieldDetails implements Dto {
	@JsonProperty("codelistId")
	private String codelistId;
	
	@JsonProperty("codelistLabel")
	private String codelistLabel;
	
	@JsonProperty("fields")
	private List<String> fields;

	public String getCodelistId() {
		return codelistId;
	}

	public void setCodelistId(String codelistId) {
		this.codelistId = codelistId;
	}

	public String getCodelistLabel() {
		return codelistLabel;
	}

	public void setCodelistLabel(String codelistLabel) {
		this.codelistLabel = codelistLabel;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
}
