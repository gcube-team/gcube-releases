package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers.impl.MergeWrapper;

import java.util.Map;

public class MergeElementConstructor implements ElementConstructor {
	public final static int InputLocator = 0;
	public final static int Output = 1;
	
	@Override
	public NodeExecutionInfo contructPlanElement(Map<String, String> properties, NamedDataType[] ndts) throws Exception {
		MergeWrapper wrapper = new MergeWrapper();
		
		wrapper.setInput(ndts[InputLocator], ndts[Output]);
		
		IPlanElement[] elements = wrapper.constructPlanElements();
		
		return new NodeExecutionInfo(elements[0], new WrapperNode(wrapper, null));
	}
}
