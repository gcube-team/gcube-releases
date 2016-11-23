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
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterFilterBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExceptionExitCodeMaping;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.AttributedInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.EnvironmentKeyValue;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import gr.uoa.di.madgik.execution.utils.ThreadBufferedReader;
import gr.uoa.di.madgik.execution.utils.ThreadBufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
 * This class acts as an invocation wrapper of a shell script, or in general an external executable, enabling it to 
 * be directly included in the execution of a plan. The executable is defined using its absolute or relative path
 * in the {@link ShellPlanElement#Command} field. Any parameters it may require to be provided are specified
 * in the {@link ShellPlanElement#ArgumentParameters}. Additionally, any environmental variables that need to
 * be present for the execution of the command can be defined in {@link ShellPlanElement#Environment}. The execution of 
 * the external executable is done using a {@link ProcessBuilder}. It is also possible to define the data that will be 
 * written to the processe's standard input using the {@link ShellPlanElement#StdInParameter}. The standard
 * output and standard error of the process can be retrieved by setting the {@link ShellPlanElement#StdOutParameter}
 * and {@link ShellPlanElement#StdErrParameter} respectively. these inputs and outputs can be retrieved / dumped 
 * from /to files by setting the respective flags {@link ShellPlanElement#StdInIsFile}, {@link ShellPlanElement#StdOutIsFile}
 * and {@link ShellPlanElement#StdErrIsFile}
 * 
 * @author gpapanikos
 */
public class ShellPlanElement extends PlanElementBase
{
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ShellPlanElement.class);

	/** The ID of the element */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element */
	private String Name = ShellPlanElement.class.getSimpleName();
	
	/** The Triggers this element supports */
	public List<ContingencyTrigger> Triggers = new ArrayList<ContingencyTrigger>();
	
	/** The executable that is to be started */
	public String Command = null;
	
	/** The parameters that should be provided to the process's executable */
	public List<AttributedInputParameter> ArgumentParameters = new ArrayList<AttributedInputParameter>();
	
	/** The parameter to retrieve the products of the standard input. If the 
	 * {@link ShellPlanElement#StdInIsFile} is set then this parameter will hold
	 * the file that the input should be read from */
	public IInputParameter StdInParameter=null;
	
	/** A flag indicating that the standard input should be retrieved by a file */
	public boolean StdInIsFile=false;
	
	/** The parameter to store the product of the standard error output. If the 
	 * {@link ShellPlanElement#StdOutIsFile} is set then this parameter will hold
	 * the file that the output was dumped to */
	public IInputOutputParameter StdOutParameter=null;
	
	/** A flag indicating that the standard output products should be stored in a file */
	public boolean StdOutIsFile=false;
	
	/** A filter that can be applied on the fly in the output of the element's standard output */
	public ParameterFilterBase StdOutOnlineFilter=null;
	
	/** The parameter to store the product of the standard error output. If the 
	 * {@link ShellPlanElement#StdErrIsFile} is set then this parameter will hold
	 * the file that the output was dumped to */
	public IInputOutputParameter StdErrParameter=null;
	
	/** A flag indicating that the standard error output should be stored in a file */
	public boolean StdErrIsFile=false;
	
	/** A filter that can be applied on the fly in the output of the element's standard error */
	public ParameterFilterBase StdErrOnlineFilter=null;
	
	/** The parameter where to store the exit value of the process */
	public IOutputParameter StdExitValueParameter=null;
	
	/** A mapping of exit codes to the respective errors they should produce */
	public List<ExceptionExitCodeMaping> ExitCodeErrors=new ArrayList<ExceptionExitCodeMaping>();
	
	/** The environmental variables that must be set for the process being started */
	public List<EnvironmentKeyValue> Environment=new ArrayList<EnvironmentKeyValue>();

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
			Element comelem = XMLUtils.GetChildElementWithName(XML, "command");
			if (comelem == null) throw new ExecutionSerializationException("Not valid serialization of element");
			this.Command = XMLUtils.GetChildText(comelem);
			Element argselem = XMLUtils.GetChildElementWithName(XML, "arguments");
			if (argselem == null) throw new ExecutionSerializationException("Not valid serialization of element");
			List<Element> params = XMLUtils.GetChildElementsWithName(argselem, "attrParam");
			this.ArgumentParameters.clear();
			for (Element a : params)
			{
				AttributedInputParameter attrParam=new AttributedInputParameter();
				attrParam.FromXML(a);
				this.ArgumentParameters.add(attrParam);
			}
			Element elemstdtmp=XMLUtils.GetChildElementWithName(XML, "stdIn");
			if(elemstdtmp==null) throw new ExecutionSerializationException("Not valid serialization of element");
			Element elemparamtmp=XMLUtils.GetChildElementWithName(elemstdtmp, "param");
			if(elemparamtmp==null) this.StdInParameter=null;
			else
			{
				if(!XMLUtils.AttributeExists(elemstdtmp, "isFile"))throw new ExecutionSerializationException("Not valid serialization of element");
				this.StdInIsFile=DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute(elemstdtmp, "isFile"));
				IParameter param=ParameterUtils.GetParameter(elemparamtmp);
				if(!(param instanceof IInputParameter)) throw new ExecutionSerializationException("Not valid serialization of element");
				this.StdInParameter=(IInputParameter)param;
			}
			elemstdtmp=XMLUtils.GetChildElementWithName(XML, "stdOut");
			if(elemstdtmp==null) throw new ExecutionSerializationException("Not valid serialization of element");
			elemparamtmp=XMLUtils.GetChildElementWithName(elemstdtmp, "param");
			if(elemparamtmp==null) this.StdOutParameter=null;
			else
			{
				if(!XMLUtils.AttributeExists(elemstdtmp, "isFile"))throw new ExecutionSerializationException("Not valid serialization of element");
				this.StdOutIsFile=DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute(elemstdtmp, "isFile"));
				IParameter param=ParameterUtils.GetParameter(elemparamtmp);
				if(!(param instanceof IInputOutputParameter)) throw new ExecutionSerializationException("Not valid serialization of element");
				this.StdOutParameter=(IInputOutputParameter)param;
				Element onlinefilterelement=XMLUtils.GetChildElementWithName(elemstdtmp, "filter");
				if(onlinefilterelement!=null) this.StdOutOnlineFilter=ParameterUtils.GetParameterFilter(onlinefilterelement);
				else this.StdOutOnlineFilter=null;
			}
			elemstdtmp=XMLUtils.GetChildElementWithName(XML, "stdErr");
			if(elemstdtmp==null) throw new ExecutionSerializationException("Not valid serialization of element");
			elemparamtmp=XMLUtils.GetChildElementWithName(elemstdtmp, "param");
			if(elemparamtmp==null) this.StdErrParameter=null;
			else
			{
				if(!XMLUtils.AttributeExists(elemstdtmp, "isFile"))throw new ExecutionSerializationException("Not valid serialization of element");
				this.StdErrIsFile=DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute(elemstdtmp, "isFile"));
				IParameter param=ParameterUtils.GetParameter(elemparamtmp);
				if(!(param instanceof IInputOutputParameter)) throw new ExecutionSerializationException("Not valid serialization of element");
				this.StdErrParameter=(IInputOutputParameter)param;
				Element onlinefilterelement=XMLUtils.GetChildElementWithName(elemstdtmp, "filter");
				if(onlinefilterelement!=null) this.StdErrOnlineFilter=ParameterUtils.GetParameterFilter(onlinefilterelement);
				else this.StdErrOnlineFilter=null;
			}
			elemstdtmp=XMLUtils.GetChildElementWithName(XML, "stdExit");
			if(elemstdtmp==null) throw new ExecutionSerializationException("Not valid serialization of element");
			elemparamtmp=XMLUtils.GetChildElementWithName(elemstdtmp, "param");
			if(elemparamtmp==null) this.StdExitValueParameter=null;
			else
			{
				IParameter param=ParameterUtils.GetParameter(elemparamtmp);
				if(!(param instanceof IOutputParameter)) throw new ExecutionSerializationException("Not valid serialization of element");
				this.StdExitValueParameter=(IOutputParameter)param;
			}
			Element maps=XMLUtils.GetChildElementWithName(XML, "exitCodeErrors");
			if(maps==null) throw new ExecutionSerializationException("Not valid serialization of element");
			List<Element> errmaps=XMLUtils.GetChildElementsWithName(maps, "exitCodeError");
			this.ExitCodeErrors.clear();
			for(Element errmap : errmaps)
			{
				ExceptionExitCodeMaping em = new ExceptionExitCodeMaping();
				em.FromXML(errmap);
				this.ExitCodeErrors.add(em);
			}
			Element envs=XMLUtils.GetChildElementWithName(XML, "env");
			if(envs==null) throw new ExecutionSerializationException("Not valid serialization of element");
			List<Element> envlst=XMLUtils.GetChildElementsWithName(envs, "item");
			this.Environment.clear();
			for(Element envit : envlst)
			{
				if(!XMLUtils.AttributeExists(envit, "key")) throw new ExecutionSerializationException("Not valid serialization of element");
				String envitKey=XMLUtils.GetAttribute(envit, "key");
				String envitValue=XMLUtils.GetChildText(envit);
				if(envitKey==null || envitKey.trim().length()==0) throw new ExecutionSerializationException("Not valid serialization of element");
				if(envitValue==null || envitValue.trim().length()==0) throw new ExecutionSerializationException("Not valid serialization of element");
				this.Environment.add(new EnvironmentKeyValue(envitKey,envitValue));
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
		return IPlanElement.PlanElementType.Shell;
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
		buf.append("<command>" + this.Command + "</command>");
		buf.append("<arguments>");
		for (AttributedInputParameter argvar : this.ArgumentParameters)
		{
			buf.append(argvar.ToXML());
		}
		buf.append("</arguments>");
		if(this.StdInParameter==null) buf.append("<stdIn/>");
		else
		{
			buf.append("<stdIn isFile=\""+this.StdInIsFile+"\">");
			buf.append(this.StdInParameter.ToXML());
			buf.append("</stdIn>");
		}
		if(this.StdOutParameter==null) buf.append("<stdOut/>");
		else
		{
			buf.append("<stdOut isFile=\""+this.StdOutIsFile+"\">");
			buf.append(this.StdOutParameter.ToXML());
			if(this.StdOutOnlineFilter!=null)buf.append(this.StdOutOnlineFilter.ToXML());
			buf.append("</stdOut>");
		}
		if(this.StdErrParameter==null) buf.append("<stdErr/>");
		else
		{
			buf.append("<stdErr isFile=\""+this.StdErrIsFile+"\">");
			buf.append(this.StdErrParameter.ToXML());
			if(this.StdErrOnlineFilter!=null)buf.append(this.StdErrOnlineFilter.ToXML());
			buf.append("</stdErr>");
		}
		if(this.StdExitValueParameter==null) buf.append("<stdExit/>");
		else
		{
			buf.append("<stdExit>");
			buf.append(this.StdExitValueParameter.ToXML());
			buf.append("</stdExit>");
		}
		buf.append("<exitCodeErrors>");
		for(ExceptionExitCodeMaping map : this.ExitCodeErrors)
		{
			buf.append(map.ToXML());
		}
		buf.append("</exitCodeErrors>");
		buf.append("<env>");
		for(EnvironmentKeyValue envkv : this.Environment)
		{
			buf.append("<item key=\""+envkv.Key+"\">"+envkv.Value+"</item>");
		}
		buf.append("</env>");
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if (this.Command == null) throw new ExecutionValidationException("Command not set");
		if (this.ArgumentParameters == null) throw new ExecutionValidationException("Argument variable names list can be empty but not null");
		for(AttributedInputParameter param : this.ArgumentParameters) param.Validate();
		if(this.StdErrParameter!=null) this.StdErrParameter.Validate();
		if(this.StdExitValueParameter!=null) this.StdExitValueParameter.Validate();
		if(this.StdInParameter!=null) this.StdInParameter.Validate();
		if(this.StdOutParameter!=null) this.StdOutParameter.Validate();
		for(ExceptionExitCodeMaping map: this.ExitCodeErrors) map.Validate();
		if (this.Environment == null) throw new ExecutionValidationException("Environment key value list can be empty but not null");
		for(EnvironmentKeyValue envkv : this.Environment) if(envkv.Key==null ||envkv.Key.trim().length()==0 || 
				envkv.Value==null ||envkv.Value.trim().length()==0) throw new ExecutionValidationException("Environment key values must have non empty values");
		if(this.StdOutOnlineFilter!=null && !this.StdOutOnlineFilter.SupportsOnLineFiltering()) throw new ExecutionValidationException("Filteres provided for std out filtering need to support online filtering");
		if(this.StdErrOnlineFilter!=null && !this.StdErrOnlineFilter.SupportsOnLineFiltering()) throw new ExecutionValidationException("Filteres provided for std err filtering need to support online filtering");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		Set<String> ModifiedHere=this.GetModifiedVariableNames();
		for(AttributedInputParameter param : this.ArgumentParameters) param.ValidatePreExecution(Handle,ModifiedHere);
		if(this.StdErrParameter!=null) this.StdErrParameter.ValidatePreExecution(Handle,ModifiedHere);
		if(this.StdExitValueParameter!=null) this.StdExitValueParameter.ValidatePreExecution(Handle,ModifiedHere);
		if(this.StdInParameter!=null) this.StdInParameter.ValidatePreExecution(Handle,ModifiedHere);
		if(this.StdOutParameter!=null) this.StdOutParameter.ValidatePreExecution(Handle,ModifiedHere);
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportedContingencyTriggers()
	 */
	public IContingencyReaction.ReactionType[] SupportedContingencyTriggers()
	{
		return new IContingencyReaction.ReactionType[] { IContingencyReaction.ReactionType.None, IContingencyReaction.ReactionType.Retry };
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
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetModifiedVariableNames()
	 */
	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		for(AttributedInputParameter param : this.ArgumentParameters) vars.addAll(param.GetModifiedVariableNames());
		if(this.StdErrParameter!=null) vars.addAll(this.StdErrParameter.GetModifiedVariableNames());
		if(this.StdExitValueParameter!=null) vars.addAll(this.StdExitValueParameter.GetModifiedVariableNames());
		if(this.StdInParameter!=null) vars.addAll(this.StdInParameter.GetModifiedVariableNames());
		if(this.StdOutParameter!=null) vars.addAll(this.StdOutParameter.GetModifiedVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		for(AttributedInputParameter param : this.ArgumentParameters) vars.addAll(param.GetNeededVariableNames());
		if(this.StdErrParameter!=null) vars.addAll(this.StdErrParameter.GetNeededVariableNames());
		if(this.StdExitValueParameter!=null) vars.addAll(this.StdExitValueParameter.GetNeededVariableNames());
		if(this.StdInParameter!=null) vars.addAll(this.StdInParameter.GetNeededVariableNames());
		if(this.StdOutParameter!=null) vars.addAll(this.StdOutParameter.GetNeededVariableNames());
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
		
		try
		{
			this.RegisterToRunningActionElementsRestriction(Handle);
			logger.debug("Starting");
			this.CheckStatus(Handle);
			
			if(!Handle.GetPlan().Config.ChokeProgressReporting)
				Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1, 3, "Starting Execution of " + this.Name, this.Name, hostname, port));
			
			String []commArray=this.GetCommandArray(Handle);
			StringBuilder bufComm=new StringBuilder();
			
			for(String comArrItem : commArray)
				bufComm.append(comArrItem+" ");
			
			if(!Handle.GetPlan().Config.ChokeProgressReporting) 
				Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1, 3, "Starting Execution " + bufComm.toString(), this.Name, hostname, port));
			
			ProcessBuilder build = new ProcessBuilder(commArray);
			for(EnvironmentKeyValue envkv : this.Environment)
				build.environment().put(envkv.Key, envkv.Value);

			if(Handle.IsIsolationRequested()) {
				build=build.directory(Handle.GetIsolationInfo().GetBaseDirFile().getAbsoluteFile());
			}
			this.StopClock(ClockType.Init);
			this.StartClock(ClockType.Children);
			Object lockstd=new Object();
			Process p = null;
			ThreadBufferedWriter twriterInput=null;
			ThreadBufferedReader treaderOutput=null;
			ThreadBufferedReader treaderError=null;
			String output=null;
			String error=null;
			
			synchronized (lockstd)
			{
				p = build.start();
				twriterInput=this.WriteProcessInput(p.getOutputStream(), this.StdInParameter, Handle,lockstd);
				treaderOutput=this.GetProcessResult(p.getInputStream(),this.StdOutIsFile,(this.StdOutIsFile ? DataTypeUtils.GetValueAsString(this.StdOutParameter.GetParameterValue(Handle)) : null),Handle,lockstd,this.StdOutOnlineFilter);
				treaderError=this.GetProcessResult(p.getErrorStream(),this.StdErrIsFile,(this.StdErrIsFile ? DataTypeUtils.GetValueAsString(this.StdErrParameter.GetParameterValue(Handle)) : null),Handle,lockstd,this.StdErrOnlineFilter);
				while(true)
				{
					if(twriterInput.Done && treaderOutput.Done && treaderError.Done) break;
					try{
						lockstd.wait();
					}
					catch(Exception ex){}
					logger.debug("twriterInput.Done ("+twriterInput.Done+") treaderOutput.Done ("+treaderOutput.Done+") treaderError.Done ("+treaderError.Done+")");
				}
			}
			logger.debug("Waiting");
			p.waitFor();
			output=treaderOutput.Output;
			error=treaderError.Output;
			logger.debug("Done Waiting");
			
			if(!Handle.GetPlan().Config.ChokeProgressReporting) 
				Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),2, 3, "Retrieving return values of " + this.Name, this.Name, hostname, port));
			
			this.StopClock(ClockType.Children);
			this.StartClock(ClockType.Finilization);
			int exitvalue=p.exitValue();
			if(this.StdOutParameter!=null) this.StdOutParameter.SetParameterValue(Handle, output);
			if(this.StdErrParameter!=null) this.StdErrParameter.SetParameterValue(Handle, error);
			if(this.StdExitValueParameter!=null) this.StdExitValueParameter.SetParameterValue(Handle, exitvalue);
			this.MapExitCodeToError(exitvalue);
		}catch(IOException ex)
		{
			ExceptionUtils.ThrowTransformedException(ex);
		}catch(ExecutionValidationException ex)
		{
			ExceptionUtils.ThrowTransformedException(ex);
		}catch(InterruptedException ex)
		{
			ExceptionUtils.ThrowTransformedException(ex);
		}
		finally
		{
			this.UnregisterToRunningActionElementsRestriction(Handle);
			this.StopClock(ClockType.Finilization);
		}
		
		if(!Handle.GetPlan().Config.ChokeProgressReporting)
			Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),3, 3, "Finishing Execution of " + this.Name, this.Name, hostname, port));
		
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) 
			Handle.EmitEvent(this.GetPerformanceEvent());
	}
	
	/**
	 * Map exit code to error as specified by {@link ShellPlanElement#ExitCodeErrors}
	 * 
	 * @param ExitCode the exit code retrieved by the process
	 * 
	 * @throws ExecutionRunTimeException The runtime error that is defined by {@link ShellPlanElement#ExitCodeErrors} 
	 */
	private void MapExitCodeToError(int ExitCode) throws ExecutionRunTimeException
	{
		for(ExceptionExitCodeMaping map : this.ExitCodeErrors)
		{
			switch(map.TypeOfMapping)
			{
				case Equal:
				{
					if(map.ExitCode==ExitCode) ExceptionUtils.ThrowTransformedException(map.ErrorFullName, map.ErrorSimpleName, map.Message);
					break;
				}
				case NotEqual:
				{
					if(map.ExitCode!=ExitCode) ExceptionUtils.ThrowTransformedException(map.ErrorFullName, map.ErrorSimpleName, map.Message);
					break;
				}
				default:
				{
					throw new ExecutionRunTimeException("Unrecognized type of exit code mapping");
				}
			}
		}
	}
	
	/**
	 * Gets the command array to pass to the {@link ProcessBuilder}. 
	 * 
	 * @param Handle the execution handle
	 * 
	 * @return the command array
	 * 
	 * @throws ExecutionRunTimeException A runtime error occurred
	 * @throws ExecutionInternalErrorException An internal error occurred
	 * @throws ExecutionCancelException The execution was canceled
	 * @throws ExecutionBreakException The execution was terminated
	 */
	private String []GetCommandArray(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		String[] cmdarray = new String[this.ArgumentParameters.size() + 1];
		cmdarray[0] = Handle.GetIsolatedFile(new File(this.Command)).getAbsolutePath();
		for (int i = 0; i < this.ArgumentParameters.size(); i += 1)
		{
			try
			{
				cmdarray[i + 1] = DataTypeUtils.GetValueAsString(this.ArgumentParameters.get(i).Parameter.GetParameterValue(Handle));
				if(this.ArgumentParameters.get(i).IsFile)
				{
					cmdarray[i + 1]=Handle.GetIsolatedFile(new File(cmdarray[i + 1])).getAbsolutePath();
				}
			} catch (Exception ex)
			{
				ExceptionUtils.ThrowTransformedException(ex);
			}
		}
		return cmdarray;
	}
	
	/**
	 * Writes to the processe's input starting a background thread using the {@link ThreadBufferedWriter}. The writer
	 * is initialized and started but the method does not wait until it is completed to enable concurrent 
	 * execution with the rest of the threads retrieving output or writing input. 
	 * 
	 * @param stream the stream to write input to
	 * @param StdInParameter the parameter containing information to write to the standard input
	 * @param Handle the execution handle
	 * @param lockstd the synchronization object where the thread should notify to report completion
	 * 
	 * @return the thread buffered writer performing the process input writing
	 * 
	 * @throws ExecutionValidationException A validation error occurred
	 * @throws ExecutionRunTimeException a Runtime error occurred
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private ThreadBufferedWriter WriteProcessInput(OutputStream stream,IInputParameter StdInParameter, ExecutionHandle Handle,Object lockstd) throws ExecutionValidationException, ExecutionRunTimeException, IOException
	{
		ThreadBufferedWriter w= new ThreadBufferedWriter(stream, StdInParameter, this.StdInIsFile, Handle,lockstd);
		w.Do();
		return w;
	}
	
	/**
	 * Gets the process result starting a background thread using the {@link ThreadBufferedReader}. The reader
	 * is initialized and started but the method does not wait until it is completed to enable concurrent 
	 * execution with the rest of the threads retrieving output or writing input. 
	 * 
	 * @param stream the stream where the shell process writes output to
	 * @param IsFile if the output should be written to a file
	 * @param FileName the file name to write output to or null if the output should not be written to a file
	 * @param Handle the execution handle
	 * @param lockstd the synchronization object where the thread should notify to report completion
	 * 
	 * @return the thread buffered reader performing the result retrieval
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ExecutionRunTimeException A runtime error occurred
	 */
	private ThreadBufferedReader GetProcessResult(InputStream stream,boolean IsFile,String FileName,ExecutionHandle Handle,Object lockstd,ParameterFilterBase Filter) throws IOException, ExecutionRunTimeException
	{
		ThreadBufferedReader r=new ThreadBufferedReader(stream, IsFile, FileName, Handle,lockstd,Filter);
		r.Do();
		return r;
	}
}
