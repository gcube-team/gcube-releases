package org.gcube.data.analysis.tabulardata.service.operation;

import java.util.List;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationDescriptor;

public interface Job extends ValidationJob {
		
	List<ValidationJob> getValidationJobs();
	
	OperationExecution getInvocation();
	
	List<ValidationDescriptor> getValidations();
	
	JobClassifier getJobClassifier();
}
