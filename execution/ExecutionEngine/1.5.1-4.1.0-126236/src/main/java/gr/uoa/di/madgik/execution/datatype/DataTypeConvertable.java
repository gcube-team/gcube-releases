package gr.uoa.di.madgik.execution.datatype;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents data type with an object that can be converted 
 * using an {@link IObjectConverter} instance
 */
public class DataTypeConvertable implements IDataType
{
	private static final long serialVersionUID = 1L;
	/** The Value that is produced and can be passed to the {@link IObjectConverter}. */
	private String Value = null;
	
	/** The Converter name that can be used to retrieve the instance of the {@link IObjectConverter}
	 * associated with the data type. */
	private String Converter=null;

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
	 * Returns {@link IDataType.DataTypes#Convertable}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeEnum()
	 */
	public IDataType.DataTypes GetDataTypeEnum()
	{
		return IDataType.DataTypes.Convertable;
	}


	/**
	 * if the value is null, an empty string is returned. Otherwise the string 
	 * representation of the value is returned
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetStringValue()
	 */
	public String GetStringValue() throws ExecutionSerializationException
	{
		try
		{
			if (this.Value == null) return "";
			return this.Value.toString();
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not generate string value", ex);
		}
	}

	/**
	 * this instance is returned
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetValue()
	 */
	public Object GetValue()
	{
		return this;
	}
	
	/**
	 * Gets the string representation of the value
	 * 
	 * @return the string representation
	 */
	public String GetConvertedValue()
	{
		return this.Value;
	}
	
	/**
	 * Sets the string representation of the value
	 * 
	 * @param ConvertedValue the string representation of the value
	 */
	public void SetConvertedValue(String ConvertedValue)
	{
		this.Value=ConvertedValue;
	}

	/**
	 * Gets the converter class name
	 * 
	 * @return the converter class name
	 */
	public String GetConverter()
	{
		return this.Converter;
	}

	/**
	 * Sets the converter class name
	 * 
	 * @param Converter the converter class name
	 */
	public void SetConverter(String Converter)
	{
		this.Converter=Converter;
	}

	/**
	 * If the value is null or an empty string, null is set. Otherwise a call to 
	 * {@link IDataType#SetValue(Object)} is made
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetStringValue(java.lang.String)
	 */
	public void SetStringValue(String val) throws ExecutionValidationException, ExecutionSerializationException
	{
		if (val == null || val.trim().length() == 0) this.SetValue(null);
		else this.SetValue(val);
	}

	/**
	 * If the provided value is null, the string representation of the converted value 
	 * is set to null. Otherwise, if the value is an instance of {@link DataTypeConvertable},
	 * the string representation of the converted value and the converter class name are
	 * set to the respective values of the argument. Else if the argument is a string, the
	 * string representation of the converted value is set to the argument. Otherwise, if
	 * the converter class is set, an instance of that class is initialized and the provided
	 * argument is provided to the {@link IObjectConverter#Convert(Object)} and the returned
	 * string is set as the string representation of the convertable.
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetValue(java.lang.Object)
	 */
	public void SetValue(Object Value) throws ExecutionValidationException
	{
		String ErrorString=null;
		if (Value == null)
		{
			this.Value = null;
			return;
		}
		else if(Value instanceof DataTypeConvertable)
		{
			this.Converter=((DataTypeConvertable)Value).Converter;
			this.Value=((DataTypeConvertable)Value).Value;
			return;
		}
		else if (Value instanceof String)
		{
			this.Value = DataTypeUtils.GetValueAsString(Value);
			return;
		}
		else
		{
			if(this.Converter!=null)
			{
				try
				{
					Object o = Class.forName(this.Converter).newInstance();
					if (o instanceof IObjectConverter)
					{
						this.Value=DataTypeUtils.GetValueAsString(((IObjectConverter)o).Convert(Value));
						return;
					}
				} catch (Exception ex)
				{
					ErrorString=ex.getMessage();
				}

			}
		}
		throw new ExecutionValidationException("Incompatible types. Expecting String or somthing convertable through "+this.Converter+" and found " + Value.getClass().getName()+" with error from converter("+ErrorString+")");
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
			if(valelem==null) throw new ExecutionSerializationException("Provided Serialization is not valid");
			Element elem = XMLUtils.GetChildElementWithName(valelem, "converter");
			if(elem==null) throw new Exception("converter value not provided in serialization");
			else this.Converter=XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(elem));
			elem = XMLUtils.GetChildElementWithName(valelem, "converted");
			if(elem==null) this.Value=null;
			else this.Value=XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(elem));
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
		if(this.Value==null || this.Value.trim().length()==0) buf.append("<converted/>");
		else buf.append("<converted>"+XMLUtils.DoReplaceSpecialCharachters(this.Value)+"</converted>");
		if(this.Converter==null || this.Converter.trim().length()==0) buf.append("<conv/>");
		else buf.append("<converter>"+XMLUtils.DoReplaceSpecialCharachters(this.Converter)+"</converter>");
		buf.append("</value>");
		buf.append("</dt>");
		return buf.toString();
	}

}
