package gr.uoa.di.madgik.commons.channel.events;

import gr.uoa.di.madgik.commons.utils.ObjectUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Event containing payload that the sender wants to provide to listeners
 * 
 * @author gpapanikos
 */
public class ObjectPayloadChannelEvent extends ChannelPayloadStateEvent
{
	
	/** The Value. */
	private ISerializable Value = null;

	/**
	 * Instantiates a new object payload channel event.
	 */
	public ObjectPayloadChannelEvent()
	{
//		this.InitID();
	}

	/**
	 * Instantiates a new object payload channel event.
	 * 
	 * @param Value the payload that the event contains
	 */
	public ObjectPayloadChannelEvent(ISerializable Value)
	{
		this.Value = Value;
//		this.InitID();
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent#GetEventName()
	 */
	public ChannelState.EventName GetEventName()
	{
		return ChannelState.EventName.ObjectPayload;
	}

	/**
	 * Retrieves the containing payload
	 * 
	 * @return the contained payload
	 */
	public ISerializable GetValue()
	{
		return this.Value;
	}

	/**
	 * Sets the containing payload
	 * 
	 * @param Value the containing payload
	 */
	public void SetValue(ISerializable Value)
	{
		this.Value = Value;
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
		ByteArrayInputStream bin = new ByteArrayInputStream(payload);
		DataInputStream din = new DataInputStream(bin);
//		this.SetIDLeastSignificantBits(din.readLong());
//		this.SetIDMostSignificantBits(din.readLong());
		this.SetEmitingNozzleIdentifierLeastSignificantBits(din.readLong());
		this.SetEmitingNozzleIdentifierMostSignificantBits(din.readLong());
		Object obj = ObjectUtils.InstantiateWithDefaultConstructor(din.readUTF());
		if (!(obj instanceof ISerializable)) throw new Exception("Provided object is not of expected type");
		this.Value = (ISerializable) obj;
		byte[] buf = new byte[din.readInt()];
		din.readFully(buf);
		this.Value.Decode(buf);
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.events.ISerializable#Encode()
	 */
	public byte[] Encode() throws Exception
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
//		dout.writeLong(this.GetIDLeastSignificantBits());
//		dout.writeLong(this.GetIDMostSignificantBits());
		dout.writeLong(this.GetEmitingNozzleIdentifierLeastSignificantBits());
		dout.writeLong(this.GetEmitingNozzleIdentifierMostSignificantBits());
		dout.writeUTF(this.Value.GetSerializableClassName());
		byte[] buf=this.Value.Encode();
		dout.writeInt(buf.length);
		dout.write(buf);
		dout.flush();
		dout.close();
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}

}
