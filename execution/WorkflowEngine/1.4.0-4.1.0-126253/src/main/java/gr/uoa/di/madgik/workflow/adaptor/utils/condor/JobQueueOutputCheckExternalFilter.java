package gr.uoa.di.madgik.workflow.adaptor.utils.condor;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;

public class JobQueueOutputCheckExternalFilter implements IExternalFilter
{
	private static Logger logger=LoggerFactory.getLogger(JobQueueOutputCheckExternalFilter.class);
	public boolean StoreOutput=false;
	public String JobOutputVariableName=null;
	public String JobOutputUpdateVariableName=null;
	public boolean ClearUp=false;
	public boolean FinalOutcome=false;

	public Set<String> GetInputVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(JobOutputVariableName);
		vars.add(JobOutputUpdateVariableName);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(JobOutputUpdateVariableName);
		return vars;
	}

	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.JobOutputVariableName==null || this.JobOutputVariableName.trim().length()==0) throw new ExecutionValidationException("Filtered parameter names cannot be empty or null");
		if(this.StoreOutput) if(this.JobOutputUpdateVariableName==null || this.JobOutputUpdateVariableName.trim().length()==0) throw new ExecutionValidationException("Needed parameter is not provided");
	}

	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.JobOutputVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.JobOutputVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.JobOutputVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if(!Handle.GetPlan().Variables.Contains(this.JobOutputUpdateVariableName))throw new ExecutionValidationException("Needed parameter to store output not present");
	}

	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ValidateForOnlineFiltering();
	}

	public boolean SupportsOnLineFiltering()
	{
		return false;
	}

	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			if(this.ClearUp) return "";
			if(this.FinalOutcome)
			{
				String output=DataTypeUtils.GetValueAsString(Handle.GetPlan().Variables.Get(JobOutputVariableName).Value.GetValue());
				if(output.trim().length()==0) return false;
				return true;
			}
			String output=DataTypeUtils.GetValueAsString(Handle.GetPlan().Variables.Get(JobOutputVariableName).Value.GetValue());
			String update=DataTypeUtils.GetValueAsString(Handle.GetPlan().Variables.Get(JobOutputUpdateVariableName).Value.GetValue());
			logger.debug("Retrieved queue output is : \n"+output);
			logger.debug("Retrieved queue update is : \n"+update);
			if(output.trim().length()==0) return update.trim();
			else return update.trim() +"-"+ output.trim();
		}
		catch(Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve job queue output", ex);
		}
	}

	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders, ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

	public String ToXML() throws ExecutionSerializationException
	{
		String outputvarString="";
		if(this.JobOutputUpdateVariableName!=null) outputvarString="storeOutputName=\""+this.JobOutputUpdateVariableName+"\"";
		StringBuilder buf=new StringBuilder();
		buf.append("<external type=\""+this.getClass().getName()+"\" storeOutput=\""+Boolean.toString(this.StoreOutput)+"\" "+outputvarString+">");
		buf.append("<filteredVariable name=\""+this.JobOutputVariableName+"\"/>");
		buf.append("<clearUp value=\""+this.ClearUp+"\"/>");
		buf.append("<final value=\""+this.FinalOutcome+"\"/>");
		buf.append("</external>");
		return buf.toString();
	}

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
				this.JobOutputUpdateVariableName=XMLUtils.GetAttribute((Element)XML, "storeOutputName");
			}
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if(!XMLUtils.AttributeExists(tmp, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.JobOutputVariableName=XMLUtils.GetAttribute(tmp, "name");
			tmp=XMLUtils.GetChildElementWithName(XML, "clearUp");
			if(!XMLUtils.AttributeExists(tmp, "value")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.ClearUp=DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute(tmp, "value"));
			tmp=XMLUtils.GetChildElementWithName(XML, "final");
			if(!XMLUtils.AttributeExists(tmp, "value")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.FinalOutcome=DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute(tmp, "value"));
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}
}
