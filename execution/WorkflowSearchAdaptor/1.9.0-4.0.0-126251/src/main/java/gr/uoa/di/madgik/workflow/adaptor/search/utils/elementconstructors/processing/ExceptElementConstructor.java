package gr.uoa.di.madgik.workflow.adaptor.search.utils.elementconstructors.processing;

import java.util.List;
import java.util.Map;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.ExceptWrapper;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.ExceptWrapper.Variables;

public class ExceptElementConstructor implements ProcessingElementConstructor 
{

	@Override
	public NodeExecutionInfo constructPlanElement(Map<String, String> arguments, List<NamedDataType> inputLocators, Integer bufferCapacity) throws Exception {
		ExceptWrapper wrapper = new ExceptWrapper();
		
		wrapper.setVariable(Variables.LeftInputLocator, inputLocators.get(0));
		wrapper.setVariable(Variables.RightInputLocator, inputLocators.get(1));
		
		if(bufferCapacity != null)
			wrapper.setBufferCapacity(bufferCapacity);
		
		//wrapper.setTimeout(3, TimeUnit.MINUTES);
		IPlanElement[] elements = wrapper.constructPlanElements();
		
		return new NodeExecutionInfo(elements[0], new WrapperNode(wrapper, null));
	}

}
