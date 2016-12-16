package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterSerializationFilter extends ParameterFilterBase
{
	private static Logger logger=LoggerFactory.getLogger(ParameterSerializationFilter.class);
	public String FilteredVariableName = null;
	public boolean StoreOutput = false;
	public String StoreOutputVariableName = null;
	public Set<String> TokenProvidingVariableNames=new HashSet<String>();
	
	public Set<String> GetInputVariableNames()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(FilteredVariableName);
		vars.addAll(TokenProvidingVariableNames);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(this.StoreOutputVariableName);
		return vars;
	}

	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		logger.debug("Starting filtering");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
		String template = null;
		try
		{
			template = ndt.Value.GetStringValue();
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve filter rule", ex);
		}
		logger.debug("filtering template is "+template);
		try
		{
			for (NamedDataType entry : Handle.GetPlan().Variables)
			{
				if(entry.Token==null || entry.Token.trim().length()==0 || !this.TokenProvidingVariableNames.contains(entry.Name)) continue;
				logger.debug("Checking if template contains token "+this.GetToken(entry.Token));
				while(template.contains(this.GetToken(entry.Token)))
				{
					logger.debug("Using token "+this.GetToken(entry.Token)+" with value "+entry.Value.GetStringValue());
					template = template.replace(this.GetToken(entry.Token), entry.Value.GetStringValue());
				}
				logger.debug("template now is "+template);
			}
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not evalute template rule", ex);
		}
		return template;
	}
	
	public Object ProcessOnLine(Object OnLineFilteredValue,Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		String template = null;
		try
		{
			template = DataTypeUtils.GetValueAsString(OnLineFilteredValue);
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve filter rule", ex);
		}
		try
		{
			for (NamedDataType entry : AdditionalValueProviders)
			{
				if(entry.Token==null || entry.Token.trim().length()==0) continue;
				logger.debug("Checking if template contains token "+this.GetToken(entry.Token));
				while(template.contains(this.GetToken(entry.Token)))
				{
					logger.debug("Using token "+this.GetToken(entry.Token)+" with value "+entry.Value.GetStringValue());
					template = template.replace(this.GetToken(entry.Token), entry.Value.GetStringValue());
				}
				logger.debug("template now is "+template);
			}
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not evalute template rule", ex);
		}
		return template;
	}

	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.FilteredVariableName == null || this.FilteredVariableName.trim().length() == 0) throw new ExecutionValidationException("Expected parameter name not provided");
		if (this.StoreOutput && (this.StoreOutputVariableName == null || this.StoreOutputVariableName.trim().length() == 0)) throw new ExecutionValidationException("No output variable name defined to store output");
		if(this.TokenProvidingVariableNames==null) throw new ExecutionValidationException("Token providing variables can be empty but cannot be null");
		this.TokenMappingValidate();
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
		if (!ndt.IsAvailable && !ExcludeAvailableConstraint.contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if(this.StoreOutput)
		{
			for(String storeVarName : this.GetStoreOutputVariableName())
			{
				if(!Handle.GetPlan().Variables.Contains(storeVarName))throw new ExecutionValidationException("Needed parameter to store output not present");
			}
		}
//		if(this.StoreOutput() && !Handle.GetPlan().Variables.Contains(this.GetStoreOutputVariableName()))throw new ExecutionValidationException("Needed parameter to store output not present");
	}
	
	public boolean SupportsOnLineFiltering()
	{
		return true;
	}

	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		if (!(this.FilteredVariableName == null || this.FilteredVariableName.trim().length() == 0)) throw new ExecutionValidationException("No parameter name should be provided for online filtering");
		if(this.StoreOutput) throw new ExecutionValidationException("Online filtering cannot store intermediate results");
		if (!(this.StoreOutputVariableName == null || this.StoreOutputVariableName.trim().length() == 0)) throw new ExecutionValidationException("No output variable name should be provided for online filtering");
		if(this.TokenProvidingVariableNames==null) throw new ExecutionValidationException("Token providing variables can be empty but cannot be null");
		if(this.TokenProvidingVariableNames.size()!=0) throw new ExecutionValidationException("No token providing set is expected");
	}

	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ValidateForOnlineFiltering();
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists((Element)XML, "type") ||
					!XMLUtils.AttributeExists((Element)XML, "order") ||
					!XMLUtils.AttributeExists((Element)XML, "storeOutput")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!ParameterFilterBase.FilterType.valueOf(XMLUtils.GetAttribute((Element)XML, "type")).equals(this.GetFilterType())) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "order"));
			this.StoreOutput=Boolean.parseBoolean(XMLUtils.GetAttribute((Element)XML, "storeOutput"));
			if(this.StoreOutput)
			{
				if(!XMLUtils.AttributeExists((Element)XML, "storeOutputName")) throw new ExecutionSerializationException("Provided serialization is not valid");
				this.StoreOutputVariableName=XMLUtils.GetAttribute((Element)XML, "storeOutputName");
			}
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.FilteredVariableName=XMLUtils.GetChildText(tmp);
			Element tokenprovelem=XMLUtils.GetChildElementWithName(XML, "tokenProviders");
			if(tokenprovelem==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			List<Element> varpovs=XMLUtils.GetChildElementsWithName(tokenprovelem, "var");
			this.TokenProvidingVariableNames.clear();
			for(Element varprov : varpovs)
			{
				if(!XMLUtils.AttributeExists(varprov, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
				this.TokenProvidingVariableNames.add(XMLUtils.GetAttribute(varprov, "name"));
			}
			this.TokenMappingFromXML(XML);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		String outputvarString="";
		if(this.StoreOutputVariableName!=null) outputvarString="storeOutputName=\""+this.StoreOutputVariableName+"\"";
		buf.append("<filter type=\""+this.GetFilterType().toString()+"\" order=\""+this.GetOrder()+"\" storeOutput=\""+Boolean.toString(this.StoreOutput)+"\" "+outputvarString+">");
		if(this.FilteredVariableName==null) buf.append("<filteredVariable/>");
		else buf.append("<filteredVariable>"+this.FilteredVariableName+"</filteredVariable>");
		buf.append("<tokenProviders>");
		for(String prov : this.TokenProvidingVariableNames)
		{
			buf.append("<var name=\""+prov+"\"/>");
		}
		buf.append("</tokenProviders>");
		buf.append(this.TokenMappingToXML());
		buf.append("</filter>");
		return buf.toString();
	}

	public FilterType GetFilterType()
	{
		return FilterType.Serialization;
	}

}
