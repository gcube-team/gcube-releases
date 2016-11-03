package gr.uoa.di.madgik.commons.channel.events;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Event containing payload that the sender wants to provide to listeners
 * 
 * @author gpapanikos
 */
public class StringPayloadChannelEvent extends ChannelPayloadStateEvent
{
	
	/** The Value. */
	private String Value=null; 

	/**
	 * Instantiates a new string payload channel event.
	 */
	public StringPayloadChannelEvent()
	{
//		this.InitID();
	}

	/**
	 * Instantiates a new string payload channel event.
	 * 
	 * @param Value the payload that the event contains
	 */
	public StringPayloadChannelEvent(String Value)
	{
		this.Value=Value;
//		this.InitID();
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent#GetEventName()
	 */
	public ChannelState.EventName GetEventName()
	{
		return ChannelState.EventName.StringPayload;
	}
	
	/**
	 * Retrieves the payload the event contains
	 * 
	 * @return the contained payload
	 */
	public String GetValue()
	{
		return this.Value;
	}
	
	/**
	 * Sets the contained payload
	 * 
	 * @param Value the contained payload
	 */
	public void SetValue(String Value)
	{
		this.Value=Value;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ISerializable#GetSerializableClassName()
	 */
	public String GetSerializableClassName()
	{
		return this.getClass().getName();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ISerializable#Decode(byte[])
	 */
	public void Decode(byte[] payload) throws Exception
	{
		ByteArrayInputStream bin=new ByteArrayInputStream(payload);
		DataInputStream din=new DataInputStream(bin);
//		this.SetIDLeastSignificantBits(din.readLong());
//		this.SetIDMostSignificantBits(din.readLong());
		this.SetEmitingNozzleIdentifierLeastSignificantBits(din.readLong());
		this.SetEmitingNozzleIdentifierMostSignificantBits(din.readLong());
		this.Value=din.readUTF();
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
		dout.writeLong(this.GetEmitingNozzleIdentifierLeastSignificantBits());
		dout.writeLong(this.GetEmitingNozzleIdentifierMostSignificantBits());
		dout.writeUTF(this.Value);
		dout.flush();
		dout.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}

}
