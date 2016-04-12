package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.channel.proxy.ChannelLocatorFactory;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.IExecutionContext;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSExecutionContext;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSExecutionContextConfig;
import java.net.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExecutionContextUtils
{
	private static final String ExecutionContextNS="http://context.execution.madgik.di.uoa.gr";
	
	public static String GenerateExecutionContextSoapHeaderElement(IChannelLocator ContextChannel, String ContextID,String ExternalSender,WSExecutionContextConfig ContextConfig) throws ExecutionSerializationException
	{
		try
		{
			StringBuilder buf=new StringBuilder();
			buf.append("<execcntx:Context xmlns:execcntx=\""+ExecutionContextUtils.ExecutionContextNS+"\">");
			buf.append("<execcntx:ID>"+ContextID+"</execcntx:ID>");
			buf.append("<execcntx:Sender>"+ExternalSender+"</execcntx:Sender>");
			if(ContextChannel!=null)buf.append("<execcntx:Channel>"+XMLUtils.DoReplaceSpecialCharachters(ContextChannel.ToURI().toString())+"</execcntx:Channel>");
			if(ContextConfig!=null)buf.append("<execcntx:Config>"+XMLUtils.DoReplaceSpecialCharachters(ContextConfig.ToXML())+"</execcntx:Config>");
			buf.append("</execcntx:Context>");
			return buf.toString();
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not create Soap Header element for Execution Context",ex);
		}
	}
	
	public static String GenerateExecutionEngineSoapHeaderElement(IChannelLocator ContextChannel, String ContextID,String ExternalSender,WSExecutionContextConfig ContextConfig) throws ExecutionSerializationException
	{
		try
		{
			StringBuilder buf=new StringBuilder();
			buf.append("<execcntx:ExecutionEngineHeader xmlns:execcntx=\""+ExecutionContextUtils.ExecutionContextNS+"\">");
			buf.append(ExecutionContextUtils.GenerateExecutionContextSoapHeaderElement(ContextChannel, ContextID,ExternalSender,ContextConfig));
			buf.append("</execcntx:ExecutionEngineHeader>");
			return buf.toString();
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not create Soap Header element for Execution Context",ex);
		}
	}
	
	public static IExecutionContext GetExecutionContext(String SOAPHeaderSerialization) throws ExecutionSerializationException
	{
		try
		{
			String ContextID=null;
			String ExternalSender=null;
			IChannelLocator Locator=null;
			WSExecutionContextConfig Config=null;
			Document doc= XMLUtils.Deserialize(SOAPHeaderSerialization);
			Element engroot=XMLUtils.GetChildElementWithNameAndNamespace(doc.getDocumentElement(), "ExecutionEngineHeader", ExecutionContextUtils.ExecutionContextNS);
			if(engroot==null || engroot.getPrefix()==null || !engroot.getPrefix().equals("execcntx")) throw new ExecutionSerializationException("Not valid serialization of execution context");
			if(!engroot.getLocalName().equals("ExecutionEngineHeader")) throw new ExecutionSerializationException("Not valid serialization of execution context");
			if(engroot.getNamespaceURI()==null || !engroot.getNamespaceURI().equals("http://context.execution.madgik.di.uoa.gr")) throw new ExecutionSerializationException("Not valid serialization of execution context");
			Element contextroot=XMLUtils.GetChildElementWithNameAndNamespace(engroot, "Context", ExecutionContextUtils.ExecutionContextNS);
			if(contextroot==null || contextroot.getPrefix()==null || !contextroot.getPrefix().equals("execcntx")) throw new ExecutionSerializationException("Not valid serialization of execution context");
			if(!contextroot.getLocalName().equals("Context")) throw new ExecutionSerializationException("Not valid serialization of execution context");
			if(contextroot.getNamespaceURI()==null || !contextroot.getNamespaceURI().equals("http://context.execution.madgik.di.uoa.gr")) throw new ExecutionSerializationException("Not valid serialization of execution context");
			Element idelement=XMLUtils.GetChildElementWithNameAndNamespace(contextroot, "ID", ExecutionContextUtils.ExecutionContextNS);
			if(idelement==null)throw new ExecutionSerializationException("Not valid serialization of execution context");
			ContextID=XMLUtils.GetChildText(idelement);
			Element senderelement=XMLUtils.GetChildElementWithNameAndNamespace(contextroot, "Sender", ExecutionContextUtils.ExecutionContextNS);
			if(senderelement==null)throw new ExecutionSerializationException("Not valid serialization of execution context");
			ExternalSender=XMLUtils.GetChildText(senderelement);
			Element channelelement=XMLUtils.GetChildElementWithNameAndNamespace(contextroot, "Channel", ExecutionContextUtils.ExecutionContextNS);
			if(channelelement!=null)
			{
				String channelPayload=XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(channelelement));
				Locator=ChannelLocatorFactory.GetLocator(new URI(channelPayload));
			}
			Element configelement=XMLUtils.GetChildElementWithNameAndNamespace(contextroot, "Config", ExecutionContextUtils.ExecutionContextNS);
			if(configelement!=null)
			{
				String configPayload=XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(configelement));
				Config=new WSExecutionContextConfig();
				Config.FromXML(configPayload);
			}
			return new WSExecutionContext(ContextID,ExternalSender,Locator,Config);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize execution context",ex);
		}
	}
}
