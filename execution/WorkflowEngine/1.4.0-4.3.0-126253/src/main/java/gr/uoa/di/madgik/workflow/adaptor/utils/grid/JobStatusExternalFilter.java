package gr.uoa.di.madgik.workflow.adaptor.utils.grid;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
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
 * The Class JobStatusExternalFilter scans through the output of a glite-WMS-job-status command and parses
 * it to retrieve the actual status. The expected input is expected to be of the following form :
 * *************************************************************
 * BOOKKEEPING INFORMATION:
 * 
 * Status info for the Job : https://lxshare0310.cern.ch:9000/C_CBUJKqc6Zqd4clQaCUTQ
 * Current Status:     Done (Success)
 * Logged Reason(s):
 *     - Job got an error while in the CondorG queue.
 *     - Job terminated successfully
 * Exit code:          0
 * Status Reason:      Job terminated successfully
 * Destination:        dl01.di.uoa.gr:2119/jobmanager-lcgpbs-d4science
 * Submitted:          Thu Dec  3 18:37:20 2009 EET
 * *************************************************************
 * 
 * The {@link JobStatusExternalFilter#Process(ExecutionHandle)} output is one of the values
 * of {@link JobStatusExternalFilter.JobStatus}
 * 
 * @author gpapanikos
 */
public class JobStatusExternalFilter implements IExternalFilter
{
	
	/**
	 * The recognizable status a job can be in
	 */
	public enum JobStatus
	{
		
		/** Submitted. */
		Submitted,
		
		/** Waiting. */
		Waiting,
		
		/** Ready. */
		Ready,
		
		/** Scheduled. */
		Scheduled,
		
		/** Running. */
		Running,
		
		/** Done. */
		Done,
		
		/** Cleared. */
		Cleared,
		
		/** Aborted. */
		Aborted,
		
		/** Canceled. */
		Cancelled,
		
		/** Other, unspecified. */
		Other
	}
	
	/** The logger. */
	private static Logger logger=LoggerFactory.getLogger(JobStatusExternalFilter.class);
	
	/** The output of the filter should be stored or not */
	public boolean StoreOutput=false;
	
	/** The variable name containing the input to process */
	public String JobStatusVariableName=null;
	
	/** The variable name to store the output at */
	public String JobStatusOutputVariableName=null;

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#GetInputVariableNames()
	 */
	public Set<String> GetInputVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(JobStatusVariableName);
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#GetStoreOutputVariableName()
	 */
	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(JobStatusOutputVariableName);
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#StoreOutput()
	 */
	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if(this.JobStatusVariableName==null || this.JobStatusVariableName.trim().length()==0) throw new ExecutionValidationException("Filtered parameter names cannot be empty or null");
		if(this.StoreOutput) if(this.JobStatusOutputVariableName==null || this.JobStatusOutputVariableName.trim().length()==0) throw new ExecutionValidationException("Needed parameter is not provided");
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
		if (!Handle.GetPlan().Variables.Contains(this.JobStatusVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.JobStatusVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.JobStatusVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if(!Handle.GetPlan().Variables.Contains(this.JobStatusOutputVariableName))throw new ExecutionValidationException("Needed parameter to store output not present");
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
			String output=DataTypeUtils.GetValueAsString(Handle.GetPlan().Variables.Get(JobStatusVariableName).Value.GetValue());
			logger.debug("Retrieved status is : \n"+output);
			BufferedReader r=new BufferedReader(new StringReader(output));
			JobStatus s=JobStatus.Other;
			while(true)
			{
				String l=r.readLine();
				if(l==null) break;
				if(l.startsWith("Current Status:"))
				{
					String sts=l.substring("Current Status:".length()).trim().toLowerCase();
					if(sts.startsWith(JobStatus.Aborted.toString().toLowerCase())) s=JobStatus.Aborted;
					else if(sts.startsWith(JobStatus.Cancelled.toString().toLowerCase())) s=JobStatus.Cancelled;
					else if(sts.startsWith(JobStatus.Cleared.toString().toLowerCase())) s=JobStatus.Cleared;
					else if(sts.startsWith(JobStatus.Done.toString().toLowerCase())) s=JobStatus.Done;
					else if(sts.startsWith(JobStatus.Ready.toString().toLowerCase())) s=JobStatus.Ready;
					else if(sts.startsWith(JobStatus.Running.toString().toLowerCase())) s=JobStatus.Running;
					else if(sts.startsWith(JobStatus.Scheduled.toString().toLowerCase())) s=JobStatus.Scheduled;
					else if(sts.startsWith(JobStatus.Submitted.toString().toLowerCase()))s=JobStatus.Submitted;
					else if(sts.startsWith(JobStatus.Waiting.toString().toLowerCase())) s=JobStatus.Waiting;
					else s=JobStatus.Other;
				}
			}
			if(s.equals(JobStatus.Other)) throw new ExecutionValidationException("Could not retrieve job status");
			logger.debug("Parsed Status is : "+s.toString());
			return s.toString();
		}
		catch(Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve job status", ex);
		}
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
		String outputvarString="";
		if(this.JobStatusOutputVariableName!=null) outputvarString="storeOutputName=\""+this.JobStatusOutputVariableName+"\"";
		StringBuilder buf=new StringBuilder();
		buf.append("<external type=\""+this.getClass().getName()+"\" storeOutput=\""+Boolean.toString(this.StoreOutput)+"\" "+outputvarString+">");
		buf.append("<filteredVariable name=\""+this.JobStatusVariableName+"\"/>");
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
			if(!XMLUtils.AttributeExists((Element)XML, "type") ||
					!XMLUtils.AttributeExists((Element)XML, "storeOutput")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.StoreOutput=Boolean.parseBoolean(XMLUtils.GetAttribute((Element)XML, "storeOutput"));
			if(this.StoreOutput)
			{
				if(!XMLUtils.AttributeExists((Element)XML, "storeOutputName")) throw new ExecutionSerializationException("Provided serialization is not valid");
				this.JobStatusOutputVariableName=XMLUtils.GetAttribute((Element)XML, "storeOutputName");
			}
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if(!XMLUtils.AttributeExists(tmp, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.JobStatusVariableName=XMLUtils.GetAttribute(tmp, "name");
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

}
