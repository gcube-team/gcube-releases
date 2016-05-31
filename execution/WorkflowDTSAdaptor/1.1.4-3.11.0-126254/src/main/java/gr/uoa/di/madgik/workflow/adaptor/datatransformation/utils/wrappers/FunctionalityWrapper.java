package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;

/**
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public abstract class FunctionalityWrapper {
	
	public static final String defaultKeyFieldName = "ObjectID";
	public static final String defaultRankFieldName = "RankID";
	
	public abstract void addVariablesToPlan(ExecutionPlan plan) throws Exception;
	public abstract IPlanElement[] constructPlanElements() throws ExecutionValidationException, ExecutionSerializationException, Exception;
	public abstract NamedDataType getOutputVariable();
}
