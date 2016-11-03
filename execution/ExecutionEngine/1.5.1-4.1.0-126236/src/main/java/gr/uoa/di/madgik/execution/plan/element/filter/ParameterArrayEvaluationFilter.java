package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterArrayEvaluationFilter extends ParameterFilterBase
{
	public boolean StoreOutput=false;
	public String StoreOutputVariableName=null;
	public List<String> FilteredVariableNames=new ArrayList<String>();

	public FilterType GetFilterType()
	{
		return ParameterFilterBase.FilterType.ArrayEvaluation;
	}

	public Set<String> GetInputVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.FilteredVariableNames);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars=new HashSet<String>();
		if(this.StoreOutputVariableName!=null) vars.add(this.StoreOutputVariableName);
		return vars;
	}

	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.FilteredVariableNames==null || this.FilteredVariableNames.size()==0) throw new ExecutionValidationException("Filtered parameter names cannot be empty or null");
		if(this.StoreOutput) if(this.StoreOutputVariableName==null || this.StoreOutputVariableName.trim().length()==0) throw new ExecutionValidationException("Needed parameter is not provided");
		this.TokenMappingValidate();
	}

	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		DataTypes elementType = null;
		for(String s : this.FilteredVariableNames)
		{
			if (!Handle.GetPlan().Variables.Contains(s)) throw new ExecutionValidationException("Needed parameter not found");
			NamedDataType ndt = Handle.GetPlan().Variables.Get(s);
			if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(s)) throw new ExecutionValidationException("Needed variable not available");
			if(elementType == null) elementType = ndt.Value.GetDataTypeEnum();
			else if(elementType != ndt.Value.GetDataTypeEnum()) throw new ExecutionValidationException("Variables to be used for array evaluation not of the same type");
		}
		if(this.StoreOutput())
		{
			for(String storeVarName : this.GetStoreOutputVariableName())
			{
				if(!Handle.GetPlan().Variables.Contains(storeVarName))throw new ExecutionValidationException("Needed parameter to store output not present");
				NamedDataType ndt = Handle.GetPlan().Variables.Get(storeVarName);
				if(ndt.Value.GetDataTypeEnum()!=DataTypes.Array) 
				{
					throw new ExecutionValidationException("Output variable is not of array type");
				}
			}
		}
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
			IDataType dtOut=null;

			dtOut=new DataTypeArray();
			((DataTypeArray)dtOut).SetArrayClassCode("["+ Handle.GetPlan().Variables.Get(this.FilteredVariableNames.get(0)).Value.GetDataTypeEnum());
			int length=this.FilteredVariableNames.size();
			List<IDataType> items=new ArrayList<IDataType>();
			for(int i=0;i<length;i+=1) items.add(Handle.GetPlan().Variables.Get(this.FilteredVariableNames.get(i)).Value);
			dtOut.SetValue(items.toArray(new IDataType[0]));
		
			return dtOut.GetValue();
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not extract value", ex);
		}
	}

	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		String outputvarString="";
		if(this.StoreOutputVariableName!=null) outputvarString="storeOutputName=\""+this.StoreOutputVariableName+"\"";
		buf.append("<filter type=\""+this.GetFilterType().toString()+"\" order=\""+this.GetOrder()+"\" storeOutput=\""+Boolean.toString(this.StoreOutput)+"\" "+outputvarString+">");
		buf.append("<filteredList>");
		for(String s : this.FilteredVariableNames) buf.append("<filteredVariable name=\""+s+"\"/>");
		buf.append("</filteredList>");
		buf.append(this.TokenMappingToXML());
		buf.append("</filter>");
		return buf.toString();
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
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredList");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			List<Element> filteredvars=XMLUtils.GetChildElementsWithName(tmp, "filteredVariable");
			if(filteredvars==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.FilteredVariableNames.clear();
			for(Element filtervar : filteredvars)
			{
				if(!XMLUtils.AttributeExists(filtervar, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
				this.FilteredVariableNames.add(XMLUtils.GetAttribute(filtervar, "name"));
			}
			this.TokenMappingFromXML(XML);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

}
