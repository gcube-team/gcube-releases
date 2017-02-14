package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.NodeExecutionInfo;

import java.util.Map;

public interface ElementConstructor {
	public NodeExecutionInfo contructPlanElement(Map<String, String> properties, NamedDataType[] inputLocator) throws ExecutionValidationException, Exception;
}
