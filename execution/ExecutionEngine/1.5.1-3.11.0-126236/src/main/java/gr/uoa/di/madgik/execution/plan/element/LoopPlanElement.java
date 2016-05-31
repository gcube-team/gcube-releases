package gr.uoa.di.madgik.execution.plan.element;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTree;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReaction;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class acts as a container of a subplan which is executed iteratively until the condition defined in
 * {@link LoopPlanElement#LoopCondition} evaluates to false. If during the execution of the subplan a {@link ExecutionBreakException}
 * is caught, the loop is broken and the exception choked. 
 * 
 * @author gpapanikos
 */
public class LoopPlanElement extends PlanElementBase
{

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(LoopPlanElement.class);
	
	/** The ID of the element */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element */
	private String Name = LoopPlanElement.class.getSimpleName();
	
	/** The Root of the subplan that is to be executed on every iteration */
	public IPlanElement Root;
	
	/** The condition of the loop which is evaluated on every iteration performed until it 
	 * is evaluated to false in which case the loop is broken */
	public ConditionTree LoopCondition = null;

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#FromXML(java.lang.String)
	 */
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#FromXML(org.w3c.dom.Element)
	 */
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if (!IPlanElement.PlanElementType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetPlanElementType())) throw new ExecutionSerializationException("plan element type missmatch");
			this.ID = XMLUtils.GetAttribute((Element) XML, "id");
			this.Name = XMLUtils.GetAttribute((Element) XML, "name");
			Element plannode = XMLUtils.GetChildElementWithName((Element) XML, "planElement");
			if (plannode == null) throw new ExecutionSerializationException("Not valid serialization");
			this.Root = PlanElementUtils.GetPlanElement(plannode);
			Element conditionnode = XMLUtils.GetChildElementWithName((Element) XML, "conditionTree");
			this.LoopCondition = new ConditionTree();
			this.LoopCondition.FromXML(conditionnode);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetID()
	 */
	public String GetID()
	{
		return this.ID;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetName()
	 */
	public String GetName()
	{
		return this.Name;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetPlanElementType()
	 */
	public PlanElementType GetPlanElementType()
	{
		return IPlanElement.PlanElementType.Loop;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID)) { return this; }
		if (this.Root != null)
		{
			IPlanElement elem = this.Root.Locate(ID);
			if (elem != null) { return elem; }
		}
		return null;
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		if (this.Root != null)
		{
			acts.addAll(this.Root.LocateActionElements());
		}
		return acts;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SetName(java.lang.String)
	 */
	public void SetName(String Name)
	{
		this.Name = Name;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<planElement type=\"" + this.GetPlanElementType().toString() + "\" id=\"" + this.GetID() + "\" name=\"" + this.GetName() + "\">");
		buf.append(this.LoopCondition.ToXML());
		buf.append(this.Root.ToXML());
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if (this.Root == null) throw new ExecutionValidationException("root of node not set");
		if (this.LoopCondition == null) throw new ExecutionValidationException("root of node not set");
		this.Root.Validate();
		this.LoopCondition.Validate();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportedContingencyTriggers()
	 */
	public IContingencyReaction.ReactionType[] SupportedContingencyTriggers()
	{
		return new IContingencyReaction.ReactionType[0];
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportsContingencyTriggers()
	 */
	public boolean SupportsContingencyTriggers()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetContingencyTriggers()
	 */
	public List<ContingencyTrigger> GetContingencyTriggers()
	{
		return new ArrayList<ContingencyTrigger>();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SetContingencyResourcePick(gr.uoa.di.madgik.execution.engine.ExecutionHandle, java.lang.String)
	 */
	public void SetContingencyResourcePick(ExecutionHandle Handle, String Pick) throws ExecutionRunTimeException
	{
		// Nothing to set. Pick Contingency trigger not supported
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetModifiedVariableNames()
	 */
	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		vars.addAll(this.Root.GetModifiedVariableNames());
		vars.addAll(this.LoopCondition.GetModifiedVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		vars.addAll(this.Root.GetNeededVariableNames());
		vars.addAll(this.LoopCondition.GetNeededVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.PlanElementBase#GetExtenderLogger()
	 */
	public Logger GetExtenderLogger()
	{
		return logger;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.PlanElementBase#ExecuteExtender(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ExecuteExtender(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		this.StartClock(ClockType.Total);
		this.StartClock(ClockType.Init);
		
		int port = -1;
		String hostname = "Unknown";
		
		try {
			if(Handle.getHostingNodeInfo() != null) {
				String[] params = Handle.getHostingNodeInfo().split(":");
				hostname = params[0];
				if(params[1].compareTo("null") != 0)
					port = Integer.parseInt(params[1]);
			}
		}
		catch(Exception e) {
			logger.warn("Unexpected error occurred!", e);
		}
		
		int CurrentStep = 1;
		logger.debug("Starting");
		this.CheckStatus(Handle);
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, CurrentStep + 1, "Starting Execution of " + this.Name, this.Name, hostname, port));
		CurrentStep+=1;
		this.InitializeCondition();
		this.StopClock(ClockType.Init);
		while (true)
		{
			this.StartClock(ClockType.Init);
			if (!this.LoopCondition.EvaluateCondition(Handle))
			{
				if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),"Condition Evaluation of " + this.Name + " set loop break"));
				break;
			}
			else
			{
				if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),"Condition Evaluation of " + this.Name + " set loop continue"));
			}
			this.CheckStatus(Handle);
			try
			{
				if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, CurrentStep + 1, "Starting Execution of subelement for the "+(CurrentStep-1)+" time", this.Name, hostname, port));
				CurrentStep+=1;
				this.StopClock(ClockType.Init);
				this.StartClock(ClockType.Children);
				this.Root.Execute(Handle);
				this.StopClock(ClockType.Children);
			} catch (ExecutionBreakException ex)
			{
				if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),"Received break exception. Breaking the loop"));
				break;
			}
		}
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, CurrentStep, "Finishing Execution of " + this.Name, this.Name, hostname, port));
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) Handle.EmitEvent(this.GetPerformanceEvent());
	}
	
	/**
	 * Initialize condition. Since some condition used in the condition tree defined in the element may need to know which is the 
	 * first time they are invoked so as to maintain some kind of state throughout their evaluations, this method calls 
	 * {@link LoopPlanElement#LoopCondition#InitializeCondition()} for all the conditional flows it contains
	 */
	private void InitializeCondition()
	{
		this.LoopCondition.InitializeCondition();
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		this.Root.ValidatePreExecution(Handle);
		this.LoopCondition.ValidatePreExecution(Handle);
	}
}
