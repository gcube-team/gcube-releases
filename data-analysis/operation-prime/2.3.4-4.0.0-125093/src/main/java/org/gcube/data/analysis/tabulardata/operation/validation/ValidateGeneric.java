package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.Collections;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class ValidateGeneric extends ValidationWorker {
	private CubeManager cube;
	
	public ValidateGeneric(OperationInvocation sourceInvocation,
			CubeManager cube) {
		super(sourceInvocation);
		this.cube = cube;
	}

	@Override
	protected ValidityResult execute() throws WorkerException {
		Table targetTable=cube.getTable(getSourceInvocation().getTargetTableId());		
		ValidationDescriptor validationDescriptor = new ValidationDescriptor(true, "The table is a valid generic table", 0);
		Validation toSet=new Validation("The table is a valid generic table", true, 0);
		ValidationsMetadata validations = new ValidationsMetadata(Collections.singletonList(toSet));
		cube.addValidations(targetTable.getId(), validations);
		return new ValidityResult(true, Collections.singletonList(validationDescriptor));		
	}

}
