package gr.uoa.di.madgik.commons.channel.proxy.local;

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
 * Defines a local proxy locator capable of identifying a channel in the same address
 * space as the one it was created in
 * 
 * @author gpapanikos
 */
public class LocalChannelLocator implements IChannelLocator
{
	
	private static final long serialVersionUID = 1L;
	/** The Registry key. */
	private ChannelRegistryKey RegistryKey=null;

	/**
	 * Instantiates a new local channel locator.
	 */
	public LocalChannelLocator()
	{
		
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#Decode(byte[])
	 */
	public void Decode(byte[] payload) throws Exception
	{
		ByteArrayInputStream bin=new ByteArrayInputStream(payload);
		DataInputStream din=new DataInputStream(bin);
		if (!din.readUTF().equalsIgnoreCase(this.GetLocatorType().toString()))
		{
			throw new Exception("Not valid serialization for this channel locator");
		}
		this.RegistryKey=new ChannelRegistryKey(din.readUTF());
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#Encode()
	 */
	public byte[] Encode() throws Exception
	{
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		DataOutputStream dout=new DataOutputStream(bout);
		dout.writeUTF(this.GetLocatorType().toString());
		dout.writeUTF(this.RegistryKey.GetUniqueID());
		dout.flush();
		dout.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#GetLocatorType()
	 */
	public LocatorType GetLocatorType()
	{
		return IChannelLocator.LocatorType.Local;
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
		String queryString=URIUtils.BuildQueryString(this.RegistryKey.GetUniqueID());
		return new URI(URIUtils.SchemeChannel,null,"localhost",-1,null,queryString,this.GetLocatorType().toString());
	}
	
	public void FromURI(URI locator) throws Exception
	{
		try
		{
			if(!locator.getScheme().equals(URIUtils.SchemeChannel)) throw new Exception("Invalid scheme");
			if(!LocatorType.valueOf(locator.getFragment()).equals(this.GetLocatorType())) throw new Exception("Invalid type");
			Map<String, String> params=URIUtils.ParseQueryString(locator.getQuery());
			this.RegistryKey = new ChannelRegistryKey(URIUtils.GetID(params, true));
		}
		catch(Exception ex)
		{
			throw new Exception("Could not parse URI "+locator,ex);
		}
	}
//	
//	/* (non-Javadoc)
//	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator#ToXML()
//	 */
//	public String ToXML() throws Exception
//	{
//		StringBuilder buf = new StringBuilder();
//		buf.append("<locator type=\"" + this.GetLocatorType().toString() + "\" ID=\"" + this.RegistryKey.GetUniqueID() + "\" />");
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
//			throw new Exception("Not valid serialization for this proxy locator");
//		}
//		if (!XMLUtils.GetAttribute(doc.getDocumentElement(), "type").equalsIgnoreCase(this.GetLocatorType().toString()))
//		{
//			throw new Exception("Not valid serialization for this proxy locator");
//		}
//		this.RegistryKey = new ChannelRegistryKey(XMLUtils.GetAttribute(doc.getDocumentElement(), "ID"));
//	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.RegistryKey.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof LocalChannelLocator))
		{
			return false;
		}
		if (this.RegistryKey.equals(((LocalChannelLocator) o).RegistryKey))
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
		int hash = 5;
		return 94 * hash + (this.RegistryKey != null ? this.RegistryKey.hashCode() : 0);
	}
}
