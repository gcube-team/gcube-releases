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
import gr.uoa.di.madgik.execution.plan.element.invocable.CheckpointConfig;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import gr.uoa.di.madgik.grs.proxy.local.LocalStoreWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreWriterProxy;

import java.net.URI;
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
 * This class is meant to be used in order to create a restoration point for the data used in the context of an 
 * execution plan. These currently include only a set of {@link URI} proxy locators that are expected as inputs in
 * the {@link CheckpointPlanElement#Inputs}. These are passed to {@link StoreClient#Init(gr.uoa.di.madgik.grs.store.share.locator.IStoreLocator.LocatorType, List, gr.uoa.di.madgik.grs.store.RecordStoreConfig.SinkInputAccessMultiplexType, gr.uoa.di.madgik.grs.store.RecordStoreConfig.SinkInputAccessDepthType, int, int, int, float, long)}
 * along with configuration retrieved by {@link CheckpointPlanElement#Config} and the store locator
 * is created.
 * 
 * @author gpapanikos
 */
public class CheckpointPlanElement extends PlanElementBase
{
	
	/** The logger. */
	private static final Logger logger=LoggerFactory.getLogger(CheckpointPlanElement.class); 
	
	/** The ID of the element. */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element */
	private String Name = CheckpointPlanElement.class.getSimpleName();
	
	/** The contingency Triggers supported */
	public List<ContingencyTrigger> Triggers=new ArrayList<ContingencyTrigger>();
	
	/** The Inputs that store the locators to be checkpointed. */
	public List<IInputParameter> Inputs=new ArrayList<IInputParameter>();
	
	/** The Output where to store the created store locator */
	public IOutputParameter Output=null;
	
	/** The configuration to use when creating the store locator */
	public CheckpointConfig Config=new CheckpointConfig();

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#FromXML(java.lang.String)
	 */
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
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
			Element cont=XMLUtils.GetChildElementWithName(XML, "triggers");
			this.Triggers.clear();
			if(cont!=null)
			{
				List<Element> trigs = XMLUtils.GetChildElementsWithName(cont, "contingency");
				for(Element trig : trigs)
				{
					ContingencyTrigger t=new ContingencyTrigger();
					t.FromXML(trig);
					this.Triggers.add(t);
				}
			}
			Element outelement=XMLUtils.GetChildElementWithName(XML, "output");
			if(outelement==null) throw new ExecutionSerializationException("Provided serialization not valid");
			Element parelem=XMLUtils.GetChildElementWithName(outelement, "param");
			if(parelem==null) throw new ExecutionSerializationException("Provided serialization not valid");
			this.Output=(IOutputParameter)ParameterUtils.GetParameter(parelem);
			Element inselement=XMLUtils.GetChildElementWithName(XML, "inputs");
			if(inselement==null) throw new ExecutionSerializationException("Provided serialization not valid");
			List<Element> inputsparamselement=XMLUtils.GetChildElementsWithName(inselement, "param");
			for(Element inpar : inputsparamselement)
			{
				IInputParameter partmp=(IInputParameter)ParameterUtils.GetParameter(inpar);
				this.Inputs.add(partmp);
			}
			Element checkpointConfigElement=XMLUtils.GetChildElementWithName(XML, "checkpointConfig");
			if(checkpointConfigElement==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Config=new CheckpointConfig();
			this.Config.FromXML(checkpointConfigElement);
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
		return IPlanElement.PlanElementType.Checkpoint;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID))
		{
			return this;
		} else
		{
			return null;
		}
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		return acts;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SetName(java.lang.String)
	 */
	public void SetName(String Name)
	{
		this.Name=Name;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<planElement type=\""+this.GetPlanElementType().toString()+"\" id=\""+this.GetID()+"\" name=\""+this.GetName()+"\">");
		buf.append("<triggers>");
		for(ContingencyTrigger trig : this.Triggers)
		{
			buf.append(trig.ToXML());
		}
		buf.append("</triggers>");
		buf.append("<output>");
		buf.append(this.Output.ToXML());
		buf.append("</output>");
		buf.append("<inputs>");
		for(IInputParameter par : this.Inputs) buf.append(par.ToXML());
		buf.append("</inputs>");
		buf.append(this.Config.ToXML());
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if(this.Inputs==null || this.Inputs.size()==0) throw new ExecutionValidationException("Input list cannot be empty");
		if(this.Output==null) throw new ExecutionValidationException("Output parameter not defined");
		for(IInputParameter par : this.Inputs) par.Validate();
		this.Output.Validate();
		this.Config.Validate();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportedContingencyTriggers()
	 */
	public IContingencyReaction.ReactionType[] SupportedContingencyTriggers()
	{
		return new IContingencyReaction.ReactionType[]{IContingencyReaction.ReactionType.None,IContingencyReaction.ReactionType.Retry};
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportsContingencyTriggers()
	 */
	public boolean SupportsContingencyTriggers()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetContingencyTriggers()
	 */
	public List<ContingencyTrigger> GetContingencyTriggers()
	{
		return this.Triggers;
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
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.Output.GetNeededVariableNames());
		for(IInputParameter par : this.Inputs) vars.addAll(par.GetNeededVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetModifiedVariableNames()
	 */
	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.Output.GetModifiedVariableNames());
		for(IInputParameter par : this.Inputs) vars.addAll(par.GetModifiedVariableNames());
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
	public void ExecuteExtender(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionCancelException, ExecutionInternalErrorException,ExecutionBreakException
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
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1, 2, "Starting Execution of "+this.Name, this.Name, hostname, port));
		try
		{
			List<URI> locators=this.RetrieveInputs(Handle);
			this.StopClock(ClockType.Init);
			this.StartClock(ClockType.Children);
//			StoreClient.SetStoreServerConfig(this.Config.GetStoreServerConfig());
			URI storeLocator=null;
			switch(this.Config.TypeOfProxy)
			{
				case LocalStore:
				{
					storeLocator = LocalStoreWriterProxy.store(locators.toArray(new URI[0]), this.Config.TypeOfMultiplex, this.Config.TimeoutValue, this.Config.TimeoutUnit);
					break;
				}
				case TCPStore:
				{
					storeLocator = TCPStoreWriterProxy.store(locators.toArray(new URI[0]), this.Config.TypeOfMultiplex, this.Config.TimeoutValue, this.Config.TimeoutUnit);
					break;
				}
				case Local:
				case TCP:
				default:
				{
					throw new ExecutionRunTimeException("Only Local and TCP Store proxies supported");
				}
			}
			this.StopClock(ClockType.Children);
			this.StartClock(ClockType.Finilization);
			logger.debug("Storing Output of checkpoint operation "+storeLocator);
			this.Output.SetParameterValue(Handle, storeLocator);
			this.StopClock(ClockType.Finilization);
		}catch(Exception ex)
		{
			ExceptionUtils.ThrowTransformedException(ex);
		}
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),2, 2, "Finishing Execution of "+this.Name, this.Name, hostname, port));
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) Handle.EmitEvent(this.GetPerformanceEvent());
	}
	
	/**
	 * Retrieve inputs as a list of proxy locators
	 * 
	 * @param Handle the execution handle
	 * 
	 * @return the list of proxy locators to be checkpointed
	 * 
	 * @throws ExecutionRunTimeException A runtime error occurred
	 * @throws ExecutionValidationException A validation error occurred
	 */
	private List<URI> RetrieveInputs(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionValidationException
	{
		List<URI> inputs=new ArrayList<URI>();
		for(IInputParameter input : this.Inputs)
		{
			Object in= input.GetParameterValue(Handle);
			if(!(in instanceof URI)) throw new ExecutionValidationException("Provided input not instance of required locator interface");
			inputs.add((URI)in);
		}
		return inputs;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		Set<String> ExcludeAvailableConstraint=this.GetModifiedVariableNames();
		for(IInputParameter par : this.Inputs) par.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.Output.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}
}
