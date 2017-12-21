package gr.uoa.di.madgik.commons.state.store.registry;

import gr.uoa.di.madgik.commons.state.StateManager;
import gr.uoa.di.madgik.commons.state.store.StateStoreInfo;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the in memory registry of all entries persisted with the State Store Repository. It is used to 
 * store metadata information on the available state entries and to retrieve them along with their attached data.
 * The repository does not store any of the actual data the clients provide. In the repository the metadata needed
 * to retrieve the payload stored internally are kept. The operations this class provides are not thread safe and even
 * though it represents a shared resource it does not provide any kind of of resource locking policy. This is
 * left to the wrapping class, {@link StateManager}
 *
 * @author gpapanikos
 */
public class StateStoreRegistry
{

	private static Logger logger = Logger.getLogger(StateStoreRegistry.class.getName());
	private Map<String, StateStoreRegistryKey> Dictionary = null;

	/**
	 * Creates a new instance
	 */
	public StateStoreRegistry()
	{
		this.Dictionary = new Hashtable<String, StateStoreRegistryKey>();
	}

	/**
	 * Retrieved the dictionary data structure used to store the {@link StateStoreRegistryKey} entries
	 *
	 * @return the internal data structure to iterate over entries
	 */
	public Map<String, StateStoreRegistryKey> GetDictionary()
	{
		return this.Dictionary;
	}

	/**
	 * Adds a {@link StateStoreRegistryKey} in the internal data structure. If the key as retrieved from
	 * {@link StateStoreRegistryKey#GetKey()} already exists, the entry it points to is replaced with the provided
	 * one. Once the {@link StateStoreRegistryKey} is inserted in the in memory data structure, it is also persisted
	 * in the State Store Registry file. If the entry already existed, it is simply updated there. 
	 * 
	 * @param Key The entry to insert or update
	 * @param info Information conserning the peristency repositories
	 * @return <code>true</code> if the entries key was not present in the dicitonary before this insert,
	 * <code>false</code> otherwise
	 * @throws java.lang.Exception The insert / update could not be updated
	 */
	public Boolean Put(StateStoreRegistryKey Key, StateStoreInfo info) throws Exception
	{
		Boolean ret = true;
		if (this.Dictionary.containsKey(Key.GetKey()))
		{
			ret = false;
			StateStoreRegistryKey entry = this.Dictionary.get(Key.GetKey());
			if (entry.IsActive())
			{
				ret = true;
			}
			this.Dictionary.put(Key.GetKey(), Key);
			entry = this.Dictionary.get(Key.GetKey());
			this.StoreUpdate(info, entry, false);
		} else
		{
			this.Dictionary.put(Key.GetKey(), Key);
			StateStoreRegistryKey entry = this.Dictionary.get(Key.GetKey());
			this.StoreUpdate(info, entry, false);
		}
		return ret;
	}

	/**
	 * Checks is the provided key is contained in the in memory structure regardless of whether or not it is
	 * active or not
	 *
	 * @param Key the key to check if it is contained
	 * @return <code>true</code> if the key is contained in the in memory data structure, <code>false</code> otherwise
	 */
	public Boolean Contains(String Key)
	{
		return this.Dictionary.containsKey(Key);
	}

	/**
	 * Retrieves the {@link StateStoreRegistryKey} that is associated with the provided key from the
	 * in memory data structure if the key is found in the structure.
	 *
	 * @param Key The key to search for
	 * @return the entry associated with the provided key or null if it is not found
	 */
	public StateStoreRegistryKey Get(String Key)
	{
		if (!this.Dictionary.containsKey(Key))
		{
			return null;
		}
		return this.Dictionary.get(Key);
	}

	/**
	 * Effectivly deletes any {@link StateStoreRegistryKey} entry from the State Store Registry that is
	 * associated with the provided key. The entry is set as inactive using {@link StateStoreRegistryKey#Deactivate()}
	 * and the change is reflected in the registry persistency file
	 * 
	 * @param Key THe key to delete
	 * @param info State Store Registry persistency information
	 * @return <code>true</code> if the update was successful, or <code>false</code> if the key was not found
	 * @throws java.lang.Exception The update could not be performed
	 */
	public Boolean Delete(String Key, StateStoreInfo info) throws Exception
	{
		if (!this.Dictionary.containsKey(Key))
		{
			return false;
		}
		StateStoreRegistryKey entry = this.Dictionary.get(Key);
		entry.Deactivate();
		this.StoreUpdate(info, entry, true);
		return true;
	}

