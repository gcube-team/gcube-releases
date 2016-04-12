package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.element.PlanElementBase;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;

public class ReactionRetryHandler implements IContingencyReactionHandler
{
	private ContingencyReactionRetry Reaction=null;

	public void SetReactionToHandle(IContingencyReaction Reaction) throws ExecutionInternalErrorException
	{
		if(!(Reaction instanceof ContingencyReactionRetry)) throw new ExecutionInternalErrorException("Reaction type set not of the expected type"); 
		this.Reaction=(ContingencyReactionRetry)Reaction;
	}
	
	public IContingencyReaction GetReactionToHandle()
	{
		return this.Reaction;
	}

	public void Handle(String ID,Exception ex, ExecutionHandle Handle, PlanElementBase PlanElement) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		boolean successfulRetry=true;
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID, "Applying reaction handler "+this.GetReactionToHandle().GetReactionType().toString()));
		for(int i=0;i<this.Reaction.NumberOfRetries;i+=1)
		{
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID, "Applying reaction handler "+this.GetReactionToHandle().GetReactionType().toString()+" after waiting for a period of "+this.Reaction.RetryInterval+" milliseconds"));
			try
			{
				if(this.Reaction.RetryInterval>0) Thread.sleep(this.Reaction.RetryInterval);
			}catch(Exception exx){}
			try
			{
				PlanElement.ExecuteWithStateAwareness(Handle);
			}catch(Exception exx)
			{
				successfulRetry=false;
			}
			if(successfulRetry) break;
		}
		if(!successfulRetry) ExceptionUtils.ThrowTransformedException(ex);
	}

}
