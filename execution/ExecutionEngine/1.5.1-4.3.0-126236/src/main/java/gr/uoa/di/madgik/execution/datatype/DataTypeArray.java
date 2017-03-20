package gr.uoa.di.madgik.execution.datatype;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents a data type that acts as an array of data types 
 */
public class DataTypeArray implements IDataType, Iterable<IDataType>
{
	private static final long serialVersionUID = 1L;
	/** The array of data types. */
	private IDataType[] Value=null;
	
	/** The Array code. This is expected to follow the same schema as java arrays, 
	 * but the component type must be one of the values defined in {@link IDataType.DataTypes}
	 * except {@link IDataType.DataTypes#Array}*/
	private String ArrayClassCode=null;
	
	/** In case the component type is {@link IDataType.DataTypes#Convertable}, this
	 * can store the default converter class name to be used for these elements */
	private String DefaultConverter=null;
	
	/** The Default component type. */
	private String DefaultComponentType=null;

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<IDataType> iterator()
	{
		return Arrays.asList(this.Value).iterator();
	}
	
	/**
	 * Gets the array code.
	 * 
	 * @return the array code
	 */
	public String GetArrayClassCode()
	{
		return this.ArrayClassCode;
	}
	
	/**
	 * Sets the array code.
	 * 
	 * @param ArrayClassCode the array code
	 */
	public void SetArrayClassCode(String ArrayClassCode)
	{
		this.ArrayClassCode=ArrayClassCode;
	}
	
	/**
	 * Gets the default converter.
	 * 
	 * @return the string
	 */
	public String GetDefaultConverter()
	{
		return this.DefaultConverter;
	}
	
	/**
	 * Sets the default converter.
	 * 
	 * @param DefaultConverter the default converter
	 */
	public void SetDefaultConverter(String DefaultConverter)
	{
		this.DefaultConverter=DefaultConverter;
	}
	
	/**
	 * Gets the default component type.
	 * 
	 * @return the string
	 */
	public String GetDefaultComponentType()
	{
		return this.DefaultComponentType;
	}
	
	/**
	 * Sets the default component type.
	 * 
	 * @param DefaultComponentType the default component type
	 */
	public void SetDefaultComponentType(String DefaultComponentType)
	{
		this.DefaultComponentType=DefaultComponentType;
	}
	
	/**
	 * Always returns false
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#CanSuggestDataTypeClass()
	 */
	public boolean CanSuggestDataTypeClass()
	{
		return false;
	}
	
	/**
	 * Returns null
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeClass()
	 */
	public Class<?> GetDataTypeClass()
	{
		return null;
	}

	/**
	 * Returns {@link IDataType.DataTypes#Array}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeEnum()
	 */
	public DataTypes GetDataTypeEnum()
	{
		return IDataType.DataTypes.Array;
	}

	/**
	 * Calls {@link DataTypeArray#ToXML()}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetStringValue()
	 */
	public String GetStringValue() throws ExecutionSerializationException
	{
		return this.ToXML();
	}

	/**
	 * Calls {@link DataTypeArray#FromXML(String)}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetStringValue(java.lang.String)
	 */
	public void SetStringValue(String val) throws ExecutionValidationException, ExecutionSerializationException
	{
		this.FromXML(val);
	}
	
	/**
	 * Gets the array items.
	 * 
	 * @return the contained data types
	 */
	public IDataType[] GetItems()
	{
		return this.Value;
	}

	/**
	 * if the contained array data types is null, then null is returned. Otherwise, 
	 * an array is created with as many dimensions as are dictated by the array code and
	 * in every index of the array the respective {@link IDataType#GetValue()} is set.
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetValue()
	 */
	public Object GetValue()
	{
		if(this.ArrayClassCode==null || this.ArrayClassCode.trim().length()==0) throw new IllegalStateException("Array code has not been set");
		if(this.Value==null) return null;
		Object valArray = null;
		try
		{
			int []dims=new int[DataTypeUtils.CountDimentionsOfObjectArrayCode(this.GetArrayClassCode())];
			dims[0]=this.Value.length;
			valArray = Array.newInstance(DataTypeUtils.GetComponentTypeOfArrayInitializingCode(this.ArrayClassCode), dims);
		}catch(Exception ex)
		{
			throw new IllegalStateException("Could not initialize array of code "+this.GetArrayClassCode(),ex);
		}
		for(int i=0;i<this.Value.length;i+=1)
		{
			Array.set(valArray, i, this.Value[i].GetValue());
		}
		return valArray;
	}

