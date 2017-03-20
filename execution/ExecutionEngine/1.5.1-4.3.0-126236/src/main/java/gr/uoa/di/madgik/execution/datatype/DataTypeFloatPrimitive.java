package gr.uoa.di.madgik.execution.datatype;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents data type with a primitive float payload
 */
public class DataTypeFloatPrimitive implements IDataType
{
	private static final long serialVersionUID = 1L;
	/** The Value. */
	private float Value;

	/**
	 * Always returns true
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#CanSuggestDataTypeClass()
	 */
	public boolean CanSuggestDataTypeClass()
	{
		return true;
	}

	/**
	 * Returns {@link float#class}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeClass()
	 */
	public Class<?> GetDataTypeClass()
	{
		return float.class;
	}
	
	/**
	 * Returns {@link IDataType.DataTypes#FloatPrimitive}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeEnum()
	 */
	public IDataType.DataTypes GetDataTypeEnum()
	{
		return IDataType.DataTypes.FloatPrimitive;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetStringValue()
	 */
	public String GetStringValue() throws ExecutionSerializationException
	{
		return Float.toString(this.Value);
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetValue()
	 */
	public Object GetValue()
	{
		return this.Value;
	}

	/**
	 * If the supplied value is null or an empty string, an exception is thrown. Otherwise
	 * the value provided is set using {@link IDataType#SetValue(Object)}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetStringValue(java.lang.String)
	 */
	public void SetStringValue(String val) throws ExecutionValidationException, ExecutionSerializationException
	{
		if(val==null || val.trim().length()==0) throw new ExecutionValidationException("Cannot set primitive type with null value");
		this.SetValue(val);
	}

	/**
	 * Sets the provided payload using {@link DataTypeUtils#GetValueAsFloat(Object)}
	 * if the value is non null
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetValue(java.lang.Object)
	 */
	public void SetValue(Object Value) throws ExecutionValidationException
	{
		if(Value==null) throw new ExecutionValidationException("Cannot set primitive type with null value");
		else this.Value=DataTypeUtils.GetValueAsFloat(Value);
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
			Element valelem=XMLUtils.GetChildElementWithName(XML, "value");
			if(valelem==null) throw new ExecutionSerializationException("Cannot set null value in specific data type");
			else this.SetValue(XMLUtils.GetChildText(valelem));
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
		StringBuilder buf=new StringBuilder();
		buf.append("<dt type=\""+this.GetDataTypeEnum().toString()+"\">");
		buf.append("<value>");
		buf.append(this.Value);
		buf.append("</value>");
		buf.append("</dt>");
		return buf.toString();
	}


}
