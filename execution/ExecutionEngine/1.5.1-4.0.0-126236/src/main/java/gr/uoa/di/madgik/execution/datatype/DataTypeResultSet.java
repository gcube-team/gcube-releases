package gr.uoa.di.madgik.execution.datatype;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import java.net.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents data type with a {@link URI} proxy locator payload
 */
public class DataTypeResultSet implements IDataType
{
	private static final long serialVersionUID = 1L;
	/** The locator Value. */
	private URI Value = null;

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
	 * Returns {@link IProxyLocator#class}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeClass()
	 */
	public Class<?> GetDataTypeClass()
	{
		return URI.class;
	}

	/**
	 * Returns {@link IDataType.DataTypes#ResultSet}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeEnum()
	 */
	public IDataType.DataTypes GetDataTypeEnum()
	{
		return IDataType.DataTypes.ResultSet;
	}

	/**
	 * Returns {@link IProxyLocator#ToXML()} or an empty string if the value is null
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
	 * Returns the actual payload
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetValue()
	 */
	public Object GetValue()
	{
		return this.Value;
	}

	/**
	 * If the supplied value is null or an empty string, null is set as the payload. Otherwise
	 * {@link DataTypeUtils#GetValueAsProxyLocator(Object)} is used
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetStringValue(java.lang.String)
	 */
	public void SetStringValue(String val) throws ExecutionValidationException, ExecutionSerializationException
	{
		if (val == null || val.trim().length() == 0) this.SetValue(null);
		else
		{
			URI loc = null;
			try
			{
				loc = DataTypeUtils.GetValueAsProxyLocator(val);
			} catch (Exception ex)
			{
				throw new ExecutionSerializationException("Could not generate locator", ex);
			}
			this.SetValue(loc);
		}
	}

	/**
	 * Sets the provided payload using {@link DataTypeUtils#GetValueAsProxyLocator(Object)}
	 * if the the provided value is not null and is either a String or an instance of
	 * a {@link IProxyLocator}. If the value is null, then null is set for the value
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetValue(java.lang.Object)
	 */
	public void SetValue(Object Value) throws ExecutionValidationException
	{
		if (Value == null) this.Value = null;
		else if (Value instanceof URI) this.Value = DataTypeUtils.GetValueAsProxyLocator(Value);
		else if (Value instanceof String) this.Value = DataTypeUtils.GetValueAsProxyLocator(Value);
		else throw new ExecutionValidationException("Incompatible types. Expecting " + URI.class.getName() + " and found " + Value.getClass().getName());
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
			if(valelem==null) this.SetValue(null);
			else this.SetValue(XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(valelem)));
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	/**
	 * The payload of the data type is stored using {@link IProxyLocator#ToXML()}
	 * after escaping special characters using 
	 * {@link XMLUtils#DoReplaceSpecialCharachters(String)}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		try
		{
			StringBuilder buf=new StringBuilder();
			buf.append("<dt type=\""+this.GetDataTypeEnum().toString()+"\">");
			if(this.Value!=null)
			{
				buf.append("<value>");
				buf.append(XMLUtils.DoReplaceSpecialCharachters(this.Value.toString()));
				buf.append("</value>");
			}
			buf.append("</dt>");
			return buf.toString();
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not serialize data type",ex);
		}
	}

}