	/**
	 * This methdo initializes the in memory data structure from the State Store registry persistency files deserializing
	 * the entries found there. If no data file is found, the registry file is also cleaned. Semilarly. if no registry file
	 * is found, the data fiule is also cleaned.
	 *
	 * @param info The State Store repository persistency files
	 * @throws java.lang.Exception The deserialization could not be performed
	 */
	public void Deserialize(StateStoreInfo info) throws Exception
	{
		this.Dictionary.clear();
		if (info.GetEntryRegistryFile().exists() && info.GetEntryRegistryFile().isFile())
		{
			DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(info.GetEntryRegistryFile())));
			this.Decode(din);
			if (info.GetEntryDataFile().exists() && info.GetEntryDataFile().isFile())
			{
				if (this.Dictionary.size() == 0)
				{
					if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Registry entries were 0. Cleaning up");
					this.CleanUp(info);
				} else
				{
					if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Registry entries seem valid. Keeping read state entries");
				}
			} else
			{
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Could not find data file. Cleaning up");
				this.CleanUp(info);
			}
		} else
		{
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Could not find registry file. Cleaning up");
			this.CleanUp(info);
		}
	}

	/**
	 * In case of deletes and updates in the registry and data file, there will be data kept in the file that
	 * are no longer needed but simply remain increraqsing the size of the repository files. This method
	 * reorganizes the registry file into a new temporary file it creates that holds only the needed valid
	 * metadata records. During the process each entry that is valid and persisted in the new clean file
	 * is also updated in its in memory representation to point to its new position in the new file. This laves the
	 * registry part of the repository in an inconsistend state until the file that the {@link StateStoreInfo} points
	 * to as the registry repository file is deleted and replaced with the file that this method returns
	 *
	 * @return The file created holding the new clean file to be used as the repository registry file
	 * @throws java.lang.Exception The clean operation could not be performed
	 */
	public File MoveToClean() throws Exception
	{
		File tmp = null;
		RandomAccessFile dout = null;
		try
		{
			tmp = File.createTempFile(UUID.randomUUID().toString(), ".madgik.state.registry.tmp");
			dout = new RandomAccessFile(tmp, "rw");
			this.Encode(dout);
			dout.close();
			return tmp;
		} catch (Exception ex)
		{
			try
			{
				if (dout != null)
				{
					dout.close();
				}
			} catch (Exception exx)
			{
			}
			try
			{
				if (tmp != null)
				{
					tmp.delete();
				}
			} catch (Exception exx)
			{
			}
			throw ex;
		}
	}

	/**
	 * This method stores a new record, or updates an existing one in the state store registry repository file.
	 *
	 * @param info The state store repository persistency files
	 * @param entry the entry to persist
	 * @param update Whether or not to update or store as new the provided entry
	 * @throws java.lang.Exception the stroe / update could not be performed
	 */
	public void StoreUpdate(StateStoreInfo info, StateStoreRegistryKey entry, boolean update) throws Exception
	{
		RandomAccessFile acc = null;
		try
		{
			acc = new RandomAccessFile(info.GetEntryRegistryFile(), "rw");
			acc.seek(0);
			if (!update)
			{
				int entrycount = 0;
				if (info.GetEntryRegistryFile().length() > 0)
				{
					entrycount = acc.readInt();
					acc.seek(0);
				}
				acc.writeInt(entrycount + 1);
				acc.seek(info.GetEntryRegistryFile().length());
			} else
			{
				acc.seek(entry.GetStartingRegistryOffset());
			}
			entry.Encode(acc);
			acc.close();
		} catch (Exception ex)
		{
			if (acc != null)
			{
				acc.close();
			}
			throw ex;
		}
	}

	private void Decode(DataInputStream stream) throws Exception
	{
		int entryCount = stream.readInt();
		for (int i = 0; i < entryCount; i += 1)
		{
			StateStoreRegistryKey entry = new StateStoreRegistryKey();
			entry.Decode(stream);
			if (entry.IsActive())
			{
				this.Dictionary.put(entry.GetKey(), entry);
			}
		}
	}

	private void Encode(RandomAccessFile stream) throws Exception
	{
		int activeCount = 0;
		for (Map.Entry<String, StateStoreRegistryKey> key : this.Dictionary.entrySet())
		{
			if (key.getValue().IsActive())
			{
				activeCount += 1;
			}
		}
		stream.seek(0);
		stream.writeInt(activeCount);
		for (Map.Entry<String, StateStoreRegistryKey> key : this.Dictionary.entrySet())
		{
			if (key.getValue().IsActive())
			{
				key.getValue().Encode(stream);
			}
		}
	}

	private void CleanUp(StateStoreInfo info)
	{
		info.GetEntryDataFile().delete();
		info.GetEntryDataFile().delete();
		this.Dictionary.clear();
	}
}
