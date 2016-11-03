package gr.uoa.di.madgik.execution.datatype;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents data type with a string payload
 */
public class DataTypeString implements IDataType
{
	private static final long serialVersionUID = 1L;
	/** The payload Value. */
	private String Value=null;
	
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
	 * Returns {@link String#getClass()}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeClass()
	 */
	public Class<?> GetDataTypeClass()
	{
		return this.Value.getClass();
	}
	
	/**
	 * Returns {@link IDataType.DataTypes#String}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeEnum()
	 */
	public IDataType.DataTypes GetDataTypeEnum()
	{
		return IDataType.DataTypes.String;
	}

	/**
	 * Returns a new instance of the string payload or an empty string if the value is null
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetStringValue()
	 */
	public String GetStringValue() throws ExecutionSerializationException
	{
		if(this.Value==null) return "";
		return this.Value.toString();
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
	 * the value provided is set
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetStringValue(java.lang.String)
	 */
	public void SetStringValue(String val) throws ExecutionValidationException, ExecutionSerializationException
	{
		if(val==null || val.trim().length()==0) this.SetValue(null);
		this.SetValue(val);
	}

	/**
	 * Sets the provided payload using {@link DataTypeUtils#GetValueAsString(Object)}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetValue(java.lang.Object)
	 */
	public void SetValue(Object Value) throws ExecutionValidationException
	{
		if(Value==null) this.Value=null;
		else this.Value=DataTypeUtils.GetValueAsString(Value);
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
			else this.SetValue(XMLUtils.GetChildCDataText(valelem));
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	/**
	 * The payload of the data type is stored within a CDATA element
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<dt type=\""+this.GetDataTypeEnum().toString()+"\">");
		if(this.Value!=null)
		{
			buf.append("<value>");
			buf.append("<![CDATA["+this.Value+"]]>");
			buf.append("</value>");
		}
		buf.append("</dt>");
		return buf.toString();
	}
}
