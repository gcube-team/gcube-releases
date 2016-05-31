package gr.uoa.di.madgik.execution.datatype;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents data type with a long object payload
 */
public class DataTypeLongClass implements IDataType
{
	private static final long serialVersionUID = 1L;
	/** The Value. */
	private Long Value=null;
	
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
	 * Returns {@link Long#getClass()}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeClass()
	 */
	public Class<?> GetDataTypeClass()
	{
		return this.Value.getClass();
	}
	
	/**
	 * Returns {@link IDataType.DataTypes#LongClass}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeEnum()
	 */
	public IDataType.DataTypes GetDataTypeEnum()
	{
		return IDataType.DataTypes.LongClass;
	}

	/**
	 * if the value is null, an empty string is returned. Otherwise a string 
	 * representation of the value is returned
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetStringValue()
	 */
	public String GetStringValue() throws ExecutionSerializationException
	{
		if(this.Value==null) return "";
		return this.Value.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetValue()
	 */
	public Object GetValue()
	{
		return this.Value;
	}

	/**
	 * If the value is null or an empty string, null is set. Otherwise a call to 
	 * {@link IDataType#SetValue(Object)} is made
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetStringValue(java.lang.String)
	 */
	public void SetStringValue(String val) throws ExecutionValidationException, ExecutionSerializationException
	{
		if(val==null || val.trim().length()==0) this.SetValue(null);
		this.SetValue(val);
	}

	/**
	 * If the provided value is null, then null is set otherwise the value is set
	 * using {@link DataTypeUtils#GetValueAsLong(Object)}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetValue(java.lang.Object)
	 */
	public void SetValue(Object Value) throws ExecutionValidationException
	{
		if(Value==null) this.Value=null;
		else this.Value=DataTypeUtils.GetValueAsLong(Value);
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
		if(this.Value!=null)
		{
			buf.append("<value>");
			buf.append(this.Value);
			buf.append("</value>");
		}
		buf.append("</dt>");
		return buf.toString();
	}


}
