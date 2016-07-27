package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class ValidateRules extends ValidationWorker{

	CubeManager cubeManager; 
	ValidateDataWithExpressionFactory validateDataWithExpression;
	
	
	
	public ValidateRules(OperationInvocation sourceInvocation, CubeManager cubeManager, ValidateDataWithExpressionFactory validateDataWithExpression) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.validateDataWithExpression = validateDataWithExpression;
	}

	

	@Override
	protected ValidityResult execute() throws WorkerException,
			OperationAbortedException {
		List<ValidationDescriptor> validationDescriptors = new ArrayList<>();
		boolean isValid = true;
		updateProgress(0.05f,"retrieving parameters");
		List<Map<String, Object>> rules = getRules();
		updateProgress(0.1f,"validating rules");
		float singleProgress = 0.8f/rules.size();
		for (int i =0; i<rules.size();i++){
			Map<String, Object> ruleMapping = rules.get(i);
			String ruleName = (String)ruleMapping.get(ValidateRulesFactory.NAME_PARAMETER.getIdentifier());
			updateProgress(0.1f+(singleProgress*(i+1)),"validating rule "+ruleName);
			checkAborted();
			WorkerWrapper<ValidationWorker, ValidityResult> wrapper = this.createWorkerWrapper(validateDataWithExpression);
			Map<String, Object> wrapperParameter = new HashMap<String, Object>();
			wrapperParameter.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), ruleMapping.get(ValidateRulesFactory.EXPRESSION_PARAMETER.getIdentifier()));
			wrapperParameter.put(ValidateDataWithExpressionFactory.VALIDATION_TITLE_PARAMETER.getIdentifier(), ruleName);
			try {
				wrapper.execute(getSourceInvocation().getTargetTableId(), null, wrapperParameter);
			} catch (InvalidInvocationException e) {
				throw new WorkerException("error invoking rule "+ruleName, e);
			}
			
			validationDescriptors.addAll(wrapper.getResult().getValidationDescriptors());
			isValid = isValid && wrapper.getResult().isValid();
		}
		updateProgress(0.95f,"preparing results");
		return new ValidityResult(isValid, validationDescriptors);
	}

	@SuppressWarnings("unchecked")
	List<Map<String, Object>> getRules(){
		return (List<Map<String, Object>>) getSourceInvocation().getParameterInstances().get(ValidateRulesFactory.RULES_PARAMETER.getIdentifier());
	}
	
}
