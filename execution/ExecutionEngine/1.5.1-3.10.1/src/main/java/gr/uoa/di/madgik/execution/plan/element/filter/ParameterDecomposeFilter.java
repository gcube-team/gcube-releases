package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.DataTypeReflectable;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.datatype.ReflectableItem;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterDecomposeFilter extends ParameterFilterBase
{
	public String FilteredVariableName=null;
	public List<String> OutputVariableNames=new ArrayList<String>();

	public FilterType GetFilterType()
	{
		return ParameterFilterBase.FilterType.Decompose;
	}

	public Set<String> GetInputVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(this.FilteredVariableName);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.OutputVariableNames);
		return vars;
	}

	public boolean StoreOutput()
	{
		//The decomposed populated variables are stored internally by default
		return false;
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.FilteredVariableName==null || this.FilteredVariableName.trim().length()==0) throw new ExecutionValidationException("Expected parameter name not provided");
		if(this.OutputVariableNames==null || this.OutputVariableNames.size()==0) throw new ExecutionValidationException("Output parameter names cannot be empty or null");
		this.TokenMappingValidate();
	}

	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if((ndt.Value.GetDataTypeEnum()!=DataTypes.Array && 
				ndt.Value.GetDataTypeEnum()!=DataTypes.Reflectable) 
				||(ndt.Value.GetDataTypeEnum()==DataTypes.Array && 
				DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray)ndt.Value).GetArrayClassCode())!=IDataType.DataTypes.Reflectable)) 
		{
			throw new ExecutionValidationException("Filtered variable is not of type reflectable or array of reflectables");
		}
		for(String storeVarName : this.GetStoreOutputVariableName())
		{
			if(!Handle.GetPlan().Variables.Contains(storeVarName))throw new ExecutionValidationException("Needed parameter to store output not present");
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
			Set<String> InterestingTokens=new HashSet<String>();
			for(String s : this.OutputVariableNames) InterestingTokens.add(this.GetToken(Handle.GetPlan().Variables.Get(s).Token));
			NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
			if(ndt.Value.GetDataTypeEnum()==DataTypes.Reflectable)
			{
				Map<String,IDataType> extracted = this.Process((DataTypeReflectable)ndt.Value,InterestingTokens);
				for(String s : this.OutputVariableNames)
				{
					NamedDataType ndtOut=Handle.GetPlan().Variables.Get(s);
					if(extracted.containsKey(this.GetToken(ndtOut.Token)) && ndtOut.Value.GetDataTypeEnum().equals(extracted.get(this.GetToken(ndtOut.Token)).GetDataTypeEnum()))
					{
						Handle.GetPlan().Variables.Update(ndtOut.Name, extracted.get(this.GetToken(ndtOut.Token)).GetValue());
					}
				}
			}
			else if(ndt.Value.GetDataTypeEnum()==DataTypes.Array && DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray)ndt.Value).GetArrayClassCode())==IDataType.DataTypes.Reflectable)
			{
				Map<String,List<IDataType>> tmpHolder=new HashMap<String, List<IDataType>>();
				for(String s : this.OutputVariableNames)
				{
					NamedDataType ndtOut=Handle.GetPlan().Variables.Get(s);
					tmpHolder.put(this.GetToken(ndtOut.Token), new ArrayList<IDataType>());
				}
				for(IDataType arrItem : ((DataTypeArray)ndt.Value).GetItems())
				{
					if(arrItem.GetDataTypeEnum()!=IDataType.DataTypes.Reflectable) throw new ExecutionRunTimeException("Filtered variable is not of the expected type");
					Map<String,IDataType> extracted = this.Process((DataTypeReflectable)arrItem,InterestingTokens);
					for(String s : this.OutputVariableNames)
					{
						NamedDataType ndtOut=Handle.GetPlan().Variables.Get(s);
						if(extracted.containsKey(this.GetToken(ndtOut.Token)))
						{
							IDataType extr=extracted.get(this.GetToken(ndtOut.Token));
							if(tmpHolder.containsKey(this.GetToken(ndtOut.Token)))
							{
								tmpHolder.get(this.GetToken(ndtOut.Token)).add(extr);
							}
						}
					}
				}
				for(String s : this.OutputVariableNames)
				{
					NamedDataType ndtOut=Handle.GetPlan().Variables.Get(s);
					List<IDataType> vals=tmpHolder.get(this.GetToken(ndtOut.Token));
					if(vals.size()==0) continue;
					if(ndtOut.Value.GetDataTypeEnum()!=IDataType.DataTypes.Array && ndtOut.Value.GetDataTypeEnum().equals(vals.get(0).GetDataTypeEnum()))
					{
						Handle.GetPlan().Variables.Update(ndtOut.Name, vals.get(0).GetValue());
					}
					else if(ndtOut.Value.GetDataTypeEnum()==IDataType.DataTypes.Array && DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray)ndtOut.Value).GetArrayClassCode()).equals(vals.get(0).GetDataTypeEnum()))
					{
						Handle.GetPlan().Variables.Update(ndtOut.Name, vals.toArray(new IDataType[0]));
					}
				}
			}
			else throw new ExecutionRunTimeException("Filtered variable is not of the expected type");
			return null;
		} catch (ExecutionValidationException ex)
		{
			throw new ExecutionRunTimeException("Could not extract value", ex);
		}
	}
	
	private Map<String,IDataType> Process(DataTypeReflectable reflectable,Set<String> InterestingTokens)
	{
		//scans only top level tokens of reflectable. Not in deeper level
		Map<String,IDataType> extracted=new HashMap<String,IDataType>();
		for(ReflectableItem item : reflectable.GetItems())
		{
			if(InterestingTokens.contains(this.GetToken(item.Token)))
			{
				extracted.put(this.GetToken(item.Token), item.Value);
			}
		}
		return extracted;
	}

	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<filter type=\""+this.GetFilterType().toString()+"\" order=\""+this.GetOrder()+"\">");
		if(this.FilteredVariableName==null) buf.append("<filteredVariable/>");
		else buf.append("<filteredVariable>"+this.FilteredVariableName+"</filteredVariable>");
		buf.append("<outputList>");
		for(String s : this.OutputVariableNames) buf.append("<outputVariable name=\""+s+"\"/>");
		buf.append("</outputList>");
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
					!XMLUtils.AttributeExists((Element)XML, "order")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!ParameterFilterBase.FilterType.valueOf(XMLUtils.GetAttribute((Element)XML, "type")).equals(this.GetFilterType())) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "order"));
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.FilteredVariableName=XMLUtils.GetChildText(tmp);
			tmp=XMLUtils.GetChildElementWithName(XML, "outputList");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			List<Element> outvars=XMLUtils.GetChildElementsWithName(tmp, "outputVariable");
			if(outvars==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.OutputVariableNames.clear();
			for(Element outvar : outvars)
			{
				if(!XMLUtils.AttributeExists(outvar, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
				this.OutputVariableNames.add(XMLUtils.GetAttribute(outvar, "name"));
			}
			this.TokenMappingFromXML(XML);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

}
