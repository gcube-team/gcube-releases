package gr.uoa.di.madgik.workflow.adaptor.utils.grid;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.JobStatusExternalFilter.JobStatus;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class JobStatusBreakLoopFilter processes its input that expects to be a serialization of one of the values
 * defined by {@link JobStatusExternalFilter.JobStatus}. Depending on the value of the input it returns either
 * true or false is returned. True is returned in case the input is one of the {@link JobStatusExternalFilter.JobStatus#Ready},
 * {@link JobStatusExternalFilter.JobStatus#Running}, {@link JobStatusExternalFilter.JobStatus#Scheduled}, 
 * {@link JobStatusExternalFilter.JobStatus#Submitted}, {@link JobStatusExternalFilter.JobStatus#Waiting}.
 * 
 * @author gpapanikos
 */
public class JobStatusBreakLoopFilter implements IExternalFilter
{
	
	/** The logger. */
	private static Logger logger=LoggerFactory.getLogger(JobStatusBreakLoopFilter.class);
	
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
			logger.debug("Input is "+output);
			JobStatus s=JobStatus.valueOf(output);
			switch(s)
			{
				case Aborted:
				case Cancelled:
				case Cleared:
				case Other:
				case Done:
				{
					logger.debug("returning false");
					return false;
				}
				case Ready:
				case Running:
				case Scheduled:
				case Submitted:
				case Waiting:
				{
					logger.debug("returning true");
					return true;
				}
				default:
				{
					throw new ExecutionValidationException("Unrecognized job status "+s);
				}
			}
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
