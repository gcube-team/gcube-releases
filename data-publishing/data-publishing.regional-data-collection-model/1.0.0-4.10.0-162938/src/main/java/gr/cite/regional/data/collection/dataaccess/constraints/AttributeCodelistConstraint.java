package gr.cite.regional.data.collection.dataaccess.constraints;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AttributeCodelistConstraint extends ConstraintDefinition {
	
	@JsonProperty("fieldId")
	private String fieldId;
	
	@JsonProperty("field")
	private String field;
	
	@JsonProperty("codelistId")
	private String codelistId;
	
	@JsonProperty("codelist")
	private String codelist;
	
	@JsonProperty("displayField")
	private String displayField;
	
	@JsonProperty("persistField")
	private String persistField;
	
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
	
	public String getCodelistId() {
		return codelistId;
	}
	
	public void setCodelistId(String codelistId) {
		this.codelistId = codelistId;
	}
	
	public String getCodelist() {
		return codelist;
	}
	
	public void setCodelist(String codelist) {
		this.codelist = codelist;
	}
	
	public String getDisplayField() {
		return displayField;
	}
	
	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}
	
	public String getPersistField() {
		return persistField;
	}
	
	public void setPersistField(String persistField) {
		this.persistField = persistField;
	}
	
}
