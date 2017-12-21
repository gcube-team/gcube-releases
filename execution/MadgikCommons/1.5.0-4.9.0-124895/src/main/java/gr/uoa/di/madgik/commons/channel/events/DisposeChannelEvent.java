package gr.uoa.di.madgik.commons.channel.events;

import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistry;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * Event indicating that the channel is being disposed by one of its nozzles. Even if multiple nozzles are 
 * registered, once a single nozzle emits this event, the {@link ChannelRegistry} purges the registry from 
 * the associated entry
 * 
 * @author gpapanikos
 */
public class DisposeChannelEvent extends ChannelStateEvent
{
	
	/** The Registry key. */
	private ChannelRegistryKey RegistryKey=null;

	/**
	 * Instantiates a new dispose channel event.
	 */
	public DisposeChannelEvent()
	{
//		this.InitID();
	}

	/**
	 * Instantiates a new dispose channel event.
	 * 
	 * @param RegistryKey the registry key
	 */
	public DisposeChannelEvent(ChannelRegistryKey RegistryKey)
	{
//		this.InitID();
		this.RegistryKey=RegistryKey;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent#GetEventName()
	 */
	public ChannelState.EventName GetEventName()
	{
		return ChannelState.EventName.DisposeChannel;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ISerializable#GetSerializableClassName()
	 */
	public String GetSerializableClassName()
	{
		return this.getClass().getName();
	}
	
	/**
	 * Gets the registry key of the disposed channel
	 * 
	 * @return the channel registry key
	 */
	public ChannelRegistryKey GetRegistryKey()
	{
		return this.RegistryKey;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ISerializable#Decode(byte[])
	 */
	public void Decode(byte[] payload) throws Exception
	{
//		ByteArrayInputStream bin=new ByteArrayInputStream(payload);
//		DataInputStream din=new DataInputStream(bin);
//		this.SetIDLeastSignificantBits(din.readLong());
//		this.SetIDMostSignificantBits(din.readLong());
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ISerializable#Encode()
	 */
	public byte[] Encode() throws Exception
	{
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		DataOutputStream dout=new DataOutputStream(bout);
//		dout.writeLong(this.GetIDLeastSignificantBits());
//		dout.writeLong(this.GetIDMostSignificantBits());
		dout.flush();
		dout.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}

}
