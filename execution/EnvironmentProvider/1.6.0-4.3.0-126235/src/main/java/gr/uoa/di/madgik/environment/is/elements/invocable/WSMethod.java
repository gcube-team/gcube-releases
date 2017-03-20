package gr.uoa.di.madgik.environment.is.elements.invocable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import java.io.Serializable;
import java.util.List;
import org.w3c.dom.Element;

public class WSMethod extends Method implements Serializable
{
	private static final long serialVersionUID = -7590114661605524464L;
	public enum ExtractType
	{
		String,
		Node
	}
	
	public String MethodURN;
	public String EnvelopeTemplate;
	public String ExecutioContextToken=null;
	
	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			if(XMLUtils.AttributeExists(XML, "id")) this.ID=XMLUtils.GetAttribute(XML, "id");
			Element callMethodElement =XMLUtils.GetChildElementWithNameAndNamespace(XML, "method", InvocableProfileInfo.ExecutionProfileNS);
			if(callMethodElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(callMethodElement, "name")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Name=XMLUtils.GetAttribute(callMethodElement, "name");
			this.IsConstructor=false;
			Element callMethodURNElement =XMLUtils.GetChildElementWithNameAndNamespace(XML, "methodURN", InvocableProfileInfo.ExecutionProfileNS);
			if(callMethodURNElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(callMethodURNElement, "name")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.MethodURN=XMLUtils.GetAttribute(callMethodURNElement, "name");
			Element callSignatureElement =XMLUtils.GetChildElementWithNameAndNamespace(XML, "signature", InvocableProfileInfo.ExecutionProfileNS);
			if(callSignatureElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Signature=XMLUtils.GetChildText(callSignatureElement);
			Element callMethodTemplateElement =XMLUtils.GetChildElementWithNameAndNamespace(XML, "envelopeTemplate", InvocableProfileInfo.ExecutionProfileNS);
			if(callMethodTemplateElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.EnvelopeTemplate=XMLUtils.GetChildCDataText(callMethodTemplateElement);
			Element callMethodTokenElement =XMLUtils.GetChildElementWithNameAndNamespace(XML, "executionContextToken", InvocableProfileInfo.ExecutionProfileNS);
			if(callMethodTokenElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.ExecutioContextToken=XMLUtils.GetChildText(callMethodTokenElement);
			List<Element> callMethodArgumentsElementslst=XMLUtils.GetChildElementsWithNameAndNamespace(XML, "argument", InvocableProfileInfo.ExecutionProfileNS);
			this.Parameters.clear();
			for(Element callMethodArgumentElement : callMethodArgumentsElementslst)
			{
				Parameter p=new Parameter();
				p.FromXML(callMethodArgumentElement,false);
				this.Add(p);
			}
			Element returnElem=XMLUtils.GetChildElementWithNameAndNamespace(XML, "return", InvocableProfileInfo.ExecutionProfileNS);
			if(returnElem!=null)
			{
				this.ReturnValue=new WSParameterType();
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
		buf.append("<execprf:methodURN name=\""+this.MethodURN+"\"/>");
		buf.append("<execprf:signature>"+this.Signature+"</execprf:signature>");
		buf.append("<execprf:envelopeTemplate><![CDATA[");
		buf.append(this.EnvelopeTemplate);
		buf.append("]]></execprf:envelopeTemplate>");
		buf.append("<execprf:executionContextToken>");
		buf.append(this.ExecutioContextToken);
		buf.append("</execprf:executionContextToken>");
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
