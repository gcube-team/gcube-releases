package gr.uoa.di.madgik.commons.channel.proxy.tcp;

import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class provides configuration on the creation of a new channel through a {@link ChannelInlet}. This configuration
 * dictates the creation of a channel that can be accessed either locally or remotely through a TCP connection
 * 
 * @author gpapanikos
 */
public class TCPServerNozzleConfig implements INozzleConfig
{
	private static final long serialVersionUID = 1L;

	/** The Channel proxy. */
	private IChannelProxy ChannelProxy=null;
	
	/** The Broadcast. */
	private boolean Broadcast=false;
	
	/** The Restrict broadcast. */
	private int RestrictBroadcast=0;
	
	public TCPServerNozzleConfig(){}
	
	/**
	 * Instantiates a new TCP nozzle configuration
	 * 
	 * @param Config the configuration of the {@link TCPTransportServer}
	 * @param Broadcast the value to set as defined by the {@link INozzleConfig#SetIsBroadcast(boolean)}
	 * @param RestrictBroadcast the value to set as defined by the {@link INozzleConfig#SetRestrictBroadcast(int)}
	 */
	public TCPServerNozzleConfig(boolean Broadcast,int RestrictBroadcast)
	{
		this.ChannelProxy=new TCPServerChannelProxy();
		this.Broadcast=Broadcast;
		this.RestrictBroadcast=RestrictBroadcast;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig#GetChannelProxy()
	 */
	public IChannelProxy GetChannelProxy()
	{
		if(this.ChannelProxy==null)
		{
			this.ChannelProxy=new TCPServerChannelProxy();
		}
		return this.ChannelProxy;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig#SetProxy(gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy)
	 */
	public void SetProxy(IChannelProxy ChannelProxy)
	{
		this.ChannelProxy=ChannelProxy;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig#GetIsBroadcast()
	 */
	public boolean GetIsBroadcast()
	{
		return this.Broadcast;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig#SetIsBroadcast(boolean)
	 */
	public void SetIsBroadcast(boolean Broadcast)
	{
		this.Broadcast=Broadcast;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig#GetRestrictBroadcast()
	 */
	public int GetRestrictBroadcast()
	{
		return this.RestrictBroadcast;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig#SetRestrictBroadcast(int)
	 */
	public void SetRestrictBroadcast(int RestrictBroadcast)
	{
		this.RestrictBroadcast=RestrictBroadcast;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig#Dispose()
	 */
	public void Dispose()
	{
		this.ChannelProxy.Dispose();
	}

	public ConfigType GetConfigType()
	{
		return ConfigType.TCP;
	}

	public void FromXML(String XML) throws Exception
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new Exception("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws Exception
	{
		if(!XMLUtils.AttributeExists((Element)XML, "type") || !XMLUtils.AttributeExists((Element)XML, "broadcast") || !XMLUtils.AttributeExists((Element)XML, "restrict")) throw new Exception ("Not valid serialization of element");
		if(!INozzleConfig.ConfigType.valueOf(XMLUtils.GetAttribute((Element)XML, "type")).equals(this.GetConfigType())) throw new Exception ("Not valid serialization of element");
		this.Broadcast=Boolean.parseBoolean(XMLUtils.GetAttribute((Element)XML, "broadcast"));
		this.RestrictBroadcast=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "restrict"));
	}

	public String ToXML() throws Exception
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<nozzleConfig type=\""+this.GetConfigType().toString()+"\" broadcast=\""+this.Broadcast+"\" restrict=\""+this.RestrictBroadcast+"\"/>");
		return buf.toString();
	}
}
