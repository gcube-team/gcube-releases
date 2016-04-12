package gr.uoa.di.madgik.workflow.adaptor.hive.utils.elementconstructors;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.wrappers.impl.DataSinkWrapper;

import java.util.Map;

/**
 * Class used to construct DataSink plan elements.
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class DataSinkElementConstructor {
	public final static int INPUTLOCATOR = 0;
	public final static int OUTPUTTYPE = 1;
	public final static int OUTPUTVALUE = 2;
	public final static int OUTPUTPARAMS = 3;
	public final static int STATSCONTAINER = 4;
	
	public NodeExecutionInfo contructPlanElement(Map<String, String> properties, NamedDataType[] ndts) throws ExecutionValidationException, Exception {
		DataSinkWrapper wrapper = new DataSinkWrapper();
		
		wrapper.setInput(ndts[INPUTLOCATOR], ndts[OUTPUTTYPE], ndts[OUTPUTVALUE], ndts[OUTPUTPARAMS], ndts[STATSCONTAINER]);
		
		IPlanElement[] elements = wrapper.constructPlanElements();
		
		return new NodeExecutionInfo(elements[0], new WrapperNode(wrapper, null));
	}
}
