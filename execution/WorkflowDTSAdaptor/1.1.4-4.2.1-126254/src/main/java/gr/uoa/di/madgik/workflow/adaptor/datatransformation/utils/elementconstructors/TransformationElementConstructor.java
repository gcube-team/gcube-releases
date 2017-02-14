package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers.impl.TransformationWrapper;

import java.util.Map;

/**
 * Class used to construct Transformation plan elements.
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class TransformationElementConstructor implements ElementConstructor {
	public final static int InputLocator = 0;
	public final static int TransformationUnit = 1;
	public final static int ContentType = 2;
	public final static int Scope = 3;
	
	@Override
	public NodeExecutionInfo contructPlanElement(Map<String, String> properties, NamedDataType[] ndts) throws ExecutionValidationException, Exception {
		TransformationWrapper wrapper = new TransformationWrapper();
		
		wrapper.setInput(ndts[InputLocator], ndts[TransformationUnit], ndts[ContentType], ndts[Scope]);
		
		IPlanElement[] elements = wrapper.constructPlanElements();
		
		return new NodeExecutionInfo(elements[0], new WrapperNode(wrapper, null));
	}
}
