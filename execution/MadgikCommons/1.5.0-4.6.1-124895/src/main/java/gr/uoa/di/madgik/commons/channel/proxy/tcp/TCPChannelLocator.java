package gr.uoa.di.madgik.commons.channel.proxy.tcp;

import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey;
import gr.uoa.di.madgik.commons.utils.URIUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URI;
import java.util.Map;

/**
 * Defines a TCP proxy locator capable of identifying a channel wither within the same host or from
 * different hosts
 * 
 * @author gpapanikos
 */
public class TCPChannelLocator implements IChannelLocator
{
	
	private static final long serialVersionUID = 1L;

	/** The Registry key. */
	private ChannelRegistryKey RegistryKey=null;
	
	/** The Host name. */
	private String HostName = null;
	
	/** The Port. */
	private int Port = 0;

	/**
	 * Instantiates a new tCP channel locator.
	 */
	public TCPChannelLocator()
	{
		
	}

	/**
	 * Instantiates a new TCP channel locator.
	 * 
	 * @param HostName the host name
	 * @param Port the port
	 */
	public TCPChannelLocator(String HostName, int Port)
	{
		this.HostName = HostName;
		this.Port = Port;
	}

	/**
	 * Gets the host name.
	 * 
	 * @return the host name
	 */
	public String GetHostName()
	{
		return this.HostName;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int GetPort()
	{
		return this.Port;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#Decode(byte[])
	 */
	public void Decode(byte[] payload) throws Exception
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(payload);
		DataInputStream din = new DataInputStream(bin);
		String loctype=din.readUTF();
		if (!loctype.equalsIgnoreCase(this.GetLocatorType().toString()))
		{
			throw new Exception("Not valid serialization for this channel locator. Type found "+loctype);
		}
		this.HostName = din.readUTF();
		this.Port = din.readInt();
		this.RegistryKey=new ChannelRegistryKey(din.readUTF());
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#Encode()
	 */
	public byte[] Encode() throws Exception
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		dout.writeUTF(this.GetLocatorType().toString());
		dout.writeUTF(this.GetHostName());
		dout.writeInt(this.GetPort());
		dout.writeUTF(this.GetRegistryKey().GetUniqueID());
		dout.flush();
		dout.close();
		return bout.toByteArray();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#GetLocatorType()
	 */
	public LocatorType GetLocatorType()
	{
		return IChannelLocator.LocatorType.TCP;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#GetRegistryKey()
	 */
	public ChannelRegistryKey GetRegistryKey()
	{
		return this.RegistryKey;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#SetRegistryKey(gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey)
	 */
	public void SetRegistryKey(ChannelRegistryKey RegistryKey)
	{
		this.RegistryKey=RegistryKey;
	}
	
	public URI ToURI() throws Exception
	{
		String queryString=URIUtils.BuildQueryString(this.GetRegistryKey().GetUniqueID());
		return new URI(URIUtils.SchemeChannel,null,this.GetHostName(),this.GetPort(),null,queryString,this.GetLocatorType().toString());
	}
	
	public void FromURI(URI locator) throws Exception
	{
		if(!locator.getScheme().equals(URIUtils.SchemeChannel)) throw new Exception("Invalid scheme");
		if(!LocatorType.valueOf(locator.getFragment()).equals(this.GetLocatorType())) throw new Exception("Invalid type");
		this.HostName=locator.getHost();
		this.Port=locator.getPort();
		Map<String, String> params=URIUtils.ParseQueryString(locator.getQuery());
		this.RegistryKey= new ChannelRegistryKey(URIUtils.GetID(params, true));
	}

//	/* (non-Javadoc)
//	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#ToXML()
//	 */
//	public String ToXML() throws Exception
//	{
//		StringBuilder buf = new StringBuilder();
//		buf.append("<locator type=\"" + this.GetLocatorType().toString() + "\" ID=\"" + this.GetRegistryKey().GetUniqueID() + "\">");
//		buf.append("<hostname value=\"" + this.GetHostName() + "\"/>");
//		buf.append("<port value=\"" + this.GetPort() + "\"/>");
//		buf.append("</locator>");
//		return buf.toString();
//	}
//
//	/* (non-Javadoc)
//	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#FromXML(java.lang.String)
//	 */
//	public void FromXML(String xml) throws Exception
//	{
//		Document doc = XMLUtils.Deserialize(xml);
//		if (!XMLUtils.AttributeExists(doc.getDocumentElement(), "type") || !XMLUtils.AttributeExists(doc.getDocumentElement(), "ID"))
//		{
//			throw new Exception("Not valid serialization for this chanel locator");
//		}
//		if (!XMLUtils.GetAttribute(doc.getDocumentElement(), "type").equalsIgnoreCase(this.GetLocatorType().toString()))
//		{
//			throw new Exception("Not valid serialization for this channel locator");
//		}
//		this.RegistryKey= new ChannelRegistryKey(XMLUtils.GetAttribute(doc.getDocumentElement(), "ID"));
//		Element tmp = XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "hostname");
//		if (tmp == null || !XMLUtils.AttributeExists(tmp, "value"))
//		{
//			throw new Exception("Not valid serialization for this channel locator");
//		}
//		this.HostName = XMLUtils.GetAttribute(tmp, "value");
//		tmp = XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "port");
//		if (tmp == null || !XMLUtils.AttributeExists(tmp, "value"))
//		{
//			throw new Exception("Not valid serialization for this channel locator");
//		}
//		this.Port = Integer.parseInt(XMLUtils.GetAttribute(tmp, "value"));
//	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.GetLocatorType().toString() + "#" + this.GetHostName() + "#" + this.GetPort() + "#" +  this.GetRegistryKey();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof TCPChannelLocator))
		{
			return false;
		}
		if (this.GetRegistryKey().equals(((TCPChannelLocator) o).GetRegistryKey()) && this.GetHostName().equals(((TCPChannelLocator) o).GetHostName()) && this.GetPort() == ((TCPChannelLocator) o).GetPort())
		{
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 89 * hash + (this.HostName != null ? this.HostName.hashCode() : 0);
		hash = 89 * hash + this.Port;
		hash = 89 * hash + (this.RegistryKey != null ? this.RegistryKey.hashCode() : 0);
		return hash;
	}
}
