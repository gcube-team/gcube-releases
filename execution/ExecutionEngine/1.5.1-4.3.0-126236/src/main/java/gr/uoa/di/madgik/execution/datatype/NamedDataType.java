package gr.uoa.di.madgik.execution.datatype;

import java.io.Serializable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.variable.VariableCollection;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import org.w3c.dom.Element;

/**
 * This class acts as a named container of {@link IDataType}s and is the key
 * class involved in variable definition of an {@link ExecutionPlan} and the
 * corresponding {@link VariableCollection} of the plan
 */
public class NamedDataType implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** The Name of the type. */
	public String Name = null;
	
	/** The Token of the type. */
	public String Token = null;
	
	/** Whether the value for this type is available. */
	public Boolean IsAvailable = false;
	
	/** The actual type. */
	public IDataType Value = null;

	/**
	 * Creates an XML serialization of the named data type
	 * 
	 * @return The serialization
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		if(Value==null) throw new ExecutionSerializationException("Data type cannot be null");
		StringBuilder buf = new StringBuilder();
		String tokenString="token=\"" + this.Token + "\"";
		if(this.Token==null) tokenString="";
		buf.append("<ndt name=\"" + this.Name + "\" available=\"" + this.IsAvailable.toString() + "\" "+tokenString+">");
		buf.append(this.Value.ToXML());
		buf.append("</ndt>");
		return buf.toString();
	}

	/**
	 * Parses the provided serialization as returned by {@link NamedDataType#ToXML()}
	 * 
	 * @param XML The XML serialization to parse
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		try
		{
			this.FromXML(XMLUtils.Deserialize(XML).getDocumentElement());
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided document", ex);
		}
	}

	/**
	 * Parses the provided serialization as returned by {@link NamedDataType#ToXML()}
	 * 
	 * @param XML the root element of the serialization
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if (!XMLUtils.AttributeExists(XML, "name") || !XMLUtils.AttributeExists(XML, "available")) throw new ExecutionSerializationException("Not a valid serialization of a named data type");
			this.Name = XMLUtils.GetAttribute(XML, "name");
			if(XMLUtils.AttributeExists(XML, "token"))this.Token = XMLUtils.GetAttribute(XML, "token");
			else this.Token=null;
			this.IsAvailable = Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "available"));
			Element val=XMLUtils.GetChildElementWithName(XML, "dt");
			if(val==null) throw new ExecutionSerializationException("Not a valid serialization of a named data type");
			this.Value=DataTypeUtils.GetDataType(val);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize named data type from provided serialization", ex);
		}
	}

	/**
	 * Checks if the two instances are equal. Two instances are considered equal only if 
	 * they are both of type {@link NamedDataType}, the {@link NamedDataType#Name} is equal,
	 * the {@link NamedDataType#Token} is equal, the {@link NamedDataType#IsAvailable} is the
	 * same and the {@link IDataType#equals(Object)} also evaluates to true.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof NamedDataType)) return false;
		if (!this.Name.equals(((NamedDataType) o).Name)) return false;
		if (!this.Token.equals(((NamedDataType) o).Token)) return false;
		if (!this.IsAvailable.toString().equals(((NamedDataType) o).IsAvailable.toString())) return false;
		if (!this.Value.equals(((NamedDataType) o).Value)) return false;
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}
