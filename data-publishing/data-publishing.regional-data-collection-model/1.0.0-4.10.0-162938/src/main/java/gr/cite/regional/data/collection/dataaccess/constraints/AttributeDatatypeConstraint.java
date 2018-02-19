package gr.cite.regional.data.collection.dataaccess.constraints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AttributeDatatypeConstraint extends ConstraintDefinition {
	
	public enum DataType {
		DATE("date"),
		DECIMAL("decimal"),
		INTEGER("integer"),
		STRING("string");
		
		private final String dataType;
		
		private DataType(final String dataType) {
			this.dataType = dataType;
		}
		
		@JsonValue
		public String toValue() {
			return this.dataType;
		}
		
		@JsonCreator
		public static DataType fromValue(String value) {
			switch (value.toLowerCase()) {
				case "date":
					return DataType.DATE;
				case "decimal":
					return DataType.DECIMAL;
				case "integer":
					return DataType.INTEGER;
				case "string":
					return DataType.STRING;
				default:
					return DataType.INTEGER;
			}
		}
	}
	
	@JsonProperty("fieldId")
	private String fieldId;
	
	@JsonProperty("field")
	private String field;
	
	@JsonProperty("datatype")
	private DataType datatype;
	
	@JsonProperty("min")
	private String min;
	
	@JsonProperty("max")
	private String max;
	
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
	
	public DataType getDatatype() {
		return datatype;
	}
	
	public void setDatatype(DataType datatype) {
		this.datatype = datatype;
	}
	
	public String getMin() {
		return min;
	}
	
	public void setMin(String min) {
		this.min = min;
	}
	
	public String getMax() {
		return max;
	}
	
	public void setMax(String max) {
		this.max = max;
	}
}
