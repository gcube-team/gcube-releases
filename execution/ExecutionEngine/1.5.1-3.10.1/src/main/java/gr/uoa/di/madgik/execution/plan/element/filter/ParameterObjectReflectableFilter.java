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
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.ReflectableAnalyzer;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterObjectReflectableFilter extends ParameterFilterBase
{
	public String FilteredVariableName = null;
	public boolean StoreOutput = false;
	public String StoreOutputVariableName = null;
	public String TargetReflectableVariableName=null;
	
	public Set<String> GetInputVariableNames()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(FilteredVariableName);
		vars.add(TargetReflectableVariableName);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(this.StoreOutputVariableName);
		return vars;
	}
	
	private boolean IsDataTypeValidForFiltering(NamedDataType ndt) throws ExecutionValidationException
	{
		if(ndt.Value instanceof DataTypeReflectable) return true;
		if(ndt.Value instanceof DataTypeArray && 
			DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray)ndt.Value).GetArrayClassCode()).equals(IDataType.DataTypes.Reflectable))
			return true;
		return false;
	}
	
	private Class<?> FindTargetComponentType(ExecutionHandle Handle,NamedDataType ndt)
	{
		String target=null;
		try
		{
			target= Handle.GetPlan().Variables.Get(this.TargetReflectableVariableName).Value.GetStringValue();
		}catch(Exception ex)
		{
			return null;
		}
		Class<?> targetType=null;
		try
		{
			targetType = Class.forName(target);
		}catch(Exception ex)
		{
			targetType=null;
		}
		if(targetType==null && ((DataTypeArray)ndt.Value).GetDefaultComponentType()!=null)
		{
			try
			{
				targetType=Class.forName(((DataTypeArray)ndt.Value).GetDefaultComponentType());
			}catch(Exception ex)
			{
				targetType=null;
			}
		}
		return targetType;
	}
	
	private Object CopyArray(Object source, Class<?> TargetType) throws ExecutionValidationException, ArrayIndexOutOfBoundsException, IllegalArgumentException, ExecutionRunTimeException
	{
		if(!source.getClass().isArray()) throw new ExecutionValidationException("Cannot copy arrays if source is not array");
		Object arr=null;
		if(source.getClass().getComponentType().isArray())
		{
			int []dims=new int[DataTypeUtils.CountDimentionsOfObjectArrayCode(source.getClass().getName())];
			dims[0]=Array.getLength(source);
			arr=Array.newInstance(TargetType,dims);
			for(int i=0;i<Array.getLength(source);i+=1)
			{
				Array.set(arr, i, CopyArray(Array.get(source, i), TargetType));
			}
		}
		else
		{
			arr=Array.newInstance(TargetType, Array.getLength(source));
			for(int i=0;i<Array.getLength(source);i+=1)
			{
				if(Array.get(source, i)==null)
				{
					Array.set(arr, i, null);
				}
				else
				{
					if(!(Array.get(source, i) instanceof DataTypeReflectable)) throw new ExecutionValidationException("Array's value is not of the supported ones");
					Array.set(arr, i, this.ProcessReflectable((DataTypeReflectable)Array.get(source, i),TargetType));
				}
			}
		}
		return arr;
	}
	
	private Object ProcessReflectable(DataTypeReflectable ndt,Class<?> TargetType) throws ExecutionRunTimeException
	{
		try
		{
			ReflectableAnalyzer anal=new ReflectableAnalyzer(TargetType,null);
			if(!anal.CanRepresentAsReflectable()) throw new ExecutionRunTimeException("The target type cannot be processed as a reflectable");
			return anal.PopulateTarget(ndt,this.TokenMapping);
		}catch(Exception ex)
		{
			ExceptionUtils.ThrowTransformedRunTimeException(ex);
			return null;
		}
	}

	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
		try
		{
			if(!this.IsDataTypeValidForFiltering(ndt)) throw new ExecutionValidationException("Object filter cann only be applied to Reflectable objects or arrays of them");
		}
		catch(Exception ex)
		{
			ExceptionUtils.ThrowTransformedRunTimeException(ex);
		}
		Class<?> targetType=this.FindTargetComponentType(Handle,ndt);
		if(targetType==null) throw new ExecutionRunTimeException("No target reflectable class could be used");
		if(ndt.Value instanceof DataTypeReflectable) return this.ProcessReflectable(((DataTypeReflectable)ndt.Value),targetType);
		else if (ndt.Value instanceof DataTypeArray)
		{
			Object valArray = null;
			Object array=((DataTypeArray)ndt.Value).GetValue();
			if(!array.getClass().isArray())throw new ExecutionRunTimeException("Object filter can only be applied to Reflectable objects or arrays of them");
			try
			{
				valArray=this.CopyArray(array, targetType);
			}
			catch(Exception ex)
			{
				ExceptionUtils.ThrowTransformedRunTimeException(ex);
			}
			return valArray;
		}
		else throw new ExecutionRunTimeException("Object filter can only be applied to Reflectable objects or arrays of them");
	}
	
	public Object ProcessOnLine(Object OnLineFilteredValue,Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.FilteredVariableName == null || this.FilteredVariableName.trim().length() == 0) throw new ExecutionValidationException("Expected parameter name not provided");
		if (this.TargetReflectableVariableName == null || this.TargetReflectableVariableName.trim().length() == 0) throw new ExecutionValidationException("Expected parameter name not provided");
		if (this.StoreOutput && (this.StoreOutputVariableName == null || this.StoreOutputVariableName.trim().length() == 0)) throw new ExecutionValidationException("No output variable name defined to store output");
		this.TokenMappingValidate();
	}
	
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
		if(!ndt.IsAvailable && !ExcludeAvailableConstraint.contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if(!this.IsDataTypeValidForFiltering(ndt))throw new ExecutionValidationException("Filtered variable is not of expected type");
		if(this.StoreOutput)
		{
			for(String storeVarName : this.GetStoreOutputVariableName())
			{
				if(!Handle.GetPlan().Variables.Contains(storeVarName))throw new ExecutionValidationException("Needed parameter to store output not present");
			}
		}
//		if(this.StoreOutput() && !Handle.GetPlan().Variables.Contains(this.GetStoreOutputVariableName()))throw new ExecutionValidationException("Needed parameter to store output not present");
		if (!Handle.GetPlan().Variables.Contains(this.TargetReflectableVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		ndt = Handle.GetPlan().Variables.Get(this.TargetReflectableVariableName);
		if(!ndt.IsAvailable && !ExcludeAvailableConstraint.contains(this.TargetReflectableVariableName)) throw new ExecutionValidationException("Needed variable not available");
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
		try{
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
			tmp=XMLUtils.GetChildElementWithName(XML, "targetReflectableVariable");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.TargetReflectableVariableName=XMLUtils.GetChildText(tmp);
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
		buf.append("<filteredVariable>"+this.FilteredVariableName+"</filteredVariable>");
		buf.append("<targetReflectableVariable>"+this.TargetReflectableVariableName+"</targetReflectableVariable>");
		buf.append(this.TokenMappingToXML());
		buf.append("</filter>");
		return buf.toString();
	}

	public FilterType GetFilterType()
	{
		return FilterType.ObjectReflectable;
	}

}
