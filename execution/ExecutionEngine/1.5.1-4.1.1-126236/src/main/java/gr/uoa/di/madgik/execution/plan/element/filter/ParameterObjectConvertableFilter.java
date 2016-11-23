package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterObjectConvertableFilter extends ParameterFilterBase
{
	private static final long serialVersionUID = 1L;
	public String FilteredVariableName = null;
	public boolean StoreOutput = false;
	public String StoreOutputVariableName = null;
	
	public Set<String> GetInputVariableNames()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(FilteredVariableName);
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
		if(ndt.Value instanceof DataTypeConvertable) return true;
		if(ndt.Value instanceof DataTypeArray && 
			DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray)ndt.Value).GetArrayClassCode()).equals(IDataType.DataTypes.Convertable))
			return true;
		return false;
	}
	
	private Class<?> FindComponentType(Object array) throws ExecutionValidationException, ExecutionRunTimeException
	{
		if(!array.getClass().isArray()) return array.getClass(); //this should not happen
		Class<?> arrCompType=null;
		if(array.getClass().getComponentType().isArray())
		{
			for(int i=0;i<Array.getLength(array);i+=1)
			{
				Class<?> foundCompType=this.FindComponentType(Array.get(array, i));
				if(foundCompType!=null)
				{
					arrCompType=foundCompType;
					break;
				}
			}			
		}
		else
		{
			for(int i=0;i<Array.getLength(array);i+=1)
			{
				if(Array.get(array, i)==null) continue;
				if(!(Array.get(array, i) instanceof DataTypeConvertable)) throw new ExecutionValidationException("Array's value ("+Array.get(array, i)+") is not of the supported ones");
				arrCompType=this.ProcessConvertable((DataTypeConvertable)Array.get(array, i)).getClass();
				break;
			}
		}
		return arrCompType;
	}
	
	private Object CopyArray(Object source, Class<?> ComponentType) throws ExecutionValidationException, ArrayIndexOutOfBoundsException, IllegalArgumentException, ExecutionRunTimeException
	{
		if(!source.getClass().isArray()) throw new ExecutionValidationException("Cannot copy arrays if source is not array");
		Object arr=null;
		if(source.getClass().getComponentType().isArray())
		{
			int []dims=new int[DataTypeUtils.CountDimentionsOfObjectArrayCode(source.getClass().getName())];
			dims[0]=Array.getLength(source);
			arr=Array.newInstance(ComponentType,dims);
			for(int i=0;i<Array.getLength(source);i+=1)
			{
				Array.set(arr, i, CopyArray(Array.get(source, i), ComponentType));
			}
		}
		else
		{
			arr=Array.newInstance(ComponentType, Array.getLength(source));
			for(int i=0;i<Array.getLength(source);i+=1)
			{
				if(Array.get(source, i)==null)
				{
					Array.set(arr, i, null);
				}
				else
				{
					if(!(Array.get(source, i) instanceof DataTypeConvertable)) throw new ExecutionValidationException("Array's value is not of the supported ones");
					Array.set(arr, i, this.ProcessConvertable((DataTypeConvertable)Array.get(source, i)));
				}
			}
		}
		return arr;
	}
	
	private Object ProcessConvertable(DataTypeConvertable ndt) throws ExecutionRunTimeException
	{
		if(ndt.GetConverter()==null || ndt.GetConverter().trim().length()==0) throw new ExecutionRunTimeException("No converter defined");
		Object o = null;
		try
		{
			o = Class.forName(ndt.GetConverter()).newInstance();
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not instantiate external filter", ex);
		}
		if (!(o instanceof IObjectConverter)) throw new ExecutionRunTimeException("Privided external filter is not of needed type");
		try
		{
			Object converted = ((IObjectConverter) o).Convert(DataTypeUtils.GetValueAsString(ndt.GetConvertedValue()));
			return converted;
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not convert using external filter", ex);
		}
	}

	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
		try
		{
			if(!this.IsDataTypeValidForFiltering(ndt)) throw new ExecutionValidationException("Object filter cann only be applied to Convertable objects or arrays of them");
		}
		catch(Exception ex)
		{
			ExceptionUtils.ThrowTransformedRunTimeException(ex);
		}
		if(ndt.Value instanceof DataTypeConvertable) return this.ProcessConvertable(((DataTypeConvertable)ndt.Value));
		else if (ndt.Value instanceof DataTypeArray)
		{
			Object valArray = null;
			Object array=((DataTypeArray)ndt.Value).GetValue();
			if(!array.getClass().isArray())throw new ExecutionRunTimeException("Object filter cann only be applied to Convertable objects or arrays of them");
			try
			{
				Class<?> componentType=this.FindComponentType(array);
				if(componentType==null && ((DataTypeArray)ndt.Value).GetDefaultComponentType()!=null)
				{
					componentType=Class.forName(((DataTypeArray)ndt.Value).GetDefaultComponentType());
				}
				else if(componentType==null) throw new ExecutionValidationException("Cannot instantiate an array of convertables if no element is present and no default component type is set because no type can be infered");
				valArray=this.CopyArray(array, componentType);
			}
			catch(Exception ex)
			{
				ExceptionUtils.ThrowTransformedRunTimeException(ex);
			}
			
			return valArray;
		}
		else throw new ExecutionRunTimeException("Object filter cann only be applied to Convertable objects or arrays of them");
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
		buf.append(this.TokenMappingToXML());
		buf.append("</filter>");
		return buf.toString();
	}

	public FilterType GetFilterType()
	{
		return FilterType.ObjectConvertable;
	}

}
