package gr.uoa.di.madgik.environment.is.elements.invocable;

import java.io.Serializable;
import org.w3c.dom.Element;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.types.IReflectableDescription;

public class ParameterType implements Serializable
{
	private static final long serialVersionUID = 5209289995274664376L;
	public String Type=null;
	public String EngineType=null;
	public String Converter=null;
	public IReflectableDescription Reflectable=null;
	public String ArgumentTemplate=null;
	
	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			Element tmpTypeElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "type", InvocableProfileInfo.ExecutionProfileNS);
			if(tmpTypeElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(tmpTypeElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Type=XMLUtils.GetAttribute(tmpTypeElement, "value");
			Element tmpConverterElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "converter", InvocableProfileInfo.ExecutionProfileNS);
			if(tmpConverterElement!=null)
			{
				if(!XMLUtils.AttributeExists(tmpConverterElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
				this.Converter=XMLUtils.GetAttribute(tmpConverterElement, "value");
			}
			Element tmpArgTemplateElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "argumentTemplate", InvocableProfileInfo.ExecutionProfileNS);
			if(tmpArgTemplateElement!=null) this.ArgumentTemplate=XMLUtils.GetChildCDataText(tmpArgTemplateElement);
			else this.ArgumentTemplate=null;
			Element tmpEngineTypeElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "engineType", InvocableProfileInfo.ExecutionProfileNS);
			if(tmpEngineTypeElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(tmpEngineTypeElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.EngineType=XMLUtils.GetAttribute(tmpEngineTypeElement, "value");
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<execprf:type value=\""+this.Type+"\"/>");
		buf.append("<execprf:engineType value=\""+this.EngineType+"\"/>");
		if(this.Converter!=null && !this.Converter.equalsIgnoreCase("none")) buf.append("<execprf:converter value=\""+this.Converter+"\"/>");
		if(this.ArgumentTemplate!=null)
		{
			buf.append("<execprf:argumentTemplate>");
			buf.append(this.ArgumentTemplate);
			buf.append("</execprf:argumentTemplate>");
		}
		return buf.toString();
	}
}
