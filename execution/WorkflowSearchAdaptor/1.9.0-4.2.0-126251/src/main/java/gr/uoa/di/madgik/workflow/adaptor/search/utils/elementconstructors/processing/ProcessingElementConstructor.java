package gr.uoa.di.madgik.workflow.adaptor.search.utils.elementconstructors.processing;

import java.util.List;
import java.util.Map;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.NodeExecutionInfo;

public interface ProcessingElementConstructor 
{
	public NodeExecutionInfo constructPlanElement(Map<String, String> properties, List<NamedDataType> inputLocators, Integer bufferCapacity) throws ExecutionValidationException, Exception;
}
