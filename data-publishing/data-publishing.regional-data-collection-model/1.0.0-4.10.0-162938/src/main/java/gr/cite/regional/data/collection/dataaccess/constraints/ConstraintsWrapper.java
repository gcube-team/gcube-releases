package gr.cite.regional.data.collection.dataaccess.constraints;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConstraintsWrapper {
	@JsonProperty("activeDataCollectionperiod")
	boolean activeDataCollectionperiod;
	@JsonProperty("constraints")
	List<ConstraintDefinition> constraints;
	public ConstraintsWrapper(boolean activeDataCollectionperiod, List<ConstraintDefinition> constraints) {
		super();
		this.activeDataCollectionperiod = activeDataCollectionperiod;
		this.constraints = constraints;
	}
	public boolean isActiveDataCollectionperiod() {
		return activeDataCollectionperiod;
	}
	public void setActiveDataCollectionperiod(boolean activeDataCollectionperiod) {
		this.activeDataCollectionperiod = activeDataCollectionperiod;
	}
	public List<ConstraintDefinition> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<ConstraintDefinition> constraints) {
		this.constraints = constraints;
	}
}
