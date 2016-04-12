package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.element.PlanElementBase;

public interface IContingencyReactionHandler
{
	public void SetReactionToHandle(IContingencyReaction Reaction) throws ExecutionInternalErrorException;
	public IContingencyReaction GetReactionToHandle();
	public void Handle(String ID,Exception ex, ExecutionHandle Handle, PlanElementBase PlanElement) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException;
}
