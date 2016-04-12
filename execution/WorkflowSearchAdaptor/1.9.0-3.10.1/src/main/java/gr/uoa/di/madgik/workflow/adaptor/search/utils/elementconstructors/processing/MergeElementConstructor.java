package gr.uoa.di.madgik.workflow.adaptor.search.utils.elementconstructors.processing;

import java.util.List;
import java.util.Map;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.OperationMode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.FunctionalArgumentParser;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.MergeWrapper;

public class MergeElementConstructor implements ProcessingElementConstructor 
{

	@Override
	public NodeExecutionInfo constructPlanElement(Map<String, String> arguments, List<NamedDataType> inputLocators, Integer bufferCapacity) throws Exception 
	{
		MergeWrapper wrapper = new MergeWrapper();
		
		boolean duplicateElimination = FunctionalArgumentParser.getDuplicateEliminationStatus(arguments) && !FunctionalArgumentParser.getMergeOperationMode(arguments).equals(OperationMode.Fusion);
		if(duplicateElimination == true)
			wrapper.enableDuplicateElimination(); //support for duplicate elimination based on custom field names for object ids should be included by the planner as a parameter
												  //or checked dynamically by this planner by annotating each plan element with the respective wrapper
		wrapper.setOperationMode(FunctionalArgumentParser.getMergeOperationMode(arguments)); //the same holds for custom field names for object ranks in case of sort mode	
		
		if (FunctionalArgumentParser.getMergeOperationMode(arguments).equals(OperationMode.Fusion)) {
			wrapper.setQuery(arguments.get("query"));
		}
		
		
		if(bufferCapacity != null)
			wrapper.setBufferCapacity(bufferCapacity);
		//wrapper.setTimeout(3, TimeUnit.MINUTES);
		SequencePlanElement seq = new SequencePlanElement();
		
		for(int i = 0; i < inputLocators.size(); i++)
			wrapper.setInputLocator(i, inputLocators.get(i));
		
		IPlanElement[] elements = wrapper.constructPlanElements();
		seq.ElementCollection.add(elements[0]);
		if(duplicateElimination == true)
			seq.ElementCollection.add(elements[1]);
		return new NodeExecutionInfo(seq, new WrapperNode(wrapper, null));
	}

}
