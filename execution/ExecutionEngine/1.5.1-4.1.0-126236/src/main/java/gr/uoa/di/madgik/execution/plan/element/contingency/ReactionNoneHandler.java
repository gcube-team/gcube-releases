package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.element.PlanElementBase;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;

public class ReactionNoneHandler implements IContingencyReactionHandler
{
	private ContingencyReactionNone Reaction=null;

	public void SetReactionToHandle(IContingencyReaction Reaction) throws ExecutionInternalErrorException
	{
		if(!(Reaction instanceof ContingencyReactionNone)) throw new ExecutionInternalErrorException("Reaction type set not of the expected type"); 
		this.Reaction=(ContingencyReactionNone)Reaction;
	}
	
	public IContingencyReaction GetReactionToHandle()
	{
		return this.Reaction;
	}

	public void Handle(String ID,Exception ex, ExecutionHandle Handle, PlanElementBase PlanElement) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID, "Applying reaction handler "+this.GetReactionToHandle().GetReactionType().toString()));
		ExceptionUtils.ThrowTransformedException(ex);
	}

}
