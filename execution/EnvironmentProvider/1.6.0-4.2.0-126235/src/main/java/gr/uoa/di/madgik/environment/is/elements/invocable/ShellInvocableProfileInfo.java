package gr.uoa.di.madgik.environment.is.elements.invocable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;

public class ShellInvocableProfileInfo extends InvocableProfileInfo implements Serializable
{
	private static final long serialVersionUID = -6822668010951864524L;
	public String ExecutableName=null;
	public boolean AreParametersBound=false;
	public List<Parameter> Parameters=new ArrayList<Parameter>();
	
	@Override
	public void FromXML(String XML) throws EnvironmentInformationSystemSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}
	
	@Override
	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			if(XMLUtils.AttributeExists(XML, "id")) this.ID=XMLUtils.GetAttribute(XML, "id");
			Element itemElem=XMLUtils.GetChildElementWithNameAndNamespace(XML, "item",InvocableProfileInfo.ExecutionProfileNS);
			if(itemElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
			Element executableNameElem=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "executableName", InvocableProfileInfo.ExecutionProfileNS);
			if(executableNameElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
			if(!XMLUtils.AttributeExists(executableNameElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
			this.ExecutableName=XMLUtils.GetAttribute(executableNameElem, "value");
			Element argumentsElem=XMLUtils.GetChildElementWithNameAndNamespace(XML, "arguments", InvocableProfileInfo.ExecutionProfileNS);
			if(argumentsElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
			if(!XMLUtils.AttributeExists(argumentsElem, "bound")) throw new EnvironmentInformationSystemException("Not valid serialization");
			this.AreParametersBound=Boolean.parseBoolean(XMLUtils.GetAttribute(argumentsElem, "bound"));
			if(this.AreParametersBound)
			{
				List<Element> shellArgumentsElementslst=XMLUtils.GetChildElementsWithNameAndNamespace(argumentsElem, "argument", InvocableProfileInfo.ExecutionProfileNS);
				for(Element shellArgumentElement : shellArgumentsElementslst)
				{
					Parameter p=new Parameter();
					p.FromXML(shellArgumentElement, true);
					this.Parameters.add(p);
				}
			}
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	@Override
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<execprf:InvocableProfile xmlns:execprf=\""+InvocableProfileInfo.ExecutionProfileNS+"\" type=\"Shell\" id=\""+this.ID+"\">");
		buf.append("<execprf:item>");
		buf.append("<execprf:executableName value=\""+this.ExecutableName+"\"/>");
		buf.append("</execprf:item>");
		if(!this.AreParametersBound)
		{
			buf.append("<execprf:arguments bound=\""+this.AreParametersBound+"\" />");
		}
		else
		{
			buf.append("<execprf:arguments>");
			for(Parameter p : this.Parameters) buf.append(p.ToXML("Simple"));
			buf.append("</execprf:arguments>");
		}
		buf.append("</execprf:InvocableProfile>");
		return buf.toString();
	}
}
