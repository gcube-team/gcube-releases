package gr.uoa.di.madgik.execution.plan.element.invocable.ws;

import gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig;
import gr.uoa.di.madgik.commons.channel.nozzle.NozzleConfigUtils;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase;
import org.w3c.dom.Element;

public class WSExecutionContextConfig extends ExecutionContextConfigBase
{
	private static final long serialVersionUID = 1L;
	public INozzleConfig NozzleConfig=null;

	@Override
	public ContextConfigType GetContextConfigType()
	{
		return ContextConfigType.WS;
	}

	@Override
	public String ToXML() throws ExecutionSerializationException
	{
		try
		{
			StringBuilder buf=new StringBuilder();
			buf.append("<contextConfig type=\""+this.GetContextConfigType().toString()+"\" proxytype=\""+this.ProxyType.toString()+"\" keepAlive=\""+this.KeepContextAlive+"\"/>");
			if(this.NozzleConfig!=null)
			{
				buf.append(this.NozzleConfig.ToXML());
			}
			buf.append("</contextConfig>");
			return buf.toString();
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not serialize element",ex);
		}
	}

	@Override
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "type")) throw new ExecutionSerializationException("Provided serialization not valid");
			if(!ContextConfigType.valueOf(XMLUtils.GetAttribute(XML, "type")).equals(this.GetContextConfigType())) throw new ExecutionSerializationException("Provided serialization not valid");
			if(!XMLUtils.AttributeExists(XML, "proxytype")) throw new ExecutionSerializationException("Provided serialization not valid");
			if(!XMLUtils.AttributeExists(XML, "keepAlive")) throw new ExecutionSerializationException("Provided serialization not valid");
			this.ProxyType=ContextProxyType.valueOf(XMLUtils.GetAttribute(XML, "proxytype"));
			this.KeepContextAlive=Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "keepAlive"));
			Element nozzleconfigElement=XMLUtils.GetChildElementWithName(XML, "nozzleConfig");
			if(nozzleconfigElement==null) this.NozzleConfig=null;
			else
			{
				this.NozzleConfig=NozzleConfigUtils.GetNozzleConfig(nozzleconfigElement);
			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

}
