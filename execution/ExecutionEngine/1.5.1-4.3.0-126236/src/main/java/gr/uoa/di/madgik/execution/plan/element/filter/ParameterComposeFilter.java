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
import java.util.UUID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterComposeFilter extends ParameterFilterBase
{
	public boolean StoreOutput=false;
	public String StoreOutputVariableName=null;
	public List<String> FilteredVariableNames=new ArrayList<String>();
	public boolean PromoteArray=false;

	public FilterType GetFilterType()
	{
		return ParameterFilterBase.FilterType.Compose;
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
		for(String s : this.FilteredVariableNames)
		{
			if (!Handle.GetPlan().Variables.Contains(s)) throw new ExecutionValidationException("Needed parameter not found");
			NamedDataType ndt = Handle.GetPlan().Variables.Get(s);
			if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(s)) throw new ExecutionValidationException("Needed variable not available");
		}
		if(this.StoreOutput())
		{
			for(String storeVarName : this.GetStoreOutputVariableName())
			{
				if(!Handle.GetPlan().Variables.Contains(storeVarName))throw new ExecutionValidationException("Needed parameter to store output not present");
				NamedDataType ndt = Handle.GetPlan().Variables.Get(storeVarName);
				if((ndt.Value.GetDataTypeEnum()!=DataTypes.Array && 
						ndt.Value.GetDataTypeEnum()!=DataTypes.Reflectable) 
						||(ndt.Value.GetDataTypeEnum()==DataTypes.Array && 
						DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray)ndt.Value).GetArrayClassCode())!=IDataType.DataTypes.Reflectable)) 
				{
					throw new ExecutionValidationException("Output variable is not of type reflectable or array of reflectables");
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
			if(this.ShouldConstructArray(Handle))
			{
				dtOut=new DataTypeArray();
				((DataTypeArray)dtOut).SetArrayClassCode("["+IDataType.DataTypes.Reflectable);
				int length=this.GetConstructedArrayLength(Handle);
				List<IDataType> items=new ArrayList<IDataType>();
				for(int i=0;i<length;i+=1) items.add(this.GetReflectable(Handle, i));
				dtOut.SetValue(items.toArray(new IDataType[0]));
			}
			else
			{
				dtOut=this.GetReflectable(Handle, -1);
			}
			return dtOut.GetValue();
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not extract value", ex);
		}
	}
	
	private DataTypeReflectable GetReflectable(ExecutionHandle Handle,int index) throws ExecutionRunTimeException, ExecutionValidationException
	{
		DataTypeReflectable dtOut=new DataTypeReflectable();
		Map<String, IDataType> extracted=this.GetDataTypesOfIndex(Handle, index);
		List<ReflectableItem> items=new ArrayList<ReflectableItem>();
		for(Map.Entry<String, IDataType> entry : extracted.entrySet())
		{
			ReflectableItem it=new ReflectableItem();
			it.Name=UUID.randomUUID().toString();
			it.Token=this.GetToken(entry.getKey());
			it.Value=entry.getValue();
			items.add(it);
		}
		dtOut.SetValue(items.toArray(new ReflectableItem[0]));
		return dtOut;
	}
	
	private Map<String, IDataType> GetDataTypesOfIndex(ExecutionHandle Handle,int index) throws ExecutionRunTimeException
	{
		Map<String,IDataType> vals=new HashMap<String, IDataType>();
		for(String s : this.FilteredVariableNames)
		{
			NamedDataType ndtin = Handle.GetPlan().Variables.Get(s);
			if(index>=0 && ndtin.Value.GetDataTypeEnum()==DataTypes.Array)
			{
				if(((DataTypeArray)ndtin.Value).GetItems().length<=index) throw new ExecutionRunTimeException("Index out of range");
				vals.put(ndtin.Token, ((DataTypeArray)ndtin.Value).GetItems()[index]);
			}
			else
			{
				vals.put(ndtin.Token, ndtin.Value);
			}
		}
		return vals;
	}
	
	private boolean ShouldConstructArray(ExecutionHandle Handle)
	{
		if(!this.PromoteArray) return false;
		for(String s : this.FilteredVariableNames)
		{
			if(Handle.GetPlan().Variables.Get(s).Value.GetDataTypeEnum()==DataTypes.Array) return true;
		}
		return false;
	}
	
	private int GetConstructedArrayLength(ExecutionHandle Handle)
	{
		int MinLength=Integer.MAX_VALUE;
		for(String s : this.FilteredVariableNames)
		{
			NamedDataType ndtin = Handle.GetPlan().Variables.Get(s);
			if(ndtin.Value.GetDataTypeEnum()==DataTypes.Array)
			{
				if(((DataTypeArray)ndtin.Value).GetItems().length<MinLength) MinLength=((DataTypeArray)ndtin.Value).GetItems().length;
			}
		}
		return MinLength;
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
		buf.append("<filter type=\""+this.GetFilterType().toString()+"\" order=\""+this.GetOrder()+"\" storeOutput=\""+Boolean.toString(this.StoreOutput)+"\" "+outputvarString+" promoteArray=\""+this.PromoteArray+"\">");
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
					!XMLUtils.AttributeExists((Element)XML, "promoteArray") ||
					!XMLUtils.AttributeExists((Element)XML, "storeOutput")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!ParameterFilterBase.FilterType.valueOf(XMLUtils.GetAttribute((Element)XML, "type")).equals(this.GetFilterType())) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "order"));
			this.StoreOutput=Boolean.parseBoolean(XMLUtils.GetAttribute((Element)XML, "storeOutput"));
			this.PromoteArray=Boolean.parseBoolean(XMLUtils.GetAttribute((Element)XML, "promoteArray"));
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
