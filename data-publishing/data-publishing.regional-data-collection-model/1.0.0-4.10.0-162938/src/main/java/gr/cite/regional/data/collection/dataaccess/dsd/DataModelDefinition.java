package gr.cite.regional.data.collection.dataaccess.dsd;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.regional.data.collection.dataaccess.constraints.ConstraintDefinition;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataModelDefinition {
	@JsonProperty("fields")
	private List<Field> fields;
	
	@JsonProperty("constraints")
	private Map<String, List<ConstraintDefinition>> constraints;
	
	public List<Field> getFields() {
		return fields;
	}
	
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public Map<String, List<ConstraintDefinition>> getConstraints() {
		return constraints;
	}
	
	public void setConstraints(Map<String, List<ConstraintDefinition>> constraints) {
		this.constraints = constraints;
	}
}
