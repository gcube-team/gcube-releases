package gr.uoa.di.madgik.workflow.adaptor.utils.condor;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

public class JobSubmitExternalFilter implements IExternalFilter
{
	private static Logger logger=LoggerFactory.getLogger(JobSubmitExternalFilter.class);
	public boolean StoreOutput=false;
	public String JobIdentifierVariableName=null;
	public String JobIdentifierOutputVariableName=null;

	public Set<String> GetInputVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(JobIdentifierVariableName);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(JobIdentifierOutputVariableName);
		return vars;
	}

	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.JobIdentifierVariableName==null || this.JobIdentifierVariableName.trim().length()==0) throw new ExecutionValidationException("Filtered parameter names cannot be empty or null");
		if(this.StoreOutput) if(this.JobIdentifierOutputVariableName==null || this.JobIdentifierOutputVariableName.trim().length()==0) throw new ExecutionValidationException("Needed parameter is not provided");
	}

	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.JobIdentifierVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.JobIdentifierVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.JobIdentifierVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if(!Handle.GetPlan().Variables.Contains(this.JobIdentifierOutputVariableName))throw new ExecutionValidationException("Needed parameter to store output not present");
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
			String output=DataTypeUtils.GetValueAsString(Handle.GetPlan().Variables.Get(JobIdentifierVariableName).Value.GetValue());
			logger.debug("Retrieved submission output is : \n"+output);
			BufferedReader r=new BufferedReader(new StringReader(output));
			String line;
			line = r.readLine(); // skip 'Submitting job(s).'
			line = r.readLine(); // skip 'Logging submit event(s).'
			line = r.readLine(); // read 'X job(s) submitted to cluster XXX'.
			Pattern pattern = Pattern.compile("(\\d*) job\\(s\\) submitted to cluster (\\d*)\\.");
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) throw new ExecutionRunTimeException("faied to parse the cluster number.");
			int noJobs = Integer.parseInt(matcher.group(1));
			String []clusters=matcher.group(2).split(",");
			logger.debug("Retrieved "+clusters.length+" number of clusters with "+noJobs+" jobs associated");
			return clusters;
		}
		catch(Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve job status", ex);
		}
	}

	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders, ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

	public String ToXML() throws ExecutionSerializationException
	{
		String outputvarString="";
		if(this.JobIdentifierOutputVariableName!=null) outputvarString="storeOutputName=\""+this.JobIdentifierOutputVariableName+"\"";
		StringBuilder buf=new StringBuilder();
		buf.append("<external type=\""+this.getClass().getName()+"\" storeOutput=\""+Boolean.toString(this.StoreOutput)+"\" "+outputvarString+">");
		buf.append("<filteredVariable name=\""+this.JobIdentifierVariableName+"\"/>");
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
				this.JobIdentifierOutputVariableName=XMLUtils.GetAttribute((Element)XML, "storeOutputName");
			}
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if(!XMLUtils.AttributeExists(tmp, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.JobIdentifierVariableName=XMLUtils.GetAttribute(tmp, "name");
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}
}
