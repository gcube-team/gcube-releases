package org.gcube.data.analysis.tabulardata.task.executor.operation;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationDescriptor;

public class PreconditionResult {

	private boolean valid;

	private List<ValidationDescriptor> validations = new ArrayList<ValidationDescriptor>();

	public PreconditionResult(boolean valid) {
		this.valid = valid;
	}
	
	public PreconditionResult(boolean valid, List<ValidationDescriptor> validations) {
		this(valid);
		this.validations = validations;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public List<ValidationDescriptor> getValidations() {
		return validations;
	}

	public void setValidations(List<ValidationDescriptor> validations) {
		this.validations = validations;
	}

	@Override
	public String toString() {
		return "PreconditionResult [valid=" + valid + ", validations="
				+ validations + "]";
	}



}
