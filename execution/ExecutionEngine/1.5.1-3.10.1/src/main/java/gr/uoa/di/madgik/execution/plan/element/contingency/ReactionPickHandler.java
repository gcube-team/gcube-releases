package gr.uoa.di.madgik.execution.plan.element.contingency;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.element.PlanElementBase;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.is.InformationSystem;
import java.util.List;

public class ReactionPickHandler implements IContingencyReactionHandler
{

	private ContingencyReactionPick Reaction=null;

	public void SetReactionToHandle(IContingencyReaction Reaction) throws ExecutionInternalErrorException
	{
		if(!(Reaction instanceof ContingencyReactionPick)) throw new ExecutionInternalErrorException("Reaction type set not of the expected type"); 
		this.Reaction=(ContingencyReactionPick)Reaction;
	}
	
	public IContingencyReaction GetReactionToHandle()
	{
		return this.Reaction;
	}

	public void Handle(String ID,Exception ex, ExecutionHandle Handle, PlanElementBase PlanElement) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		boolean successfullRetry=true;
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID, "Applying reaction handler "+this.GetReactionToHandle().GetReactionType().toString()));
		successfullRetry=this.ScanList(ID,this.Reaction.PickList, this.Reaction.ExhaustPickList, PlanElement, Handle);
		if(!successfullRetry && this.Reaction.RetrievePickList!=null && this.Reaction.RetrievePickList.trim().length()!=0)
		{
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID, "Reaction handler "+this.GetReactionToHandle().GetReactionType().toString()+" retrieving list from Information System"));
			List<String> RetrievedPickList=null;
			try
			{
				RetrievedPickList=InformationSystem.Query(this.Reaction.RetrievePickList,Handle.GetPlan().EnvHints);
			} catch (EnvironmentInformationSystemException exx)
			{
				throw new ExecutionRunTimeException("Could not retrieve pick list", exx);
			}
			successfullRetry=this.ScanList(ID,RetrievedPickList, this.Reaction.ExhaustPickList, PlanElement, Handle);
		}
		if(!successfullRetry) ExceptionUtils.ThrowTransformedException(ex);
	}
	
	private boolean ScanList(String ID,List<String> PickList, boolean ExhaustPickList, PlanElementBase PlanElement, ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID, "Reaction handler "+this.GetReactionToHandle().GetReactionType().toString()+" scaning provided list"));
		boolean successfulRetry=true;
		if(PickList==null || PickList.size()==0) return false;
		for(int i=0;i<PickList.size();i+=1)
		{
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID, "Applying reaction handler "+this.GetReactionToHandle().GetReactionType().toString()+" for picked resource"));
			if(i>0 && !ExhaustPickList) break;
			PlanElement.SetContingencyResourcePick(Handle,this.Reaction.PickList.get(i));
			try
			{
				PlanElement.ExecuteWithStateAwareness(Handle);
			}catch(Exception exx)
			{
				successfulRetry=false;
			}
			if(successfulRetry) break;
		}
		return successfulRetry;
	}
}
