package gr.cite.regional.data.collection.dataaccess.constraints;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AttributeMandatoryConstraint extends ConstraintDefinition {
	
	@JsonProperty("fieldId")
	private String fieldId;
	
	@JsonProperty("field")
	private String field;
	
	public String getFieldId() {
		return fieldId;
	}
	
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	
	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
}
