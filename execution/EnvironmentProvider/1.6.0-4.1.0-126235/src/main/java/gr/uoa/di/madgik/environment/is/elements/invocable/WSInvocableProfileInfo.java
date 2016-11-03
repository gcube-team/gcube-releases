package gr.uoa.di.madgik.environment.is.elements.invocable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.context.InvocableContext;
import gr.uoa.di.madgik.environment.is.elements.invocable.context.InvocableContextgRSProxy;
import gr.uoa.di.madgik.environment.is.elements.invocable.context.InvocableContext.ProgressReportingProvider;

public class WSInvocableProfileInfo extends InvocableProfileInfo implements Serializable
{
	private static final long serialVersionUID = -268003432096585042L;
	public String ClassName=null;
	private Map<String,WSMethod> Methods=new HashMap<String,WSMethod>();
	
	public void Add(WSMethod m)
	{
		this.Methods.put(m.Signature, m);
	}
	
	public WSMethod Get(String signature)
	{
		if(!this.Methods.containsKey(signature)) return null;
		return this.Methods.get(signature);
	}

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
			Element classNameElem=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "className", InvocableProfileInfo.ExecutionProfileNS);
			if(classNameElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
			if(!XMLUtils.AttributeExists(classNameElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
			this.ClassName=XMLUtils.GetAttribute(classNameElem, "value");
			Element contextElement=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "context",InvocableProfileInfo.ExecutionProfileNS);
			if(contextElement==null) throw new EnvironmentInformationSystemException("Invalid serialization");
			if(!XMLUtils.AttributeExists(contextElement, "supported")) throw new EnvironmentInformationSystemException("Not valid serialization");
			this.ExecutionContext=new InvocableContext();
			this.ExecutionContext.Supported=Boolean.parseBoolean(XMLUtils.GetAttribute(contextElement, "supported"));
			if(this.ExecutionContext.Supported)
			{
				Element contextKeepAliveElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "keepAlive", InvocableProfileInfo.ExecutionProfileNS);
				if(contextKeepAliveElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
				if(!XMLUtils.AttributeExists(contextKeepAliveElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
				this.ExecutionContext.KeepAlive=Boolean.parseBoolean(XMLUtils.GetAttribute(contextKeepAliveElem, "value"));
				Element contextReportsProgressElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "reportsProgress", InvocableProfileInfo.ExecutionProfileNS);
				if(contextReportsProgressElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
				if(!XMLUtils.AttributeExists(contextReportsProgressElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
				this.ExecutionContext.ReportsProgress=Boolean.parseBoolean(XMLUtils.GetAttribute(contextReportsProgressElem, "value"));
				if(this.ExecutionContext.ReportsProgress)
				{
					Element contextProviderTypeElement=XMLUtils.GetChildElementWithNameAndNamespace(contextReportsProgressElem, "progressProvider", InvocableProfileInfo.ExecutionProfileNS);
					if(contextProviderTypeElement==null) throw new EnvironmentInformationSystemException("Not valid serialization");
					if(!XMLUtils.AttributeExists(contextProviderTypeElement, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
					this.ExecutionContext.ProgressProvider=ProgressReportingProvider.valueOf(XMLUtils.GetAttribute(contextProviderTypeElement, "value"));
				}
				Element contextgRSProxyElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "gRSProxy", InvocableProfileInfo.ExecutionProfileNS);
				if(contextgRSProxyElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
				if(!XMLUtils.AttributeExists(contextgRSProxyElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
				this.ExecutionContext.ProxygRS=new InvocableContextgRSProxy();
				this.ExecutionContext.ProxygRS.SupplyProxy=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyElem, "value"));
				if(this.ExecutionContext.ProxygRS.SupplyProxy)
				{
					Element contextgRSProxyEncryptElem=XMLUtils.GetChildElementWithNameAndNamespace(contextgRSProxyElem, "encrypt",InvocableProfileInfo.ExecutionProfileNS);
					if(contextgRSProxyEncryptElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
					if(!XMLUtils.AttributeExists(contextgRSProxyEncryptElem, "value")) throw new EnvironmentInformationSystemException("not valie serialization");
					this.ExecutionContext.ProxygRS.Encrypt=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyEncryptElem, "value"));
					Element contextgRSProxyAuthenticateElem=XMLUtils.GetChildElementWithNameAndNamespace(contextgRSProxyElem, "authenticate",InvocableProfileInfo.ExecutionProfileNS);
					if(contextgRSProxyAuthenticateElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
					if(!XMLUtils.AttributeExists(contextgRSProxyAuthenticateElem, "value")) throw new EnvironmentInformationSystemException("not valie serialization");
					this.ExecutionContext.ProxygRS.Authenticate=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyAuthenticateElem, "value"));
				}
				Element callsElem=XMLUtils.GetChildElementWithNameAndNamespace(XML, "calls", InvocableProfileInfo.ExecutionProfileNS);
				if(callsElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
				List<Element> calllst=XMLUtils.GetChildElementsWithNameAndNamespace(callsElem, "call", InvocableProfileInfo.ExecutionProfileNS);
				for(Element callElem : calllst)
				{
					WSMethod m=new WSMethod();
					m.FromXML(callElem);
					this.Add(m);
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
		buf.append("<execprf:InvocableProfile xmlns:execprf=\""+InvocableProfileInfo.ExecutionProfileNS+"\" type=\"WS\" id=\""+this.ID+"\">");
		buf.append("<execprf:item>");
		buf.append("<execprf:className value=\""+this.ClassName+"\"/>");
		buf.append("<execprf:context supported=\""+this.ExecutionContext.Supported+"\">");
		if(this.ExecutionContext.Supported)
		{
			buf.append("<execprf:type value=\"WS\"/>");
			buf.append("<execprf:keepAlive value=\""+this.ExecutionContext.KeepAlive+"\"/>");
			buf.append("<execprf:reportsProgress value=\""+this.ExecutionContext.ReportsProgress+"\">");
			if(this.ExecutionContext.ReportsProgress)
			{
				buf.append("<execprf:progressProvider value=\""+this.ExecutionContext.ProgressProvider.toString()+"\"/>");
			}
			buf.append("</execprf:reportsProgress>");
			buf.append("<execprf:gRSProxy value=\""+this.ExecutionContext.ProxygRS.SupplyProxy+"\">");
			if(this.ExecutionContext.ProxygRS.SupplyProxy)
			{
				buf.append("<execprf:encrypt value=\""+this.ExecutionContext.ProxygRS.Encrypt+"\">");
				buf.append("<execprf:authenticate value=\""+this.ExecutionContext.ProxygRS.Authenticate+"\">");
			}
			buf.append("</execprf:gRSProxy>");
		}
		buf.append("</execprf:context>");
		buf.append("</execprf:item>");
		buf.append("<execprf:calls>");
		for(WSMethod m : this.Methods.values())
		{
			buf.append(m.ToXML("WS"));
		}
		buf.append("</execprf:calls>");
		buf.append("</execprf:InvocableProfile>");
		return buf.toString();
	}
}
