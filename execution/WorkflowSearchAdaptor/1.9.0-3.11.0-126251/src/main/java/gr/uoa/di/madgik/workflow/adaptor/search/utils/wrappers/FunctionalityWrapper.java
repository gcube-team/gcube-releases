package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers;

import java.io.Serializable;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public abstract class FunctionalityWrapper implements Serializable
{
	
	
	private static final long serialVersionUID = 1L;
	public static final String defaultKeyFieldName = "ObjectID";
	public static final String defaultRankFieldName = "RankID";
	
	public abstract void addVariablesToPlan(ExecutionPlan plan) throws Exception;
	public abstract IPlanElement[] constructPlanElements() throws ExecutionValidationException, ExecutionSerializationException, Exception;
	public abstract NamedDataType getOutputVariable();
}
