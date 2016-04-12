package org.gcube.data.analysis.tabulardata.operation.worker.results;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;

public class ValidationDescriptor {

	private String description;

	private boolean valid;

	private ColumnLocalId validationColumn = null;

	private int conditionCode;

	private String title;

	public ValidationDescriptor(boolean valid, String description, int conditionCode) {
		this.valid = valid;
		this.description = description;
		this.conditionCode = conditionCode;
	}

	public ValidationDescriptor(boolean valid, String title, String description, int conditionCode) {
		this(valid, description, conditionCode);
		this.title = title;
	}
	
	public ValidationDescriptor(boolean valid, String description, int conditionCode, ColumnLocalId validationColumn) {
		this(valid, description, conditionCode);
		this.validationColumn = validationColumn;
	}

	public ValidationDescriptor(boolean valid, String title, String description, int conditionCode, ColumnLocalId validationColumn) {
		this(valid, title,  description, conditionCode);
		this.validationColumn = validationColumn;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public ColumnLocalId getValidationColumn() {
		return validationColumn;
	}

	public int getConditionCode() {
		return conditionCode;
	}

}
