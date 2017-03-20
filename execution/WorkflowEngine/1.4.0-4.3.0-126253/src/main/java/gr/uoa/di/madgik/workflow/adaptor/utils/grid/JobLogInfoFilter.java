package gr.uoa.di.madgik.workflow.adaptor.utils.grid;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class JobLogInfoFilter scans through the output of a glite-WMS-job-logging-info command and parses
 * it into a series of records. The expected input is expected to be of the following form :
 * ********************************************************************** 
 * LOGGING INFORMATION: 
 *  
 * Printing info for the Job: https://lxshare0310.cern.ch:9000/C_CBUJKqc6Zqd4clQaCUTQ 
 *  
 *         - - - 
 *  Event: RegJob 
 * - source               =    UserInterface 
 * - timestamp            =    Fri Feb 20 10:30:16 2004 
 *         - - - 
 *  Event: Transfer 
 * - destination          =    NetworkServer 
 * - result               =    START 
 * - source               =    UserInterface 
 * - timestamp            =    Fri Feb 20 10:30:16 2004 
 *         - - - 
 *  Event: Transfer 
 * - destination          =    NetworkServer 
 * - result               =    OK 
 * - source               =    UserInterface 
 * - timestamp            =    Fri Feb 20 10:30:19 2004 
 *         - - - 
 *  Event: Accepted 
 * - source               =    NetworkServer 
 * - timestamp            =    Fri Feb 20 10:29:17 2004 
 *         - - - 
 *  Event: EnQueued 
 * - result               =    OK 
 * - source               =    NetworkServer 
 * - timestamp            =    Fri Feb 20 10:29:18 2004 
 * [...]
 * **********************************************************************
 * 
 * Every event extracted is send back to the caller in the form of an {@link ExecutionExternalProgressReportStateEvent}.
 * The call to {@link JobLogInfoFilter#Process(ExecutionHandle)} does not produce any output other than the events it emits
 * while it processes the provided input.
 * 
 * @author gpapanikos
 */
public class JobLogInfoFilter implements IExternalFilter
{
	
	/** The logger. */
	private static Logger logger=LoggerFactory.getLogger(JobLogInfoFilter.class);
	
	/** The Job log info variable name. */
	public String JobLogInfoVariableName=null;
	
	/** The Plan node id. */
	public String PlanNodeID=null;
	
	/** The Last event that was send. */
	private String LastEvent=null;
	
	/** Whether the last event has already been seen again. */
	private boolean ReachedLastEvent=false;
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#GetInputVariableNames()
	 */
	public Set<String> GetInputVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(JobLogInfoVariableName);
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#GetStoreOutputVariableName()
	 */
	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars=new HashSet<String>();
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#StoreOutput()
	 */
	public boolean StoreOutput()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if(this.JobLogInfoVariableName==null || this.JobLogInfoVariableName.trim().length()==0) throw new ExecutionValidationException("Filtered parameter names cannot be empty or null");
		if(this.PlanNodeID==null || this.PlanNodeID.trim().length()==0) throw new ExecutionValidationException("Needed value not set");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ValidateForOnlineFiltering()
	 */
	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle, java.util.Set)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.JobLogInfoVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.JobLogInfoVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.JobLogInfoVariableName)) throw new ExecutionValidationException("Needed variable not available");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ValidatePreExecutionForOnlineFiltering(gr.uoa.di.madgik.execution.engine.ExecutionHandle, java.util.Set)
	 */
	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ValidateForOnlineFiltering();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#SupportsOnLineFiltering()
	 */
	public boolean SupportsOnLineFiltering()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#Process(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			String currentPayload=null;
			logger.debug("Scanning log info");
			if(Handle.GetPlan().Config.ChokeProgressReporting) return null;
			this.ReachedLastEvent=false;
			String output=DataTypeUtils.GetValueAsString(Handle.GetPlan().Variables.Get(JobLogInfoVariableName).Value.GetValue());
			logger.debug("Log info is : \n"+output);
			BufferedReader reader=new BufferedReader(new StringReader(output));
			boolean inRec=false;
			StringBuilder buf=new StringBuilder();
			while(true)
			{
				String line=reader.readLine();
				logger.debug("read line : "+line);
				if(line==null) break;
				line=line.trim();
				if(!this.LineContainsPayload(line)) continue;
				else if(line.toLowerCase().startsWith("event:".toLowerCase()))
				{
					if(!inRec)
					{
						inRec=true;
						buf.append(line);
						buf.append("\n");
					}
					else
					{
						String payload=buf.toString();
						currentPayload=payload;
						if(this.ShouldSend(payload)) Handle.EmitEvent(new ExecutionExternalProgressReportStateEvent(this.PlanNodeID, "glite-wms-job-logging-info parsed output", payload));
						buf=new StringBuilder();
						buf.append(line);
						buf.append("\n");
					}
				}
				else if(line.startsWith("-") && inRec)
				{
					buf.append(line);
					buf.append("\n");
				}
				else continue;
			}
			if(buf.length()!=0)
			{
				String payload=buf.toString();
				currentPayload=payload;
				if(this.ShouldSend(payload)) Handle.EmitEvent(new ExecutionExternalProgressReportStateEvent(this.PlanNodeID, "glite-wms-job-logging-info parsed output", payload));
			}
			this.LastEvent=currentPayload;
			logger.debug("Finished Scanning log info");
			return null;
		}
		catch(Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve job status", ex);
		}
	}
	
	/**
	 * Checks if the provided event should be send back to the caller as a new event or not
	 * 
	 * @param payload the payload to check
	 * 
	 * @return true, if it should be send
	 */
	private boolean ShouldSend(String payload)
	{
		if(this.LastEvent==null)
		{
			return true;
		}
		else
		{
			if(!this.ReachedLastEvent)
			{
				if(this.LastEvent.equals(payload))
				{
					this.ReachedLastEvent=true;
				}
				return false;
			}
			else return true;
		}
	}
	
	/**
	 * Checks if the provided line contains payload other than whitespace and '-' characters
	 * 
	 * @param line the line
	 * 
	 * @return true, if the line contains characters other than whitespace and '-'
	 */
	private boolean LineContainsPayload(String line)
	{
		String tmp=new String(line);
		if(tmp==null || tmp.trim().length()==0) return false;
		while(tmp.contains("-")) tmp=tmp.replace('-', ' ');
		tmp=tmp.trim();
		if(tmp==null || tmp.trim().length()==0) return false;
		for(int i=0;i<tmp.length();i+=1) if(!Character.isWhitespace(tmp.charAt(i))) return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ProcessOnLine(java.lang.Object, java.util.Set)
	 */
	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<external type=\""+this.getClass().getName()+"\">");
		buf.append("<filteredVariable name=\""+this.JobLogInfoVariableName+"\"/>");
		buf.append("<planNodeID name=\""+this.PlanNodeID+"\"/>");
		buf.append("</external>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#FromXML(org.w3c.dom.Node)
	 */
	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists((Element)XML, "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if(!XMLUtils.AttributeExists(tmp, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.JobLogInfoVariableName=XMLUtils.GetAttribute(tmp, "name");
			tmp=XMLUtils.GetChildElementWithName(XML, "planNodeID");
			if(!XMLUtils.AttributeExists(tmp, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.PlanNodeID=XMLUtils.GetAttribute(tmp, "name");
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}
}
