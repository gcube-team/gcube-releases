package gr.uoa.di.madgik.workflow.adaptor.hive.utils.elementconstructors;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.wrappers.impl.DataSourceWrapper;

import java.util.Map;

/**
 * Class used to construct DataSource plan elements.
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class DataSourceElementConstructor {

	public final static int INPUTTYPE = 0;
	public final static int INPUTVALUE = 1;
	public final static int INPUTPARAMETERS = 2;
	
	public NodeExecutionInfo contructPlanElement(Map<String, String> properties, NamedDataType[] ndts) throws ExecutionValidationException, Exception {
		DataSourceWrapper wrapper = new DataSourceWrapper();
		
		wrapper.setInput(ndts[INPUTTYPE], ndts[INPUTVALUE], ndts[INPUTPARAMETERS]);
		
		IPlanElement[] elements = wrapper.constructPlanElements();
		
		return new NodeExecutionInfo(elements[0], new WrapperNode(wrapper, null));
	}
}