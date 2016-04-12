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
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionalFlow;
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
 * This class represents a set of flows that are only executed in case their respective conditions are met.
 * The element follows the known pattern of if ({@link ConditionalPlanElement#IfFlow}) / else if 
 * ({@link ConditionalPlanElement#ElseIfFlows}) / else ({@link ConditionalPlanElement#ElseFlow}) construct. 
 * The if ({@link ConditionalPlanElement#IfFlow}) and else if ({@link ConditionalPlanElement#ElseIfFlows}) flows 
 * are only executed in case their respective conditions are met. The else flow ({@link ConditionalPlanElement#ElseFlow})
 * can be left null. When the execution is started, the conditions of the {@link ConditionalFlow}s are initialized 
 * using the {@link ConditionalFlow#InitializeCondition()} method. After that the if ({@link ConditionalPlanElement#IfFlow})
 * / else if ({@link ConditionalPlanElement#ElseIfFlows}) conditions are evaluated in turn until the first condition 
 * that is evaluated o true is found. The respective flow is executed. If none of the conditions are met, the else flow 
 * ({@link ConditionalPlanElement#ElseFlow}) is executed if it is available.
 * 
 * @author gpapanikos
 */
public class ConditionalPlanElement extends PlanElementBase
{
	
	/** The logger. */
	private static Logger logger= LoggerFactory.getLogger(ConditionalPlanElement.class);
	
	/** The ID of the element */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element */
	private String Name = ConditionalPlanElement.class.getSimpleName();
	
	/** The If flow. */
	public ConditionalFlow IfFlow = null;
	
	/** The Else if flows. */
	public List<ConditionalFlow> ElseIfFlows = new ArrayList<ConditionalFlow>();
	
	/** The Else flow. */
	public IPlanElement ElseFlow = null;

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
			Element ifnode = XMLUtils.GetChildElementWithName((Element) XML, "if");
			if (ifnode == null) throw new ExecutionSerializationException("Not valid serialization provided");
			Element ifpelnode = XMLUtils.GetChildElementWithName(ifnode, "conditionalFlow");
			this.IfFlow = new ConditionalFlow();
			this.IfFlow.FromXML(ifpelnode);
			Element elifnode = XMLUtils.GetChildElementWithName((Element) XML, "elif");
			if (elifnode == null) throw new ExecutionSerializationException("Not valid serialization provided");
			List<Element> elifpelnode = XMLUtils.GetChildElementsWithName(elifnode, "conditionalFlow");
			this.ElseIfFlows.clear();
			for (Element el : elifpelnode)
			{
				ConditionalFlow f = new ConditionalFlow();
				f.FromXML(el);
				this.ElseIfFlows.add(f);
			}
			Element elsenode = XMLUtils.GetChildElementWithName((Element) XML, "else");
			if (elsenode == null) throw new ExecutionSerializationException("Not valid serialization provided");
			Element elsepelnode = XMLUtils.GetChildElementWithName(elsenode, "planElement");
			if (elsepelnode != null) this.ElseFlow = PlanElementUtils.GetPlanElement(elsepelnode);
			else
			{
				this.ElseFlow = null;
			}
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
		return IPlanElement.PlanElementType.Conditional;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID)) { return this; }
		IPlanElement elem = this.IfFlow.Locate(ID);
		if (elem != null) { return elem; }
		if (this.ElseIfFlows != null)
		{
			for (ConditionalFlow elif : this.ElseIfFlows)
			{
				elem = elif.Locate(ID);
				if (elem != null) { return elem; }
			}
		}
		if (this.ElseFlow != null)
		{
			elem = this.ElseFlow.Locate(ID);
			if (elem != null) { return elem; }
		}
		return null;
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		acts.addAll(this.IfFlow.LocateActionElements());
		if (this.ElseIfFlows != null)
		{
			for (ConditionalFlow elif : this.ElseIfFlows)
			{
				acts.addAll(elif.LocateActionElements());
			}
		}
		if (this.ElseFlow != null)
		{
			acts.addAll(this.ElseFlow.LocateActionElements());
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
		buf.append("<if>");
		buf.append(this.IfFlow.ToXML());
		buf.append("</if>");
		buf.append("<elif>");
		if (this.ElseIfFlows != null)
		{
			for (ConditionalFlow cf : this.ElseIfFlows)
			{
				buf.append(cf.ToXML());
			}
		}
		buf.append("</elif>");
		buf.append("<else>");
		if (this.ElseFlow != null)
		{
			buf.append(this.ElseFlow.ToXML());
		}
		buf.append("</else>");
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if (this.IfFlow == null) { throw new ExecutionValidationException("If condition must always have valid value"); }
		this.IfFlow.Validate();
		if (this.ElseIfFlows != null)
		{
			for (ConditionalFlow elif : this.ElseIfFlows)
			{
				elif.Validate();
			}
		}
		if (this.ElseFlow != null)
		{
			this.ElseFlow.Validate();
		}
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
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		vars.addAll(this.IfFlow.GetNeededVariableNames());
		for(ConditionalFlow flow : this.ElseIfFlows)
		{
			vars.addAll(flow.GetNeededVariableNames());
		}
		if(this.ElseFlow!=null) vars.addAll(this.ElseFlow.GetNeededVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetModifiedVariableNames()
	 */
	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		vars.addAll(this.IfFlow.GetModifiedVariableNames());
		for(ConditionalFlow flow : this.ElseIfFlows)
		{
			vars.addAll(flow.GetModifiedVariableNames());
		}
		if(this.ElseFlow!=null) vars.addAll(this.ElseFlow.GetModifiedVariableNames());
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
	public void ExecuteExtender(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException,ExecutionBreakException
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
		
		logger.debug("Starting");
		this.CheckStatus(Handle);
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1, 3, "Starting Execution of "+this.Name, this.Name, hostname, port));
		this.InitializeCondition();
		boolean executedSubElement=false;
		if(this.IfFlow.Condition.EvaluateCondition(Handle))
		{
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),2, 3, "Starting Execution of subelement", this.Name, hostname, port));
			this.StopClock(ClockType.Init);
			this.StartClock(ClockType.Children);
			this.IfFlow.Root.Execute(Handle);
			this.StopClock(ClockType.Children);
			executedSubElement=true;
		}
		else
		{
			for(ConditionalFlow cf:this.ElseIfFlows)
			{
				if(cf.Condition.EvaluateCondition(Handle))
				{
					if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),2, 3, "Starting Execution of subelement", this.Name, hostname, port));
					this.StopClock(ClockType.Init);
					this.StartClock(ClockType.Children);
					cf.Root.Execute(Handle);
					this.StopClock(ClockType.Children);
					executedSubElement=true;
					break;
				}
			}
			if(!executedSubElement && this.ElseFlow!=null)
			{
				if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),2, 3, "Starting Execution of subelement"));
				this.StopClock(ClockType.Init);
				this.StartClock(ClockType.Children);
				this.ElseFlow.Execute(Handle);
				this.StopClock(ClockType.Children);
				executedSubElement=true;
			}
		}
		if(!executedSubElement)
		{
			this.StopClock(ClockType.Init);
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),2, 3, "None of the defined conditions matched"));
		}
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),3, 3, "Finishing Execution of "+this.Name));
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) Handle.EmitEvent(this.GetPerformanceEvent());
	}
	
	/**
	 * Initialize condition. Since some condition used in the condition trees defined in the element may need to know which is the 
	 * first time they are invoked so as to maintain some kind of state throughout their evaluations, this method calls 
	 * {@link ConditionalFlow#InitializeCondition()} for all the conditional flows it contains
	 */
	private void InitializeCondition()
	{
		this.IfFlow.InitializeCondition();
		if (this.ElseIfFlows != null)
		{
			for (ConditionalFlow elif : this.ElseIfFlows)
			{
				elif.InitializeCondition();
			}
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		this.IfFlow.ValidatePreExecution(Handle);
		if (this.ElseIfFlows != null)
		{
			for (ConditionalFlow elif : this.ElseIfFlows)
			{
				elif.ValidatePreExecution(Handle);
			}
		}
		if (this.ElseFlow != null)
		{
			this.ElseFlow.ValidatePreExecution(Handle);
		}
	}
}