	/**
	 * if the provided value is null, then the internal array is set to null. Otherwise,
	 * if the value is an instance of an array of {@link IDataType}, then the internal 
	 * array is set to that. If the value is a string, the {@link DataTypeArray#FromXML(String)}
	 * is called. Otherwise, if the provided value is an array, a new array of the type that is
	 * dictated by the array code is created and the respective data types are created from the
	 * respective values of the argument.
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetValue(java.lang.Object)
	 */
	public void SetValue(Object Value) throws ExecutionValidationException
	{
		//could i check if argument is DataTypeArray and if yes just place it inside instead of traversing it
		if(this.ArrayClassCode==null || this.ArrayClassCode.trim().length()==0) throw new IllegalStateException("Array code has not been set");
		if(Value==null)
		{
			this.Value=null;
			return;
		}
		else if (Value instanceof IDataType[])
		{
			this.Value=(IDataType[]) Value;
			return;
		}
		if(Value instanceof String)
		{
			try
			{
				this.FromXML((String)Value);
				return;
			} catch (ExecutionSerializationException ex)
			{
				throw new ExecutionValidationException("Could not set value from infered XML serialization of array data type",ex);
			}
		}
		if(!Value.getClass().isArray() && !(Value instanceof DataTypeArray)) throw new ExecutionValidationException("provided value is not an array");
		int dimentions=DataTypeUtils.CountDimentionsOfObjectArrayCode(ArrayClassCode);
		//if(DataTypeUtils.CountDimentionsOfObjectArray(Value)==1)
		if(dimentions==1)
		{
			int length=0;
			if(Value.getClass().isArray()) length=Array.getLength(Value);
			else length=((DataTypeArray)Value).Value.length;
			this.Value=DataTypeUtils.GetArrayOfDataType(DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(this.ArrayClassCode), length);
			for(int i=0;i<length;i+=1)
			{
				IDataType dtitem= DataTypeUtils.GetDataType(DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(this.ArrayClassCode));
				if((dtitem instanceof DataTypeConvertable) && this.DefaultConverter!=null) ((DataTypeConvertable)dtitem).SetConverter(DefaultConverter);
				Object val=null;
				if(Value.getClass().isArray()) val=Array.get(Value, i);
				else val=((DataTypeArray)Value).Value[i].GetValue();
				dtitem.SetValue(val);
				this.Value[i]=dtitem;
			}
		}
		else
		{
			int length=0;
			if(Value.getClass().isArray()) length=Array.getLength(Value);
			else length=((DataTypeArray)Value).Value.length;
			this.Value=DataTypeUtils.GetArrayOfDataType(IDataType.DataTypes.Array, length);
			for(int i=0;i<length;i+=1)
			{
				this.Value[i]=new DataTypeArray();
				((DataTypeArray)this.Value[i]).SetDefaultConverter(this.DefaultConverter);
				((DataTypeArray)this.Value[i]).SetDefaultComponentType(this.DefaultComponentType);
				((DataTypeArray)this.Value[i]).SetArrayClassCode(ArrayClassCode.substring(1));
				Object val=null;
				if(Value.getClass().isArray()) val=Array.get(Value, i);
				else val=((DataTypeArray)Value).Value[i].GetValue();
				this.Value[i].SetValue(val);
			}
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#FromXML(java.lang.String)
	 */
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#FromXML(org.w3c.dom.Element)
	 */
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!IDataType.DataTypes.valueOf(XMLUtils.GetAttribute(XML, "type")).equals(this.GetDataTypeEnum())) throw new ExecutionSerializationException("Provided serialization is not valid");
			Element defconvelem=XMLUtils.GetChildElementWithName(XML, "defConverter");
			if(defconvelem==null) this.DefaultConverter=null;
			else this.DefaultConverter=XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(defconvelem));
			Element defcompelem=XMLUtils.GetChildElementWithName(XML, "defComponent");
			if(defcompelem==null) this.DefaultComponentType=null;
			else this.DefaultComponentType=XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(defcompelem));
			Element valelem=XMLUtils.GetChildElementWithName(XML, "value");
			if(valelem==null) throw new ExecutionSerializationException("provided serialization is not valid");
			Element arrelem=XMLUtils.GetChildElementWithName(valelem, "array");
			if(arrelem==null) throw new ExecutionSerializationException("provided serialization is not valid");
			if(!XMLUtils.AttributeExists(arrelem, "code")) throw new ExecutionValidationException("Provided serialization is not valid");
			this.ArrayClassCode=XMLUtils.GetAttribute(arrelem, "code");
			List<Element> items=  XMLUtils.GetChildElementsWithName(arrelem,"item");
			if(DataTypeUtils.CountDimentionsOfObjectArrayCode(ArrayClassCode)==1)
			{
				this.Value=DataTypeUtils.GetArrayOfDataType(DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(this.ArrayClassCode), items.size());
				for(int i=0;i<items.size();i+=1)
				{
					Element subdtelem=XMLUtils.GetChildElementWithName(items.get(i), "dt");
					if(subdtelem==null) throw new ExecutionSerializationException("Not valid serialization provided");
					this.Value[i]=DataTypeUtils.GetDataType(subdtelem);
				}
			}
			else
			{
				this.Value=DataTypeUtils.GetArrayOfDataType(IDataType.DataTypes.Array, items.size());
				for(int i=0;i<items.size();i+=1)
				{
					Element subdtelem=XMLUtils.GetChildElementWithName(items.get(i), "dt");
					if(subdtelem==null) throw new ExecutionSerializationException("Not valid serialization provided");
					this.Value[i]=DataTypeUtils.GetDataType(subdtelem);
				}
			}
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		if(this.ArrayClassCode==null || this.ArrayClassCode.trim().length()==0) throw new IllegalStateException("Array code has not been set");
		StringBuilder buf=new StringBuilder();
		buf.append("<dt type=\""+this.GetDataTypeEnum().toString()+"\">");
		if(this.DefaultConverter!=null) buf.append("<defConverter>"+XMLUtils.DoReplaceSpecialCharachters(this.DefaultConverter)+"</defConverter>");
		if(this.DefaultComponentType!=null) buf.append("<defComponent>"+XMLUtils.DoReplaceSpecialCharachters(this.DefaultComponentType)+"</defComponent>");
		buf.append("<value>");
		if(this.Value==null) buf.append("<array code=\""+this.GetArrayClassCode()+"\"/>");
		else
		{
			buf.append("<array code=\""+this.GetArrayClassCode()+"\">");
			for(IDataType dt : this.Value)
			{
				buf.append("<item>");
				buf.append(dt.ToXML());
				buf.append("</item>");
			}
			buf.append("</array>");
		}
		buf.append("</value>");
		buf.append("</dt>");
		return buf.toString();
	}
}
