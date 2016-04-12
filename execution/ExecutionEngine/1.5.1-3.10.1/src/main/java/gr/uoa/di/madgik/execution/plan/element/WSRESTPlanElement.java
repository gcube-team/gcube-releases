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
import gr.uoa.di.madgik.execution.plan.element.invocable.CallBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.IExecutionContext;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSExecutionContextConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSRESTCall;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSRESTProxyWrapper;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class acts as an invocation wrapper of a web service enabling it to be directly included in the 
 * execution of a plan. The web service that is to be used is specified using its end point in the 
 * {@link WSRESTPlanElement#ServiceEndPoint}. The actual invocations on this web service 
 * are specified by {@link WSRESTCall}s in the list of {@link WSRESTPlanElement#Calls}. To perform the actual 
 * invocations the {@link WSRESTProxyWrapper} utility class is used calling the {@link WSRESTProxyWrapper#Invoke(WSRESTCall)}
 * method.
 * 
 * @author gpapanikos
 */
public class WSRESTPlanElement extends PlanElementBase
{

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WSRESTPlanElement.class);
	
	/** The ID of the element */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element */
	private String Name = WSRESTPlanElement.class.getSimpleName();
	
	/** The Triggers. */
	public List<ContingencyTrigger> Triggers = new ArrayList<ContingencyTrigger>();
	
	/** The list of invocations that are to be performed in the service {@link WSRESTPlanElement#ServiceEndPoint} 
	 * in the order specified by the calls. Instances of this list are expected to be of
	 * type {@link WSRESTCall}*/
	public List<CallBase> Calls=new ArrayList<CallBase>();
	
	/** The Service end point to contact */
	public IInputOutputParameter ServiceEndPoint=null;
	
	/** Whether or not the WS {@link WSRESTPlanElement#ServiceEndPoint} should be provided with an
	 * {@link IExecutionContext}. In cases of WS invocations as this, to enable execution context
	 * both this flag needs to be set as well as the invocation envelop  */
	public boolean SupportsExecutionContext=false;
	
	/** If the {@link WSRESTPlanElement#SupportsExecutionContext} is set, this configuration field specifies
	 * the configuration needed to initialize the elements that are to be used to the provided execution context */
	public ExecutionContextConfigBase ExecutionContextConfig=null;

	
	public String scope;
	
	public String path;
	
	public String resourceID;
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
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
			Element cont = XMLUtils.GetChildElementWithName(XML, "triggers");
			this.Triggers.clear();
			if (cont != null)
			{
				List<Element> trigs = XMLUtils.GetChildElementsWithName(cont, "contingency");
				for (Element trig : trigs)
				{
					ContingencyTrigger t = new ContingencyTrigger();
					t.FromXML(trig);
					this.Triggers.add(t);
				}
			}
			Element endpointelem=XMLUtils.GetChildElementWithName(XML, "endpoint");
			if(endpointelem==null) throw new ExecutionSerializationException("Provided serialization not valid");
			Element eprelem=XMLUtils.GetChildElementWithName(endpointelem, "param");
			if(eprelem==null) throw new ExecutionSerializationException("Provided serialization not valid");
			this.ServiceEndPoint=(IInputOutputParameter)ParameterUtils.GetParameter(eprelem);
			Element callselem=XMLUtils.GetChildElementWithName(XML, "calls");
			if(callselem==null) throw new ExecutionSerializationException("Provided serialization not valid");
			List<Element> calls=XMLUtils.GetChildElementsWithName(callselem, "call");
			this.Calls.clear();
			for(Element c : calls)
			{
				this.Calls.add(PlanElementUtils.GetCall(c));
			}
			Element supExecCntxtElement=XMLUtils.GetChildElementWithName(XML, "context");
			if(supExecCntxtElement==null) throw new ExecutionSerializationException("Provided serialization not valid");
			if(!XMLUtils.AttributeExists(supExecCntxtElement, "supported")) throw new ExecutionSerializationException("Provided serialization not valid");
			this.SupportsExecutionContext=Boolean.parseBoolean(XMLUtils.GetAttribute(supExecCntxtElement, "supported"));
			Element execProxyElement=XMLUtils.GetChildElementWithName(supExecCntxtElement, "contextConfig");
			if(execProxyElement==null) this.ExecutionContextConfig=null;
			else 
			{
				this.ExecutionContextConfig=PlanElementUtils.GetExecutionContextConfig(execProxyElement);
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
		return IPlanElement.PlanElementType.WSREST;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID))
		{
			return this;
		}
		else
		{
			return null;
		}
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		acts.add(this);
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
		buf.append("<triggers>");
		for (ContingencyTrigger trig : this.Triggers)
		{
			buf.append(trig.ToXML());
		}
		buf.append("</triggers>");
		buf.append("<endpoint>");
		buf.append(this.ServiceEndPoint.ToXML());
		buf.append("</endpoint>");
		buf.append("<calls>");
		for(CallBase c : this.Calls)
		{
			buf.append(c.ToXML());
		}
		buf.append("</calls>");
		buf.append("<context supported=\""+this.SupportsExecutionContext+"\">");
		if(this.ExecutionContextConfig!=null) buf.append(this.ExecutionContextConfig.ToXML());
		buf.append("</context>");
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if(this.ServiceEndPoint==null) throw new ExecutionValidationException("Service end point not provided");
		this.ServiceEndPoint.Validate();
		if(this.Calls==null || this.Calls.size()==0) throw new ExecutionValidationException("No calls specified");
		for(CallBase c : this.Calls) c.Validate();
		if(this.SupportsExecutionContext && this.ExecutionContextConfig==null) throw new ExecutionValidationException("Declare support for context but not config provided");
		if(this.ExecutionContextConfig!=null)
		{
			if(!(this.ExecutionContextConfig instanceof WSExecutionContextConfig)) throw new ExecutionValidationException("Execution context supported by this element must be of type "+ExecutionContextConfigBase.ContextConfigType.WS.toString());
			this.ExecutionContextConfig.Validate();
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		Set<String> ExcludeAvailableConstraint=this.GetModifiedVariableNames();
		for(CallBase c : this.Calls) c.ValidatePreExecution(Handle,ExcludeAvailableConstraint);
		this.ServiceEndPoint.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportedContingencyTriggers()
	 */
	public IContingencyReaction.ReactionType[] SupportedContingencyTriggers()
	{
		return new IContingencyReaction.ReactionType[] { IContingencyReaction.ReactionType.None, IContingencyReaction.ReactionType.Retry, IContingencyReaction.ReactionType.Pick };
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
		try
		{
			this.ServiceEndPoint.SetParameterValue(Handle, Pick);
		}catch(Exception ex)
		{
			ExceptionUtils.ThrowTransformedRunTimeException(ex);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetModifiedVariableNames()
	 */
	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		for(CallBase c: this.Calls) vars.addAll(c.GetModifiedVariableNames());
		vars.addAll(this.ServiceEndPoint.GetModifiedVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		for(CallBase c: this.Calls) vars.addAll(c.GetNeededVariableNames());
		vars.addAll(this.ServiceEndPoint.GetNeededVariableNames());
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
		
		int TotalSteps=0;
		try
		{
			this.RegisterToRunningActionElementsRestriction(Handle);
			int CurrentStep=1;
			TotalSteps=2+this.Calls.size();
			logger.debug("Starting");
			this.CheckStatus(Handle);
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, TotalSteps, "Starting Execution of "+this.Name, this.Name, hostname, port));
			CurrentStep+=1;
			Collections.sort(this.Calls);
			WSRESTProxyWrapper wrapper=new WSRESTProxyWrapper(Handle,this.ID,DataTypeUtils.GetValueAsURL(this.ServiceEndPoint.GetParameterValue(Handle)),this.SupportsExecutionContext,this.ExecutionContextConfig);
			this.StopClock(ClockType.Init);
			this.StartClock(ClockType.Children);
			for(CallBase c : this.Calls)
			{
				this.StartClock(ClockType.Call);
				if(!(c instanceof WSRESTCall))
				{
					if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, TotalSteps, "Method "+c.MethodName+" could not be invoked because of inconsistent call type", this.Name, hostname, port));
					CurrentStep+=1;
				}
				else
				{
					if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),CurrentStep, TotalSteps, "Invoking Method "+c.MethodName, this.Name, hostname, port));
					CurrentStep+=1;
					wrapper.Invoke((WSRESTCall)c,Handle, scope, path, resourceID);
				}
				this.StopClock(ClockType.Call);
			}
			this.StopClock(ClockType.Children);
		}catch(ExecutionSerializationException ex) 
		{
			ExceptionUtils.ThrowTransformedException(ex);
		}catch(ExecutionValidationException ex)
		{
			ExceptionUtils.ThrowTransformedException(ex);
		}
		finally
		{
			this.StartClock(ClockType.Finilization);
			this.UnregisterToRunningActionElementsRestriction(Handle);
			this.StopClock(ClockType.Finilization);
		}
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),TotalSteps, TotalSteps, "Finishing Execution of "+this.Name, this.Name, hostname, port));
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) Handle.EmitEvent(this.GetPerformanceEvent());
	}
}
