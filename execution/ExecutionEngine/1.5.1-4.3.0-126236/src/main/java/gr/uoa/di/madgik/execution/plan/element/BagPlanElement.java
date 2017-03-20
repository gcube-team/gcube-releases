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
import gr.uoa.di.madgik.execution.plan.element.condition.BagConditionEnvironment;
import gr.uoa.di.madgik.execution.plan.element.condition.BagConditionalElement;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTree;
import gr.uoa.di.madgik.execution.plan.element.condition.IConditionEnvironment;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReaction;
import gr.uoa.di.madgik.execution.utils.BackgroundExecution;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This plan element acts as a container of other {@link IPlanElement}s. It orchestrates their execution based on their
 * execution preconditions. These elements as well as their conditions are evaluated every time one of the previously running
 * elements have completed their execution. While this element is executing everytime an element previously running has 
 * terminated the set of not yet run elements is scanned. If no element can be run while no more elements are still
 * running, the process is terminated if the {@link BagPlanElement#TerminateOnNoProgress} is set. Even if more elements
 * are still running, if the {@link BagPlanElement#TerminationCondition} is set and is evaluated to true, the execution of
 * the element is terminated.
 * 
 * @author gpapanikos
 */
public class BagPlanElement extends PlanElementBase
{

	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(BagPlanElement.class);
	
	/** The ID of the element. */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element. */
	private String Name = BagPlanElement.class.getSimpleName();
	
	/** The Element collection that make up the flow that this element will orchestrate. */
	public Map<String,BagConditionalElement> ElementCollection = new HashMap<String,BagConditionalElement>();
	
	/** If set, when no more elements are running and none of the not yet executed elements can be run based on their
	 * execution conditions, the execution is terminated. */
	public boolean TerminateOnNoProgress=true;
	
	/** If set, everytime an element is completed the condition is evaluated and if it evaluates to true, the
	 * execution is terminated. */
	public ConditionTree TerminationCondition=null;
	
	/** The list of background workers that are currently running monitoring the execution of the elements that have
	 * been started. */
	private List<BackgroundExecution> workers = new ArrayList<BackgroundExecution>();
	
	/** Synchronization object for the list of workers. */
	private final Boolean synchWorker = new Boolean(false);

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
			List<Element> lstmembers = XMLUtils.GetChildElementsWithName(listnode, "bagElement");
			this.ElementCollection.clear();
			for (Element lstm : lstmembers)
			{
				BagConditionalElement pelem=new BagConditionalElement();
				pelem.FromXML(lstm);
				if(pelem.Element==null) throw new ExecutionSerializationException("Not valid serialization");
				if(this.ElementCollection.containsKey(pelem.Element.GetName())) throw new ExecutionSerializationException("Not valid serialization");
				this.ElementCollection.put(pelem.Element.GetName(),pelem);
			}
			Element terminationElem = XMLUtils.GetChildElementWithName((Element) XML, "termination");
			if (terminationElem == null) throw new ExecutionSerializationException("Not valid serialization");
			if(!XMLUtils.AttributeExists(terminationElem, "onNoProgress")) throw new ExecutionSerializationException("Not valid serialization");
			this.TerminateOnNoProgress=DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute(terminationElem, "onNoProgress"));
			Element conditionnode = XMLUtils.GetChildElementWithName(terminationElem, "conditionTree");
			if(conditionnode!=null)
			{
				ConditionTree nodeCondition = new ConditionTree();
				nodeCondition.FromXML(conditionnode);
				this.TerminationCondition=nodeCondition;
			}
			else this.TerminationCondition=null;
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
		return IPlanElement.PlanElementType.Bag;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID)) { return this; }
		for (BagConditionalElement elem : this.ElementCollection.values())
		{
			IPlanElement e = elem.Element.Locate(ID);
			if (e != null) { return e; }
		}
		return null;
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		for (BagConditionalElement elem : this.ElementCollection.values())
		{
			acts.addAll(elem.Element.LocateActionElements());
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
		for (BagConditionalElement elem : this.ElementCollection.values())
		{
			buf.append(elem.ToXML());
		}
		buf.append("</list>");
		buf.append("<termination onNoProgress=\""+this.TerminateOnNoProgress+"\">");
		if(this.TerminationCondition!=null) buf.append(this.TerminationCondition.ToXML());
		buf.append("</termination>");
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if (this.ElementCollection == null || this.ElementCollection.size() == 0) throw new ExecutionValidationException("Element collection not set");
		for (Map.Entry<String, BagConditionalElement> elem : this.ElementCollection.entrySet())
		{
			if(elem.getValue().Element==null) throw new ExecutionValidationException("Element cannot be null");
			if(!elem.getKey().equals(elem.getValue().Element.GetName())) throw new ExecutionValidationException("Element key is not the same as its name");
			elem.getValue().Element.Validate();
			if(elem.getValue().Condition!=null) elem.getValue().Condition.Validate();
		}
		if(this.TerminationCondition!=null) this.TerminationCondition.Validate();
		if(!this.TerminateOnNoProgress && this.TerminationCondition==null) throw new ExecutionValidationException("Termination condition must be set when termination on no progress is not set");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
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
		for (BagConditionalElement elem : this.ElementCollection.values())
		{
			vars.addAll(elem.Element.GetModifiedVariableNames());
			if(elem.Condition!=null) vars.addAll(elem.Condition.GetModifiedVariableNames());
		}
		if(this.TerminationCondition!=null) vars.addAll(this.TerminationCondition.GetModifiedVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		for (BagConditionalElement elem : this.ElementCollection.values())
		{
			vars.addAll(elem.Element.GetNeededVariableNames());
			if(elem.Condition!=null) vars.addAll(elem.Condition.GetNeededVariableNames());
		}
		if(this.TerminationCondition!=null) vars.addAll(this.TerminationCondition.GetNeededVariableNames());
		return vars;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.PlanElementBase#GetExtenderLogger()
	 */
	public Logger GetExtenderLogger()
	{
		return logger;
	}
	
	/**
	 * Gets the nodes that can be executed other that the ones that have already been executed or have already been picked
	 * for execution
	 * 
	 * @param Handle the execution handle
	 * @param Environment the environment that the conditions should be evaluated
	 * 
	 * @return the list of ids of the elements that can be executed
	 * 
	 * @throws ExecutionRunTimeException A runtime error occurred
	 */
	private List<String> GetNodesThatCanBeExecuted(ExecutionHandle Handle, IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		List<String> nodes=new ArrayList<String>();
		for(Map.Entry<String, BagConditionalElement> progressNode : this.ElementCollection.entrySet())
		{
			if(!progressNode.getValue().Executed && !progressNode.getValue().PickedForExecution)
			{
				if(progressNode.getValue().Condition==null || progressNode.getValue().Condition.EvaluateCondition(Handle, Environment))
				{
					logger.debug("Adding to nodes that can be executed node "+progressNode.getKey());
					progressNode.getValue().PickedForExecution=true;
					nodes.add(progressNode.getKey());
				}
			}
		}
		return nodes;
	}
	
	/**
	 * Gets the number of active workers.
	 * 
	 * @return the number
	 */
	private int GetNumberOfActiveWorkers()
	{
		int count=0;
		for(BackgroundExecution work : this.workers)
		{
			if(!work.ExecutionCompleted) count+=1;
		}
		return count;
	}
	
	/**
	 * Check if the bag should terminate its execution. The execution is terminated if no element can start being executed while
	 * no more running elements are left and the {@link BagPlanElement#TerminateOnNoProgress} is set or if the 
	 * {@link BagPlanElement#TerminationCondition} is set and it evaluates to true.
	 * 
	 * @param Handle the execution handle
	 * @param bgenv the environment to use for condition evaluation
	 * 
	 * @return true if the execution should be terminated
	 * 
	 * @throws ExecutionRunTimeException the execution run time exception
	 */
	private boolean TerminateBagExecution(ExecutionHandle Handle,BagConditionEnvironment bgEnv) throws ExecutionRunTimeException
	{
		if(this.TerminateOnNoProgress && !bgEnv.ProgressDoneInIteration)
		{
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),"No progress made in iteration. Breaking execution"));
				return true;
		}
		if(this.TerminationCondition!=null && this.TerminationCondition.EvaluateCondition(Handle,bgEnv))
		{
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),"Termination condition was met. Breaking execution"));
			return true;
		}
		return false;
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
		for(BagConditionalElement elem : this.ElementCollection.values())
		{
			elem.Executed=false;
			elem.PickedForExecution=false;
		}

		BagConditionEnvironment bgenv=new BagConditionEnvironment();
		bgenv.ElementCollection=this.ElementCollection;
		bgenv.workers=workers;
		bgenv.ProgressDoneInIteration=true; //to go past the first iteration
		int Count=0;
		int numberOfPreviousCompletedWorkers=0;
		this.StopClock(ClockType.Init);
		while(true)
		{
			this.StartClock(ClockType.Init);
			synchronized (this.synchWorker)
			{
				if(this.TerminateBagExecution(Handle, bgenv)) break;
				List<String> NodeNamesToExecute=this.GetNodesThatCanBeExecuted(Handle, bgenv);
				this.StopClock(ClockType.Init);
				this.StartClock(ClockType.Children);
				for(String nname : NodeNamesToExecute)
				{
					if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),"Starting Execution of sub element "+(Count+1)+" of "+this.ElementCollection.size()));
					BackgroundExecution bg = new BackgroundExecution(this.ElementCollection.get(nname).Element, Handle, this.synchWorker);
					this.workers.add(bg);
					Thread t = new Thread(bg);
					t.setName(BackgroundExecution.class.getName());
					t.setDaemon(true);
					t.start();
					Count+=1;
				}
				if(this.GetNumberOfActiveWorkers()==0) bgenv.ProgressDoneInIteration=false;
				else  bgenv.ProgressDoneInIteration=true;
				
				if(bgenv.ProgressDoneInIteration) try{this.synchWorker.wait();} catch (Exception ex){}

				int countCompleted=0;
				for(BackgroundExecution bg : this.workers)
				{
					if(bg.ExecutionCompleted)
					{
						this.ElementCollection.get(bg.Element.GetName()).Executed=true;
						countCompleted+=1;
					}
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
				this.StopClock(ClockType.Children);
				if(countCompleted>numberOfPreviousCompletedWorkers)
				{
					if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1+countCompleted, TotalSteps, "Completed Execution of a sub element ("+countCompleted+" finished of "+this.ElementCollection.size()+")", this.Name, hostname, port));
					numberOfPreviousCompletedWorkers=countCompleted;
				}
			}
		}
		this.workers.clear();
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),TotalSteps, TotalSteps, "Finishing Execution of " + this.Name));
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) Handle.EmitEvent(this.GetPerformanceEvent());
	}
}
