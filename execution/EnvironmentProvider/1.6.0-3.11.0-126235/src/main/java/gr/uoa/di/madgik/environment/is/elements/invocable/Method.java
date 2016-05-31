package gr.uoa.di.madgik.environment.is.elements.invocable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.w3c.dom.Element;

public class Method
{
	public String ID=UUID.randomUUID().toString();
	public boolean IsConstructor=false;
	public String Name=null;
	public String Signature=null;
	protected Map<String,Parameter> Parameters=new HashMap<String,Parameter>();
	public ParameterType ReturnValue=null;
	
	public void Add(Parameter p)
	{
		this.Parameters.put(p.Name, p);
	}
	
	public Parameter Get(String name)
	{
		if(!this.Parameters.containsKey(name)) return null;
		return this.Parameters.get(name);
	}
	
	public void FromXML(Element XML,String ClassName) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			if(XMLUtils.AttributeExists(XML, "id")) this.ID=XMLUtils.GetAttribute(XML, "id");
			Element callMethodElement =XMLUtils.GetChildElementWithNameAndNamespace(XML, "method", InvocableProfileInfo.ExecutionProfileNS);
			if(callMethodElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(callMethodElement, "name")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Name=XMLUtils.GetAttribute(callMethodElement, "name");
			if(ClassName.equals(this.Name)) this.IsConstructor=true;
			else this.IsConstructor=false;
			Element callSignatureElement =XMLUtils.GetChildElementWithNameAndNamespace(XML, "signature", InvocableProfileInfo.ExecutionProfileNS);
			if(callSignatureElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Signature=XMLUtils.GetChildText(callSignatureElement);
			List<Element> callMethodArgumentsElementslst=XMLUtils.GetChildElementsWithNameAndNamespace(XML, "argument", InvocableProfileInfo.ExecutionProfileNS);
			this.Parameters.clear();
			for(Element callMethodArgumentElement : callMethodArgumentsElementslst)
			{
				Parameter p=new Parameter();
				p.FromXML(callMethodArgumentElement,true);
				this.Add(p);
			}
			Element returnElem=XMLUtils.GetChildElementWithNameAndNamespace(XML, "return", InvocableProfileInfo.ExecutionProfileNS);
			if(returnElem!=null)
			{
				this.ReturnValue=new ParameterType();
				this.ReturnValue.FromXML(returnElem);
			}
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	public String ToXML(String type) throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<execprf:call type=\""+type+"\" id=\""+this.ID+"\">");
		buf.append("<execprf:method name=\""+this.Name+"\"/>");
		buf.append("<execprf:signature>"+this.Signature+"</execprf:signature>");
		for(Parameter p : this.Parameters.values())
		{
			buf.append(p.ToXML(type));
		}
		if(this.ReturnValue!=null)
		{
			buf.append("<execprf:return>");
			buf.append(this.ReturnValue.ToXML());
			buf.append("</execprf:return>");
		}
		buf.append("</execprf:call>");
		return buf.toString();
	}
}
