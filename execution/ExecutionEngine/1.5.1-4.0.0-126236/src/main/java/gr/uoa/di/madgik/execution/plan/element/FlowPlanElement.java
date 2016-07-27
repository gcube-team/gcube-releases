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
import gr.uoa.di.madgik.execution.utils.BackgroundExecution;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This plan element acts as a container of other {@link IPlanElement}s. It orchestrates their execution by
 * executing the elements that are contained in the  {@link FlowPlanElement#ElementCollection} in parallel
 * and waiting for their completion. If one of the elements is terminated with an error, after all the elements
 * have terminated, the exception indicating the error is re-thrown. If more than one elements finished with an 
 * error, only the first error found iterating through the {@link FlowPlanElement#ElementCollection} is thrown.
 * 
 * @author gpapanikos
 */
public class FlowPlanElement extends PlanElementBase implements Iterable<IPlanElement>
{

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(FlowPlanElement.class);
	
	/** The ID of the element. */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element. */
	private String Name = FlowPlanElement.class.getSimpleName();
	
	/** The Element collection that are to be executed in parallel */
	public List<IPlanElement> ElementCollection = new ArrayList<IPlanElement>();
	
	/** The list of background workers that are currently running monitoring the execution of the elements that have
	 * been started. */
	private List<BackgroundExecution> workers = new ArrayList<BackgroundExecution>();
	
	/** Synchronization object for the list of workers. */
	private final Boolean synchWorker = new Boolean(false);

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<IPlanElement> iterator()
	{
		return this.ElementCollection.iterator();
	}

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
			Element listnode = XMLUtils.GetChildElementWithName((Element) XML, "list");
			if (listnode == null) throw new ExecutionSerializationException("Not valid serialization");
			List<Element> lstmembers = XMLUtils.GetChildElementsWithName(listnode, "planElement");
			this.ElementCollection.clear();
			for (Element lstm : lstmembers) this.ElementCollection.add(PlanElementUtils.GetPlanElement(lstm));
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
		return IPlanElement.PlanElementType.Flow;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID)) { return this; }
		for (IPlanElement elem : this)
		{
			IPlanElement e = elem.Locate(ID);
			if (e != null) { return e; }
		}
		return null;
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		for (IPlanElement elem : this)
		{
			acts.addAll(elem.LocateActionElements());
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
		buf.append("<list>");
		for (IPlanElement elem : this)
		{
			buf.append(elem.ToXML());
		}
		buf.append("</list>");
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if (this.ElementCollection == null || this.ElementCollection.size() == 0) throw new ExecutionValidationException("Element collection not set");
		for (IPlanElement elem : this)
		{
			elem.Validate();
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
		Set<String> vars = new HashSet<String>();
		for (IPlanElement elem : this.ElementCollection)
		{
			vars.addAll(elem.GetModifiedVariableNames());
		}
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		for (IPlanElement elem : this.ElementCollection)
		{
			vars.addAll(elem.GetNeededVariableNames());
		}
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
		
		int TotalSteps = this.ElementCollection.size() + 2;
		logger.debug("Starting");
		this.CheckStatus(Handle);
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1, TotalSteps, "Starting Execution of " + this.Name, this.Name, hostname, port));
		synchronized (this.synchWorker)
		{
			this.StopClock(ClockType.Init);
			this.StartClock(ClockType.Children);
			for (int i = 0; i < this.ElementCollection.size(); i += 1)
			{
				if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),"Starting Execution of sub element "+(i+1)+" of "+this.ElementCollection.size()));
				BackgroundExecution bg = new BackgroundExecution(this.ElementCollection.get(i), Handle, this.synchWorker);
				this.workers.add(bg);
				Thread t = new Thread(bg);
				t.setName(BackgroundExecution.class.getName());
				t.setDaemon(true);
				t.start();
			}
			int numberOfPreviousCompletedWorkers=0;
			while (true)
			{
				int countCompleted=0;
				for(BackgroundExecution bg : this.workers)
				{
					if(bg.ExecutionCompleted) countCompleted+=1;
				}
				if(countCompleted>numberOfPreviousCompletedWorkers)
				{
					if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1+countCompleted, TotalSteps, "Completed Execution of a sub element ("+countCompleted+" finished of "+this.ElementCollection.size()+")", this.Name, hostname, port));
					numberOfPreviousCompletedWorkers=countCompleted;
				}
				if(countCompleted>=this.workers.size()) break;
				try
				{
					this.synchWorker.wait();
				} catch (Exception ex)
				{
				}
			}
		}
		this.StopClock(ClockType.Children);
		this.StartClock(ClockType.Finilization);
		for (BackgroundExecution bg : this.workers)
		{
			if (bg.Error != null)
			{
				if (bg.Error instanceof ExecutionRunTimeException) throw (ExecutionRunTimeException) bg.Error;
				else if (bg.Error instanceof ExecutionInternalErrorException) throw (ExecutionInternalErrorException) bg.Error;
				else if (bg.Error instanceof ExecutionCancelException) throw (ExecutionCancelException) bg.Error;
				else if (bg.Error instanceof ExecutionBreakException) throw (ExecutionBreakException) bg.Error;
				else
				{
					ExecutionRunTimeException rt = new ExecutionRunTimeException(bg.Error.getMessage());
					rt.SetCause(bg.Error);
					throw rt;
				}
			}
		}
		this.workers.clear();
		this.StopClock(ClockType.Finilization);
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),TotalSteps, TotalSteps, "Finishing Execution of " + this.Name));
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
