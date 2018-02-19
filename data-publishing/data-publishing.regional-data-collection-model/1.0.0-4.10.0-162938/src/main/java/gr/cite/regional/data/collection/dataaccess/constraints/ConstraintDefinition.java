package gr.cite.regional.data.collection.dataaccess.constraints;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "constraintType", visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = AttributeCodelistConstraint.class, name = ConstraintTypes.ATTRIBUTE_CODELIST),
		@JsonSubTypes.Type(value = AttributeCodelistSubsetConstraint.class, name = ConstraintTypes.ATTRIBUTE_CODELIST_SUBSET),
		@JsonSubTypes.Type(value = AttributeDatatypeConstraint.class, name = ConstraintTypes.ATTRIBUTE_DATATYPE),
		@JsonSubTypes.Type(value = AttributeMandatoryConstraint.class, name = ConstraintTypes.ATTRIBUTE_MANDATORY),
		@JsonSubTypes.Type(value = EntityNoDuplicatesConstraint.class, name = ConstraintTypes.ENTITY_NO_DUPLICATES)
})

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class ConstraintDefinition {
	
	public enum Trigger {
		ON_CHANGE("OnChange"),
		ON_VALIDATE("OnValidate");
		
		private final String trigger;
		private Trigger(final String trigger) {
			this.trigger = trigger;
		}
		
		@JsonValue
		public String toValue() {
			return this.trigger;
		}
		
		@JsonCreator
		public static Trigger fromValue(String value) {
			switch (value) {
				case "OnChange":
					return Trigger.ON_CHANGE;
				case "OnValidate":
					return Trigger.ON_VALIDATE;
				default:
					return Trigger.ON_VALIDATE;
			}
		}
		
	}
	
	@JsonProperty("id")
	private Integer id;
	
	//@JsonProperty("constraintType")
	private String constraintType;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("trigger")
	private List<Trigger> trigger = new ArrayList<>();
	
	@JsonProperty("errorMessage")
	private String errorMessage;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getConstraintType() {
		return constraintType;
	}
	
	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public List<Trigger> getTrigger() {
		return trigger;
	}

	public void setTrigger(List<Trigger> trigger) {
		this.trigger = trigger;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
