package gr.uoa.di.madgik.execution.plan.element.invocable;

import java.io.Serializable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ExecutionContextConfigBase implements Serializable
{
	private static final long serialVersionUID = 1L;

	public enum ContextConfigType
	{
		Simple,
		WS
	}
	
	public enum ContextProxyType
	{
		None,
		Local,
		TCP
	}
	private static Logger logger=LoggerFactory.getLogger(ExecutionContextConfigBase.class);
	public ContextProxyType ProxyType=ContextProxyType.None;
	public boolean KeepContextAlive=false;
	
	public IWriterProxy GetProxy()
	{
		IWriterProxy proxy=null;
		switch(this.ProxyType)
		{
			case Local:
			{
				proxy=new LocalWriterProxy();
				break;
			}
			case TCP:
			{
				proxy=new TCPWriterProxy();
				break;
			}
			case None:
			{
				break;
			}
			default:
			{
				logger.warn("Unrecognized proxy type found. Returning null");
				break;
			}
		}
		return proxy;
	}

	public abstract String ToXML() throws ExecutionSerializationException;

	public abstract void FromXML(Element XML) throws ExecutionSerializationException;
	
	public abstract ContextConfigType GetContextConfigType();
	
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

	public void Validate() throws ExecutionValidationException
	{
		//no validation
	}

	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
	}

}
