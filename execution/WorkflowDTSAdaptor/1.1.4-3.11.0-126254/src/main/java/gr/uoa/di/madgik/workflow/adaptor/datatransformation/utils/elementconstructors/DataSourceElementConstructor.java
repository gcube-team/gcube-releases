package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers.impl.DataSourceWrapper;

import java.util.Map;

/**
 * Class used to construct DataSource plan elements.
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class DataSourceElementConstructor implements ElementConstructor {

	public final static int InputLocator = 0;
	public final static int ContentType = 1;
	
	@Override
	public NodeExecutionInfo contructPlanElement(Map<String, String> properties, NamedDataType[] ndts) throws ExecutionValidationException, Exception {
		DataSourceWrapper wrapper = new DataSourceWrapper();
		
		wrapper.setInput(ndts[InputLocator], ndts[ContentType]);
		
		IPlanElement[] elements = wrapper.constructPlanElements();
		
		return new NodeExecutionInfo(elements[0], new WrapperNode(wrapper, null));
	}
}