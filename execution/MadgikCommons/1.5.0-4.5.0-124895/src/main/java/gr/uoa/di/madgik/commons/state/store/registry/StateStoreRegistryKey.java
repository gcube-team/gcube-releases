package gr.uoa.di.madgik.commons.state.store.registry;

import gr.uoa.di.madgik.commons.configuration.ConfigurationManager;
import gr.uoa.di.madgik.commons.state.store.data.ISerializable;
import java.io.DataInputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * The {@link StateStoreRegistryKey} class holds metadata information for one entry registrered with the State
 * repository. The metadata kept for each such entry include the key with which the entry is identified, the offset
 * within the registry file that this metadata entry is persisted, the location of the actual data the key is associated
 * with in the data file, whether or not the entry is to be considered active or a client has deleted it and the type
 * of the entry data.
 *
 * @author gpapanikos
 */
public class StateStoreRegistryKey
{

	/**
	 * The type of data an entry persisted with the state repository holds
	 */
	public enum EntryType
	{

		/**
		 * Defines a plain alphanumeric sequence
		 */
		Alphanumeric,
		/**
		 * Defines a file reachable within the local filesystem
		 */
		File,
		/**
		 * Defines a plain byte sequence
		 */
		Bytearray,
		/**
		 * Defines any object that can be serialized and deserialized as defined by {@link Serializable}
		 */
		Serializable,
		/**
		 * Defines any object that can be serialized and deserialized as defined by {@link ISerializable}
		 */
		ISerializable
	}
	private String Key = null;
	private long StartingDataOffset = 0;
	private long EndingDataOffset = 0;
	private long StartingRegistryOffset = 0;
	private boolean Active = true;
	private StateStoreRegistryKey.EntryType TypeOfEntry = EntryType.Alphanumeric;

	/**
	 * Creates a new instance
	 */
	public StateStoreRegistryKey()
	{
	}

	/**
	 * Creates a new instance with the provided values
	 *
	 * @param Key The key with which this entry is reachable
	 * @param StartingDataOffset The starting offset of the actual data in the data file
	 * @param EndingDataOffset The ending offset of the actual data in the data file
	 * @param StartingRegistryOffset The starting offset of this metadata record in the registry file
	 * @param TypeOfEntry The type of the actual data stored
	 */
	public StateStoreRegistryKey(String Key, long StartingDataOffset, long EndingDataOffset, long StartingRegistryOffset, StateStoreRegistryKey.EntryType TypeOfEntry)
	{
		this.Key = Key;
		this.StartingDataOffset = StartingDataOffset;
		this.EndingDataOffset = EndingDataOffset;
		this.StartingRegistryOffset = StartingRegistryOffset;
		this.TypeOfEntry = TypeOfEntry;
	}

	/**
	 * Whether or not the entry is considered active or it has been deleted
	 *
	 * @return whether or not the entry is considered active or it has been deactivated
	 */
	public Boolean IsActive()
	{
		return this.Active;
	}

	/**
	 * Sets the entry as deactivated
	 */
	public void Deactivate()
	{
		this.Active = false;
	}

	/**
	 * Retrieves the key with which the entry is identified
	 *
	 * @return The key
	 */
	public String GetKey()
	{
		return this.Key;
	}

	/**
	 * Retrieves the type of entry
	 *
	 * @return the type of entry
	 */
	public StateStoreRegistryKey.EntryType GetTypeOfEntry()
	{
		return this.TypeOfEntry;
	}

	/**
	 * Retrieves the offset in the registry file where this metadata entry is stored
	 *
	 * @return the offset
	 */
	public long GetStartingRegistryOffset()
	{
		return this.StartingRegistryOffset;
	}

	/**
	 * Sets the offset in the registry file where this metadata entry is stored
	 *
	 * @param Offset the offset
	 */
	public void SetStartingRegistryOffset(long Offset)
	{
		this.StartingRegistryOffset = Offset;
	}

	/**
	 * Retrieves the offset in the data falie where the entry's data are stored
	 *
	 * @return the offset
	 */
	public long GetStartingDataOffset()
	{
		return this.StartingDataOffset;
	}

	/**
	 * Sets the offset in the data falie where the entry's data are stored
	 *
	 * @param Offset the offset
	 */
	public void SetStartingDataOffset(long Offset)
	{
		this.StartingDataOffset = Offset;
	}

	/**
	 * Retrieves the ending offset in the data fale where the entry's data are stored
	 *
	 * @return The offset
	 */
	public long GetEndingDataOffset()
	{
		return this.EndingDataOffset;
	}

	/**
	 * Sets the ending offset in the data fale where the entry's data are stored
	 *
	 * @param Offset the offset
	 */
	public void SetEndingDataOffset(long Offset)
	{
		this.EndingDataOffset = Offset;
	}

	/**
	 * Stores this instance in the provided file leaving the file pointer at the end of the written entry.
	 * The method also update the position of the metadata entry it just written to point to the position
	 * of the file it started serializing its content
	 *
	 * @param stream the file to write the entry to
	 * @throws java.lang.Exception The encoding could not be performed
	 */
	public void Encode(RandomAccessFile stream) throws Exception
	{
		this.StartingRegistryOffset = stream.getFilePointer();
		byte[] keybytes = this.Key.getBytes(ConfigurationManager.GetStringParameter("EncodingCharset"));
		stream.writeInt(keybytes.length);
		stream.write(keybytes);
		stream.writeLong(this.StartingDataOffset);
		stream.writeLong(this.EndingDataOffset);
		stream.writeLong(this.StartingRegistryOffset);
		stream.writeBoolean(this.Active);
		stream.writeInt(this.TypeOfEntry.ordinal());
	}

	/**
	 * Decodes a {@link StateStoreRegistryKey} as the {@link StateStoreRegistryKey#Encode(java.io.RandomAccessFile)}
	 * wrote it in the provided file.
	 *
	 * @param stream The file to read the entry from
	 * @throws java.lang.Exception The decoding could not be performed
	 */
	public void Decode(DataInputStream stream) throws Exception
	{
		int sizeofkey = stream.readInt();
		byte[] keybytes = new byte[sizeofkey];
		stream.readFully(keybytes);
		this.Key = new String(keybytes, ConfigurationManager.GetStringParameter("EncodingCharset"));
		this.StartingDataOffset = stream.readLong();
		this.EndingDataOffset = stream.readLong();
		this.StartingRegistryOffset = stream.readLong();
		this.Active = stream.readBoolean();
		this.TypeOfEntry = EntryType.values()[stream.readInt()];
	}
}
