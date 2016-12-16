package gr.uoa.di.madgik.execution.plan.element.invocable;

import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CheckpointConfig
{

	public IProxy.ProxyType TypeOfProxy=ProxyType.TCPStore;
	public IBufferStore.MultiplexType TypeOfMultiplex=IBufferStore.MultiplexType.FIFO;
	public long TimeoutValue=60;
	public TimeUnit TimeoutUnit=TimeUnit.SECONDS;

//	
//	public StoreServerConfig GetStoreServerConfig()
//	{
//		StoreServerConfig conf=new StoreServerConfig(this.TCPServerStorePortRanges, this.TCPServerStoreUseRandomIfNonAvailable);
//		conf.SetTCPServerProxyConfig(this.TCPServerProxyConfig);
//		return conf;
//	}
	
	public void Validate()throws ExecutionValidationException
	{
		//nothing to validate
	}

	public String ToXML() throws ExecutionSerializationException
	{
		return "<checkpointConfig typeOfProxy=\""+this.TypeOfProxy+"\" " +
				"typeOfMultiplex=\""+this.TypeOfMultiplex+"\" " +
				"timeoutValue=\""+this.TimeoutValue+"\" " +
				"timeoutUnit=\""+this.TimeoutUnit.toString()+"\">";
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "typeOfMultiplex")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.TypeOfMultiplex= IBufferStore.MultiplexType.valueOf(XMLUtils.GetAttribute(XML, "typeOfMultiplex"));
			if(!XMLUtils.AttributeExists(XML, "typeOfProxy")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.TypeOfProxy= IProxy.ProxyType.valueOf(XMLUtils.GetAttribute(XML, "typeOfProxy"));
			if(!XMLUtils.AttributeExists(XML, "timeoutValue")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.TimeoutValue= Long.parseLong(XMLUtils.GetAttribute(XML, "timeoutValue"));
			if(!XMLUtils.AttributeExists(XML, "timeoutUnit")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.TimeoutUnit= TimeUnit.valueOf(XMLUtils.GetAttribute(XML, "timeoutUnit"));
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}
}
