package gr.uoa.di.madgik.execution.datatype;

import java.io.Serializable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import org.w3c.dom.Element;

/**
 * This class acts as a named container of {@link IDataType}s that are items contained in
 * an {@link DataTypeReflectable}
 */
public class ReflectableItem implements Serializable
{
	private static final long serialVersionUID = 1L;
	/** The Name of the item. */
	public String Name=null;
	
	/** The Token of the item. */
	public String Token=null;
	
	/** The Value of the item. */
	public IDataType Value=null;
	
	/**
	 * Serializes this reflectable item to an XML serialization
	 * 
	 * @return the xml serialization
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		if(this.Value==null) throw new ExecutionSerializationException("Value cannot be null");
		StringBuilder buf=new StringBuilder();
		String tokenString="token=\"" + this.Token + "\"";
		if(this.Token==null) tokenString="";
		buf.append("<item name=\""+this.Name+"\" "+tokenString+">");
		buf.append(this.Value.ToXML());
		buf.append("</item>");
		return buf.toString();
	}
	
	/**
	 * Parses the provided serialization as returned by {@link ReflectableItem#ToXML()}
	 * and populates the instance
	 * 
	 * @param element the root element of the serialization
	 * 
	 * @throws ExecutionSerializationException a serialization error occurred
	 */
	public void FromXML(Element element) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(element, "name")) throw new ExecutionSerializationException("Not a valid serialization of a named data type");
			this.Name = XMLUtils.GetAttribute(element, "name");
			if(XMLUtils.AttributeExists(element, "token"))this.Token = XMLUtils.GetAttribute(element, "token");
			else this.Token=null;
			Element val=XMLUtils.GetChildElementWithName(element, "dt");
			if(val==null) throw new ExecutionSerializationException("Not a valid serialization of a named data type");
			this.Value=DataTypeUtils.GetDataType(val);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize named data type from provided serialization", ex);
		}
	}
}
