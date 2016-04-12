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
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReaction;
import gr.uoa.di.madgik.execution.plan.trycatchfinally.CatchElement;
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
 * This class acts as a try {@link TryCatchFinallyPlanElement#TryFlow} / catch 
 * ({@link TryCatchFinallyPlanElement#CatchFlows}) / finally ({@link TryCatchFinallyPlanElement#FinallyFlow})
 * block of execution. During execution the subplan that is declared in the 
 * {@link TryCatchFinallyPlanElement#TryFlow} is executed. In case an exception is caught, the 
 * {@link CatchElement}s in the {@link TryCatchFinallyPlanElement#CatchFlows} are iterated to match
 * the exception caught with one of the handled ones. If some {@link CatchElement} can handle the exception,
 * the subplan defined in it is executed. If the {@link CatchElement} does not define that the exception is
 * re-thrown after the execution of the compensation code, the execution is moved to the 
 * {@link TryCatchFinallyPlanElement#FinallyFlow} subplan. If however the exception is to be re-thrown or a new
 * exception is thrown from the compensation code, the exception is bubbled up after the
 * {@link TryCatchFinallyPlanElement#FinallyFlow} subplan is executed. 
 * 
 * @author gpapanikos
 */
public class TryCatchFinallyPlanElement extends PlanElementBase
{
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(TryCatchFinallyPlanElement.class);
	
	/** The ID of the element */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element */
	private String Name = TryCatchFinallyPlanElement.class.getSimpleName();
	
	/** The Try flow. */
	public IPlanElement TryFlow = null;
	
	/** The Catch flows. */
	public List<CatchElement> CatchFlows = new ArrayList<CatchElement>();
	
	/** The Finally flow. */
	public IPlanElement FinallyFlow = null;

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
			Element trynode = XMLUtils.GetChildElementWithName((Element) XML, "try");
			if (trynode == null) throw new ExecutionSerializationException("Not valid serialization");
			Element plantrynode = XMLUtils.GetChildElementWithName(trynode, "planElement");
			if (plantrynode == null) throw new ExecutionSerializationException("Not valid serialization");
			this.TryFlow = PlanElementUtils.GetPlanElement(plantrynode);
			Element finallynode = XMLUtils.GetChildElementWithName((Element) XML, "finally");
			if (finallynode == null) throw new ExecutionSerializationException("Not valid serialization");
			Element planfinallynode = XMLUtils.GetChildElementWithName(finallynode, "planElement");
			if (planfinallynode != null) this.FinallyFlow = PlanElementUtils.GetPlanElement(planfinallynode);
			else this.FinallyFlow = null;
			Element catchnode = XMLUtils.GetChildElementWithName((Element) XML, "catch");
			if (catchnode == null) throw new ExecutionSerializationException("Not valid serialization");
			List<Element> catchelems = XMLUtils.GetChildElementsWithName(catchnode, "catch");
			this.CatchFlows.clear();
			for (Element c : catchelems)
			{
				CatchElement ce = new CatchElement();
				ce.FromXML(c);
				this.CatchFlows.add(ce);
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
		return IPlanElement.PlanElementType.TryCatchFinally;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID)) { return this; }
		if (this.TryFlow != null)
		{
			IPlanElement elem = this.TryFlow.Locate(ID);
			if (elem != null) { return elem; }
		}
		if (this.CatchFlows != null)
		{
			for (CatchElement ef : this.CatchFlows)
			{
				if (ef.Root == null) continue;
				IPlanElement elem = ef.Root.Locate(ID);
				if (elem != null) { return elem; }
			}
		}
		if (this.FinallyFlow != null)
		{
			IPlanElement elem = this.FinallyFlow.Locate(ID);
			if (elem != null) { return elem; }
		}
		return null;
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		if (this.TryFlow != null)
		{
			acts.addAll(this.TryFlow.LocateActionElements());
		}
		if (this.CatchFlows != null)
		{
			for (CatchElement ef : this.CatchFlows)
			{
				if (ef.Root == null) continue;
				acts.addAll(ef.Root.LocateActionElements());
			}
		}
		if (this.FinallyFlow != null)
		{
			acts.addAll(this.FinallyFlow.LocateActionElements());
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
		buf.append("<try>");
		buf.append(this.TryFlow.ToXML());
		buf.append("</try>");
		buf.append("<catch>");
		if (this.CatchFlows != null)
		{
			for (CatchElement c : this.CatchFlows)
			{
				buf.append(c.ToXML());
			}
		}
		buf.append("</catch>");
		buf.append("<finally>");
		if (this.FinallyFlow != null) buf.append(this.FinallyFlow.ToXML());
		buf.append("</finally>");
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if (this.TryFlow == null) { throw new ExecutionValidationException("The try flow is not defined"); }
		this.TryFlow.Validate();
		if (this.CatchFlows != null)
		{
			for (CatchElement catchf : this.CatchFlows)
			{
				catchf.Validate();
			}
		}
		if (this.FinallyFlow != null)
		{
			this.FinallyFlow.Validate();
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
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetModifiedVariableNames()
	 */
	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars= new HashSet<String>();
		if (this.TryFlow != null) vars.addAll(this.TryFlow.GetModifiedVariableNames());
		for (CatchElement cf : this.CatchFlows)
		{
			vars.addAll(cf.GetModifiedVariableNames());
		}
		if (this.FinallyFlow != null) vars.addAll(this.FinallyFlow.GetModifiedVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars= new HashSet<String>();
		if (this.TryFlow != null) vars.addAll(this.TryFlow.GetNeededVariableNames());
		for (CatchElement cf : this.CatchFlows)
		{
			vars.addAll(cf.GetNeededVariableNames());
		}
		if (this.FinallyFlow != null) vars.addAll(this.FinallyFlow.GetNeededVariableNames());
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
		
		int CurrentStep=1;
		logger.debug("Starting");
		this.CheckStatus(Handle);
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, CurrentStep+1, "Starting Execution of " + this.Name, this.Name, hostname, port));
		ExecutionRunTimeException nex = null;
		this.StopClock(ClockType.Init);
		this.StartClock(ClockType.Children);
		try
		{
			logger.debug("Executing");
			this.TryFlow.Execute(Handle);
		} catch (ExecutionRunTimeException ex)
		{
			nex=ex;
			this.CheckStatus(Handle);
			try
			{
				for (CatchElement ce : this.CatchFlows)
				{
					CurrentStep+=1;
					if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, CurrentStep+1, "Seeking alternative flow in element " + this.Name+" because of error "+ex.GetCauseFullName()+" with message "+ex.getMessage(), this.Name, hostname, port));
					if (ce.Execute(this.GetID(),Handle, ex))
					{
						nex=null;
						break;
					}
				}
			} catch (ExecutionRunTimeException exx)
			{
				nex = exx;
			}
		}
		if (this.FinallyFlow != null)
		{
			this.CheckStatus(Handle);
			CurrentStep+=1;
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, CurrentStep+1, "Finalizing in element " + this.Name, this.Name, hostname, port));
			this.FinallyFlow.Execute(Handle);
		}
		if (nex != null)
		{
			CurrentStep+=1;
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, CurrentStep+1, "Throwing Exception caught in element " + this.Name+"after processing it for alternative flows", this.Name, hostname, port));
			logger.debug("Exiting");
			throw nex;
		}
		this.StopClock(ClockType.Children);
		CurrentStep+=1;
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, CurrentStep, "Finishing Execution of " + this.Name, this.Name, hostname, port));
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) Handle.EmitEvent(this.GetPerformanceEvent());
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
	}
}
