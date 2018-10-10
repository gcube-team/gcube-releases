package org.gcube.data.analysis.tabulardata.operation.worker.results;

import java.util.ArrayList;
import java.util.List;

public class ValidityResult implements Result{

	private boolean valid;
	
	private List<ValidationDescriptor> validationDescriptors = new ArrayList<>();
	
	public ValidityResult(boolean valid, List<ValidationDescriptor> validationDescriptors) {
		this.valid = valid;
		this.validationDescriptors = validationDescriptors;
	}

	public boolean isValid() {
		return valid;
	}

	public List<ValidationDescriptor> getValidationDescriptors() {
		return validationDescriptors;
	}
	
		
}
