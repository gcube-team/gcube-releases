package gr.cite.regional.data.collection.dataaccess.constraints;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EntityNoDuplicatesConstraint extends ConstraintDefinition {
	@JsonProperty("fields")
	private List<FieldDto> fields;

	public List<FieldDto> getFields() {
		return fields;
	}

	public void setFields(List<FieldDto> fields) {
		this.fields = fields;
	}
}
