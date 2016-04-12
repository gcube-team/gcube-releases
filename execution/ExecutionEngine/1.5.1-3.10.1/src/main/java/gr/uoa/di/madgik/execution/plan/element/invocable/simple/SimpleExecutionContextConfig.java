package gr.uoa.di.madgik.execution.plan.element.invocable.simple;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase;
import org.w3c.dom.Element;

public class SimpleExecutionContextConfig extends ExecutionContextConfigBase
{
	private static final long serialVersionUID = 1L;

	@Override
	public ContextConfigType GetContextConfigType()
	{
		return ContextConfigType.Simple;
	}
	
	@Override
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<contextConfig type=\""+this.GetContextConfigType().toString()+"\" proxytype=\""+this.ProxyType.toString()+"\" keepAlive=\""+this.KeepContextAlive+"\"/>");
		return buf.toString();
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
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}
}
