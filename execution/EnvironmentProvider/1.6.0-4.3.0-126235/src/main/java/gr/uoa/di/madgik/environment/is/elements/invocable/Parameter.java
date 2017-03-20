package gr.uoa.di.madgik.environment.is.elements.invocable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import java.io.Serializable;
import java.util.UUID;
import org.w3c.dom.Element;

public class Parameter implements Serializable
{
	private static final long serialVersionUID = -6509807155644208222L;
	public String ID=UUID.randomUUID().toString();
	public int Order=0;
	public String Name=null;
	public String Token=null;
	public ParameterType Type=new ParameterType();
	
	public void FromXML(Element XML, boolean isSimple) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			if(XMLUtils.AttributeExists(XML, "id")) this.ID=XMLUtils.GetAttribute(XML, "id");
			Element callMethodArgumentOrderElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "order", InvocableProfileInfo.ExecutionProfileNS);
			if(callMethodArgumentOrderElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(callMethodArgumentOrderElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute(callMethodArgumentOrderElement, "value"));
			Element callMethodArgumentNameElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "name", InvocableProfileInfo.ExecutionProfileNS);
			if(callMethodArgumentNameElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(callMethodArgumentNameElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Name=XMLUtils.GetAttribute(callMethodArgumentNameElement, "value");
			Element callMethodArgumentTokenElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "token", InvocableProfileInfo.ExecutionProfileNS);
			if(callMethodArgumentTokenElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(callMethodArgumentTokenElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Token=XMLUtils.GetAttribute(callMethodArgumentTokenElement, "value");
			if(isSimple) this.Type=new ParameterType();
			else this.Type=new WSParameterType();
			this.Type.FromXML(XML);
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	public String ToXML(String type) throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<execprf:argument type=\""+type+"\" id=\""+this.ID+"\">");
		buf.append("<execprf:order value=\""+this.Order+"\"/>");
		buf.append("<execprf:name value=\""+this.Name+"\"/>");
		buf.append("<execprf:token value=\""+this.Token+"\"/>");
		buf.append(this.Type.ToXML());
		buf.append("</execprf:argument>");
		return buf.toString();
	}
}
