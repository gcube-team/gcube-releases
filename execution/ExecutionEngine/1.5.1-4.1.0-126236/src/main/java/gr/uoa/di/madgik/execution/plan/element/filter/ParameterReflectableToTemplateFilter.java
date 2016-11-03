package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.DataTypeReflectable;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ReflectableAnalyzer;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterReflectableToTemplateFilter extends ParameterFilterBase
{
	private static Logger logger=LoggerFactory.getLogger(ParameterReflectableToTemplateFilter.class);
	public boolean StoreOutput=false;
	public String StoreOutputVariableName=null;
	public String FilteredVariableName = null;
	public String TemplateVariableName = null;

	public Set<String> GetInputVariableNames()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(FilteredVariableName);
		vars.add(TemplateVariableName);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(this.StoreOutputVariableName);
		return vars;
	}

	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.FilteredVariableName==null || this.FilteredVariableName.trim().length()==0) throw new ExecutionValidationException("Expected parameter name not provided");
		if(this.TemplateVariableName==null || this.TemplateVariableName.trim().length()==0) throw new ExecutionValidationException("Expected parameter name not provided");
		if(this.StoreOutput && (this.StoreOutputVariableName==null || this.StoreOutputVariableName.trim().length()==0)) throw new ExecutionValidationException("No output variable name defined to store output");
		this.TokenMappingValidate();
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if (!Handle.GetPlan().Variables.Contains(this.TemplateVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		ndt = Handle.GetPlan().Variables.Get(this.TemplateVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.TemplateVariableName)) throw new ExecutionValidationException("Needed variable not available");
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
		return false;
	}

	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ValidateForOnlineFiltering();
	}

	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
			NamedDataType ndtx = Handle.GetPlan().Variables.Get(this.TemplateVariableName);
			if(!((ndt.Value instanceof DataTypeReflectable) || 
			((ndt.Value instanceof DataTypeArray) && 
			(DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray)ndt.Value).GetArrayClassCode())).equals(IDataType.DataTypes.Reflectable))))
				throw new ExecutionValidationException("The filter can only be applied on reflectable input filtered variable or arrays of them");
			String template=ndtx.Value.GetStringValue();
			logger.debug("template string is "+template);
			logger.debug("Value variable is "/*+ndt.Value.ToXML()*/); //comment out unnecessary toXML
			if(ndt.Value instanceof DataTypeArray)
			{
				StringBuilder buf=new StringBuilder();
				for(IDataType dt : (DataTypeArray)ndt.Value)
				{
					if(!(dt instanceof DataTypeReflectable)) throw new ExecutionValidationException("The array element is not of required type");
					ReflectableAnalyzer anal=new ReflectableAnalyzer();
					String reflString = anal.PopulateTemplateFromReflectable(template, (DataTypeReflectable)dt,this.TokenMapping);
					buf.append(reflString);
				}
				String reflString=buf.toString();
				logger.debug("Populated string is "+reflString);
				return reflString;
			}
			else if(ndt.Value instanceof DataTypeReflectable)
			{
				ReflectableAnalyzer anal=new ReflectableAnalyzer();
				String reflString = anal.PopulateTemplateFromReflectable(template, (DataTypeReflectable)ndt.Value,this.TokenMapping);
				logger.debug("Populated string is "+reflString);
				return reflString;
			}
			else throw new ExecutionValidationException("The filter can only be applied on reflectable input filtered variable or arrays of them");
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not extract value", ex);
		}
	}

	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
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
			tmp=XMLUtils.GetChildElementWithName(XML, "templateVariable");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.TemplateVariableName=XMLUtils.GetChildText(tmp);
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
		if(this.TemplateVariableName==null) buf.append("<templateVariable/>");
		else buf.append("<templateVariable>"+this.TemplateVariableName+"</templateVariable>");
		buf.append(this.TokenMappingToXML());
		buf.append("</filter>");
		return buf.toString();
	}
	
	public FilterType GetFilterType()
	{
		return FilterType.ReflectableToTemplate;
	}
}
