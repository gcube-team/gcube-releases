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
public class BytePayloadChannelEvent extends ChannelPayloadStateEvent
{

	/** The contained payload. */
	private byte[] Value = null;

	/**
	 * Instantiates a new byte payload channel event.
	 */
	public BytePayloadChannelEvent()
	{
		// this.InitID();
	}

	/**
	 * Instantiates a new byte payload channel event.
	 * 
	 * @param Value
	 *            the payload the event contains
	 */
	public BytePayloadChannelEvent(byte[] Value)
	{
		this.Value = Value;
		// this.InitID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent#GetEventName()
	 */
	public ChannelState.EventName GetEventName()
	{
		return ChannelState.EventName.BytePayload;
	}

	/**
	 * Retrieves the contained payload
	 * 
	 * @return the payload
	 */
	public byte[] GetValue()
	{
		return this.Value;
	}

	/**
	 * Sets the contained payload
	 * 
	 * @param Value
	 *            the payload
	 */
	public void SetValue(byte[] Value)
	{
		this.Value = Value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seegr.uoa.di.madgik.commons.channel.events.ISerializable#
	 * GetSerializableClassName()
	 */
	public String GetSerializableClassName()
	{
		return this.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.uoa.di.madgik.commons.channel.events.ISerializable#Decode(byte[])
	 */
	public void Decode(byte[] payload) throws Exception
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(payload);
		DataInputStream din = new DataInputStream(bin);
		// this.SetIDLeastSignificantBits(din.readLong());
		// this.SetIDMostSignificantBits(din.readLong());
		this.SetEmitingNozzleIdentifierLeastSignificantBits(din.readLong());
		this.SetEmitingNozzleIdentifierMostSignificantBits(din.readLong());
		this.Value = new byte[din.readInt()];
		din.readFully(this.Value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.uoa.di.madgik.commons.channel.events.ISerializable#Encode()
	 */
	public byte[] Encode() throws Exception
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		// dout.writeLong(this.GetIDLeastSignificantBits());
		// dout.writeLong(this.GetIDMostSignificantBits());
		dout.writeLong(this.GetEmitingNozzleIdentifierLeastSignificantBits());
		dout.writeLong(this.GetEmitingNozzleIdentifierMostSignificantBits());
		dout.writeInt(this.Value.length);
		dout.write(this.Value);
		dout.flush();
		dout.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}
}
