package org.gcube.portlets.user.td.gwtservice.shared.tr.table;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.tr.ConditionCode;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class Validations implements Serializable {
	private static final long serialVersionUID = 4950002331717895999L;
	private String id;// Only for grid
	private String title;
	private String description;
	private boolean valid;
	private ConditionCode conditionCode;
	private String validationColumnColumnId;

	public Validations() {

	}

	public Validations(String id, String title, String description, boolean valid, ConditionCode conditionCode,
			String validationColumnColumnId) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.valid = valid;
		this.conditionCode = conditionCode;
		this.validationColumnColumnId = validationColumnColumnId;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public ConditionCode getConditionCode() {
		return conditionCode;
	}

	public void setConditionCode(ConditionCode conditionCode) {
		this.conditionCode = conditionCode;
	}

	public String getValidationColumnColumnId() {
		return validationColumnColumnId;
	}

	public void setValidationColumnColumnId(String validationColumnColumnId) {
		this.validationColumnColumnId = validationColumnColumnId;
	}

	@Override
	public String toString() {
		return "Validations [id=" + id + ", title=" + title + ", description=" + description + ", valid=" + valid
				+ ", conditionCode=" + conditionCode + ", validationColumnColumnId=" + validationColumnColumnId + "]";
	}

}
