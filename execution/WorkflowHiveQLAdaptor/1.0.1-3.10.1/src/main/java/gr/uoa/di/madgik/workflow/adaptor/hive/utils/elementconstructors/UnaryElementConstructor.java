package gr.uoa.di.madgik.workflow.adaptor.hive.utils.elementconstructors;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.Unary;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.wrappers.impl.UnaryOperatorWrapper;

/**
 * Class used to construct unary plan elements.
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class UnaryElementConstructor {
	public final static int OPERATORCLASSNAME = 0;
	public final static int INPUTLOCATOR = 1;
	public final static int OPERATORPARAMS = 2;
	public final static int STATSCONTAINER = 3;
	
	public NodeExecutionInfo contructPlanElement(Class<? extends Unary> unaryClass, NamedDataType[] ndts) throws ExecutionValidationException, Exception {
		UnaryOperatorWrapper wrapper = new UnaryOperatorWrapper();
		
		wrapper.setInput(ndts[OPERATORCLASSNAME], ndts[INPUTLOCATOR], ndts[OPERATORPARAMS], ndts[STATSCONTAINER]);
		
		IPlanElement[] elements = wrapper.constructPlanElements();
		
		return new NodeExecutionInfo(elements[0], new WrapperNode(wrapper, null));
	}
}
