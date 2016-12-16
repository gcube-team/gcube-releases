package gr.uoa.di.madgik.workflow.adaptor.search.utils.elementconstructors.processing;

import java.util.List;
import java.util.Map;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.FunctionalArgumentParser;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.JoinWrapper;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.JoinWrapper.Variables;
public class JoinElementConstructor implements ProcessingElementConstructor 
{

	@Override
	public NodeExecutionInfo constructPlanElement(Map<String, String> arguments, List<NamedDataType> inputLocators, Integer bufferCapacity) throws Exception 
	{
		JoinWrapper wrapper = new JoinWrapper();
		boolean duplicateElimination = FunctionalArgumentParser.getDuplicateEliminationStatus(arguments);
		if(duplicateElimination == true)
			wrapper.enableDuplicateElimination(); //support for duplicate elimination based on custom field names for object ids should be included by the planner as a parameter
												  //or checked dynamically by this planner by annotating each plan element with the respective wrapper
		wrapper.setRecordGenerationPolicy(FunctionalArgumentParser.getPayloadSide(arguments)); //the same hold for custom field names for object ranks in case of sort mode	
		
		if(bufferCapacity != null)
			wrapper.setBufferCapacity(bufferCapacity);
		
		//wrapper.setTimeout(3, TimeUnit.MINUTES);
		SequencePlanElement seq = new SequencePlanElement();
		
		wrapper.setVariable(Variables.LeftInputLocator, inputLocators.get(0));
		wrapper.setVariable(Variables.RightInputLocator, inputLocators.get(1));
		
		IPlanElement[] elements = wrapper.constructPlanElements();
		seq.ElementCollection.add(elements[0]);
		if(duplicateElimination == true)
			seq.ElementCollection.add(elements[1]);
		return new NodeExecutionInfo(seq, new WrapperNode(wrapper, null));
	}

}
