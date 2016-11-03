package gr.uoa.di.madgik.environment.hint;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentSerializationException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;

public class NamedEnvHint implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String Name;
	public EnvHint Hint;

	public NamedEnvHint(){}
	
	public NamedEnvHint(String Name, EnvHint Hint)
	{
		this.Name=Name;
		this.Hint=Hint;
	}

	public void Validate() throws EnvironmentValidationException
	{
		if(this.Name==null || this.Name.trim().length()==0) throw new EnvironmentValidationException("No name is provided for named environment hint");
		if(this.Hint==null) throw new EnvironmentValidationException("No hint provided for named environment hint");
		this.Hint.Validate();
	}
	
	public String ToXML() throws EnvironmentSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<nhint name=\""+this.Name+"\">");
		buf.append(this.Hint.ToXML());
		buf.append("</nhint>");
		return buf.toString();
	}
	
	public void FromXML(String xml) throws EnvironmentSerializationException
	{
		try
		{
			Document doc=XMLUtils.Deserialize(xml);
			this.FromXML(doc.getDocumentElement());
		}
		catch(Exception ex)
		{
			throw new EnvironmentSerializationException("Could not deserialize named environment hint",ex);
		}
	}
	
	public void FromXML(Element xml) throws EnvironmentSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(xml, "name")) throw new EnvironmentSerializationException("non valid serialization of a named environment hint");
			this.Name=XMLUtils.GetAttribute(xml, "name");
			this.Hint=new EnvHint();
			this.Hint.Payload=XMLUtils.GetChildText(xml);
		}
		catch(Exception ex)
		{
			throw new EnvironmentSerializationException("Could not deserialize named environment hint",ex);
		}
	}
}
