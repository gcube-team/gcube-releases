package gr.uoa.di.madgik.commons.channel.events;

import java.io.Serializable;
import java.util.Observable;

//import java.util.UUID;

/**
 * Base class for every object that can be used to be registered by an Observer
 * or send as Event. Event handling must follow well known guidelines. The most
 * important of all as it could have a dramatic effect on the framework usage is
 * the processing of a caught event. When an event is caught the processing to
 * be performed should be kept minimal. Should a proxy for example catches an
 * event and in the event handler communicates with a remote location this would
 * mean that the whole framework will have to wait for this communication to
 * happen.
 * 
 * @author gpapanikos
 */
public abstract class ChannelStateEvent extends Observable implements ISerializable, Serializable
{

	private static final long serialVersionUID = 1L;

	// private long IDLeastSignificantBits = 0;
	// private long IDMostSignificantBits = 0;
	/** The emitting nozzle identifier least significant bits. */
	private long EmitingNozzleIdentifierLeastSignificantBits = 0;

	/** The emitting nozzle identifier most significant bits. */
	private long EmitingNozzleIdentifierMostSignificantBits = 0;

	/**
	 * Notify everyone that has registered for notifications to this object. The
	 * argument of the update notification is the ChannelStateEvent that carries
	 * the information of the update not the ChannelStateEvent that the Observer
	 * has originally registered with
	 * 
	 * @param ChangeEvent
	 *            The ChannelStateEvent carrying the update information
	 */
	public void NotifyChange(ChannelStateEvent ChangeEvent)
	{
		this.setChanged();
		notifyObservers(ChangeEvent);
	}

	/**
	 * Gets the event name.
	 * 
	 * @return the channel state. event name
	 */
	public abstract ChannelState.EventName GetEventName();

	// protected String SerializeID()
	// {
	// return Long.toString(this.IDLeastSignificantBits) + "#" +
	// Long.toString(this.IDMostSignificantBits);
	// }

	/**
	 * Transforms the id kept as two longs , the least and most significant bits of a UUID to a string
	 *
	 * @return the serialized UUID of the emitting nozzle
	 */
	protected String SerializeEmmitingNozzleID()
	{
		return Long.toString(this.EmitingNozzleIdentifierLeastSignificantBits) + "#" + Long.toString(this.EmitingNozzleIdentifierMostSignificantBits);
	}

	/**
	 * Deserializes a UUID as returned from {@link ChannelStateEvent#SerializeEmmitingNozzleID()} to the respective least and most
	 * significant bits
	 *
	 * @param Serialization The serialization of the id
	 */
	protected void DeserializeID(String Serialization)
	{
		String[] parts = Serialization.split("#");
		if (parts.length != 2) { throw new IllegalArgumentException("Record ID serialization " + Serialization + " not valid"); }
		// this.IDLeastSignificantBits = Long.parseLong(parts[0]);
		// this.IDMostSignificantBits = Long.parseLong(parts[1]);
	}

	// public long GetIDLeastSignificantBits()
	// {
	// return this.IDLeastSignificantBits;
	// }
	//
	// public long GetIDMostSignificantBits()
	// {
	// return this.IDMostSignificantBits;
	// }
	//
	// public void SetIDLeastSignificantBits(long IDLeastSignificantBits)
	// {
	// this.IDLeastSignificantBits = IDLeastSignificantBits;
	// }
	//
	// public void SetIDMostSignificantBits(long IDMostSignificantBits)
	// {
	// this.IDMostSignificantBits = IDMostSignificantBits;
	// }

	/**
	 * Gets the emitting nozzle identifier least significant bits.
	 * 
	 * @return the long
	 */
	public long GetEmitingNozzleIdentifierLeastSignificantBits()
	{
		return this.EmitingNozzleIdentifierLeastSignificantBits;
	}

	/**
	 * Gets the emitting nozzle identifier most significant bits.
	 * 
	 * @return the long
	 */
	public long GetEmitingNozzleIdentifierMostSignificantBits()
	{
		return this.EmitingNozzleIdentifierMostSignificantBits;
	}

	/**
	 * Sets the emitting nozzle identifier least significant bits.
	 * 
	 * @param EmitingNozzleIdentifierLeastSignificantBits the emitting nozzle identifier least significant bits
	 */
	public void SetEmitingNozzleIdentifierLeastSignificantBits(long EmitingNozzleIdentifierLeastSignificantBits)
	{
		this.EmitingNozzleIdentifierLeastSignificantBits = EmitingNozzleIdentifierLeastSignificantBits;
	}

	/**
	 * Sets the emitting nozzle identifier most significant bits.
	 * 
	 * @param EmitingNozzleIdentifierMostSignificantBits
	 *            the emitting nozzle identifier most significant bits
	 */
	public void SetEmitingNozzleIdentifierMostSignificantBits(long EmitingNozzleIdentifierMostSignificantBits)
	{
		this.EmitingNozzleIdentifierMostSignificantBits = EmitingNozzleIdentifierMostSignificantBits;
	}

	// protected void InitID()
	// {
	// UUID ID = UUID.randomUUID();
	// this.IDLeastSignificantBits = ID.getLeastSignificantBits();
	// this.IDMostSignificantBits = ID.getMostSignificantBits();
	// }

	// public String GetEventID()
	// {
	// return this.SerializeID();
	// }

	/**
	 * Retrieves the emitting nozzle UUID of the event associated with it during initialization
	 *
	 * @return The emitting nozzle UUID of the event as returned by {@link ChannelStateEvent#SerializeEmmitingNozzleID()}
	 */
	public String GetEmitingNozzleID()
	{
		return this.SerializeEmmitingNozzleID();
	}
}
