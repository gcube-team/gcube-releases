package gr.uoa.di.madgik.commons.state;

import gr.uoa.di.madgik.commons.configuration.ConfigurationManager;
import gr.uoa.di.madgik.commons.state.store.StateStoreInfo;
import gr.uoa.di.madgik.commons.state.store.data.ISerializable;
import gr.uoa.di.madgik.commons.state.store.data.StateStoreData;
import gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistry;
import gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey;
import gr.uoa.di.madgik.commons.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link StateManager} class is a utility class serving as the entry point to the State Store Repository utility.
 * Its purpose is to interface between clients and a persistency key-value system to enable clients to stroe state they may
 * need in case of restarts or simply as lognterm or temporary storage without needing to directly interact with the filesystem
 * or other data stores. The utility uses two files in the local filesystem to persist information it needs. One file is used to
 * store metadata on the entries that are stored and the other tha actual payload of the provided entries. The files that are used
 * are retrived through the {@link ConfigurationManager} where the {@link StateManager} expects to find under the
 * <code>StateManager.StateStoreInfo</code> key an instnace of the {@link StateStoreInfo} class holding the files to use. The
 * {@link StateStoreData} instnace also expects to find some information in the {@link ConfigurationManager}.
 * The actual markup that needs to be stored in the configuration file is shown below
 * <p>
 * <pre>
 * {@code
 *
 * <param name="EncodingCharset" type="String" generated="false" internal="false">UTF-8</param>
 * <param name="StateManager.CleanUpOnInit" type="BooleanPrimitive" generated="false" internal="false">CleanUp data and registry files on initialization</param>
 * <param name="StateManager.EntryRegistryPathName" type="String" generated="false" internal="true">path to registry file to use</param>
 * <param name="StateManager.EntryRegistryFile" type="Object" generated="false" internal="false" shared="false">
 *  <class value="java.io.File" />
 *   <constructor>
 *    <arguments>
 *     <arg order="1" name="pathname" param="StateManager.EntryRegistryPathName"/>
 *    </arguments>
 *   </constructor>
 * </param>
 * <param name="StateManager.EntryDataPathName" type="String" generated="false" internal="true">path to data file to use</param>
 * <param name="StateManager.EntryDataFile" type="Object" generated="false" internal="false" shared="false">
 *  <class value="java.io.File" />
 *   <constructor>
 *    <arguments>
 *	   <arg order="1" name="pathname" param="StateManager.EntryDataPathName"/>
 *    </arguments>
 *   </constructor>
 * </param>
 * <param name="StateManager.StateStoreInfo" type="Object" generated="false" internal="false" shared="false">
 *  <class value="gr.uoa.di.madgik.state.store.StateStoreInfo" />
 *  <constructor>
 *   <arguments>
 *    <arg order="1" name="EntryRegistryFile" param="StateManager.EntryRegistryFile"/>
 *    <arg order="2" name="EntryDataFile" param="StateManager.EntryDataFile"/>
 *   </arguments>
 *  </constructor>
 * </param>
 * 
 * }
 * </pre>
 * </p>
 * Different types of entries can be stored to the State Store Repository whose values will be retrieved and storerd with different
 * ways. These types are the ones depicted by the {@link gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey.EntryType} enumeration.
 * The {@link StateManager} provides serialized access to its resources.
 *
 * TODO Add functionality to compress data before storing them
 * TODO More sophisticated CleanUp initiation policy
 * 
 * @author gpapanikos
 */
public class StateManager
{

	private static Logger logger = Logger.getLogger(StateManager.class.getName());
	private static StateStoreInfo Info = null;
	private static StateStoreRegistry Registry = null;


	static
	{
		try
		{
			StateManager.Info = (StateStoreInfo) ConfigurationManager.GetParameter("StateManager.StateStoreInfo");
			StateManager.Registry = new StateStoreRegistry();
			StateManager.Registry.Deserialize(StateManager.Info);
			if(ConfigurationManager.GetBooleanParameter("StateManager.CleanUpOnInit"))
			{
				StateManager.Compact();
			}
		} catch (Exception ex)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Could not initialize state manager", ex);
		}
	}

	/**
	 * Retrieves a string representation of the {@link StateStoreInfo}
	 * 
	 * @return The string representation of the {@link StateStoreInfo}
	 */
	public static String GetStateStoreInfo()
	{
		return StateManager.Info.toString();
	}

	/**
	 * If the priovided key is available in the State Store as dictated by {@link StateManager#Contains(java.lang.String)}
	 * the {@link StateStoreRegistry#Delete(java.lang.String, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)} method is called
	 * and its return value returned. Otherwise, <code>false</code> is returned
	 *
	 * @param Key The key whose entry should be marked as inactive
	 * @return <code>false</code> if the key is not assciated with an entry, otherwise the return value of
	 * {@link StateStoreRegistry#Delete(java.lang.String, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * @throws java.lang.Exception The delete could not be performed
	 */
	public static synchronized boolean Delete(String Key) throws Exception
	{
		if (!StateManager.Contains(Key))
		{
			return false;
		}
		return StateManager.Registry.Delete(Key, StateManager.Info);
	}

	/**
	 * Checks if the provided key is contained in the State Store Repository. It does not
	 * only check if the key is stored in the internal data structuire but also that the associated
	 * {@link StateStoreRegistryKey} is active.
	 *
	 * @param key The key to check for
	 * @return <code>true</code> if thekey is associated with an existing and active entry,<code>false</code> otherwise
	 */
	public static synchronized Boolean Contains(String key)
	{
		Boolean entryexists = StateManager.Registry.Contains(key);
		if (!entryexists)
		{
			return false;
		}
		if (!StateManager.Registry.Get(key).IsActive())
		{
			return false;
		}
		return true;
	}

	/**
	 * If the provided key is associated in the State Store as the {@link StateManager#Contains(java.lang.String)}
	 * dictates, this method returns the type of data the entry stores
	 *
	 * @param key The key to check if it is associtated with an entry
	 * @return the type of entry as returned by {@link StateStoreRegistryKey#GetTypeOfEntry()}
	 * @throws java.lang.Exception The key was not found
	 */
	public static synchronized StateStoreRegistryKey.EntryType ContainsType(String key) throws Exception
	{
		if (StateManager.Contains(key))
		{
			return StateManager.Registry.Get(key).GetTypeOfEntry();
		}
		throw new Exception("Key " + key + " not found");
	}

	private static Object Get(String key) throws Exception
	{
		if (!StateManager.Contains(key))
		{
			return null;
		}
		StateStoreRegistryKey entry = StateManager.Registry.Get(key);
		if (entry == null)
		{
			return null;
		}
		Object obj = null;
		StateStoreData ssdata = new StateStoreData(StateManager.Info);
		switch (entry.GetTypeOfEntry())
		{
			case Alphanumeric:
			{
				obj = ssdata.GetAlphanumeric(entry);
				break;
			}
			case Bytearray:
			{
				obj = ssdata.GetByteArray(entry);
				break;
			}
			case File:
			{
				obj = ssdata.GetFile(entry);
				break;
			}
			case ISerializable:
			{
				obj = ssdata.GetISerializable(entry);
				break;
			}
			case Serializable:
			{
				obj = ssdata.GetSerializable(entry);
				break;
			}
		}
		return obj;
	}

	/**
	 * Retrieves the payload associated with the provided key as an alphanumeric string
	 *
	 * @param Key The key whose value to retrieve
	 * @return The payload
	 * @throws java.lang.Exception The retrieval could not be accomplished
	 */
	public static synchronized String GetAlphanumeric(String Key) throws Exception
	{
		Object obj = StateManager.Get(Key);
		if (obj == null)
		{
			return null;
		}
		if (obj instanceof String)
		{
			return (String) obj;
		} else
		{
			throw new Exception("Retrieved entry is not of expected type");
		}
	}

	/**
	 * Retrieves the payload associated with the provided key as a file whose content is teh stored data
	 *
	 * @param Key The key whose value to retrieve
	 * @return The File
	 * @throws java.lang.Exception The retrieval could not be accomplished
	 */
	public static synchronized File GetFile(String Key) throws Exception
	{
		Object obj = StateManager.Get(Key);
		if (obj == null)
		{
			return null;
		}
		if (obj instanceof File)
		{
			return (File) obj;
		} else
		{
			throw new Exception("Retrieved entry is not of expected type");
		}
	}

	/**
	 * Retrieves the payload associated with the provided key as a byte array
	 *
	 * @param Key The key whose value to retrieve
	 * @return The byte array
	 * @throws java.lang.Exception The retrieval could not be accomplished
	 */
	public static synchronized byte[] GetByteArray(String Key) throws Exception
	{
		Object obj = StateManager.Get(Key);
		if (obj == null)
		{
			return null;
		}
		if (obj instanceof byte[])
		{
			return (byte[]) obj;
		} else
		{
			throw new Exception("Retrieved entry is not of expected type");
		}
	}

	/**
	 * Retrieves the payload associated with the provided key as an object
	 *
	 * @param Key The key whose value to retrieve
	 * @return The object
	 * @throws java.lang.Exception The retrieval could not be accomplished
	 */
	public static synchronized Serializable GetSerializable(String Key) throws Exception
	{
		Object obj = StateManager.Get(Key);
		if (obj == null)
		{
			return null;
		}
		if (obj instanceof Serializable)
		{
			return (Serializable) obj;
		} else
		{
			throw new Exception("Retrieved entry is not of expected type");
		}
	}

	/**
	 * Retrieves the payload associated with the provided key as an object
	 *
	 * @param Key The key whose value to retrieve
	 * @return The object
	 * @throws java.lang.Exception The retrieval could not be accomplished
	 */
	public static synchronized ISerializable GetISerializable(String Key) throws Exception
	{
		Object obj = StateManager.Get(Key);
		if (obj == null)
		{
			return null;
		}
		if (obj instanceof ISerializable)
		{
			return (ISerializable) obj;
		} else
		{
			throw new Exception("Retrieved entry is not of expected type");
		}
	}

	/**
	 * Adds or updates the {@link StateStoreRegistryKey} referenced by the providede key. Uses
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * and {@link StateStoreData#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, java.lang.String)} to
	 * update its internal structures. The value returend is the returned by the
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * call
	 *
	 * @param key The key to associate the provided payload with
	 * @param Alphanumeric The payload to store
	 * @return The return value of {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * @throws java.lang.Exception The addition could not be performed
	 */
	public static synchronized Boolean Put(String key, String Alphanumeric) throws Exception
	{
		if (Alphanumeric == null)
		{
			throw new NullPointerException("Provided object not set");
		}
		StateStoreRegistryKey Key = new StateStoreRegistryKey(key, 0, 0, 0, StateStoreRegistryKey.EntryType.Alphanumeric);
		StateStoreData ssdata = new StateStoreData(StateManager.Info);
		ssdata.Put(Key, Alphanumeric);
		return StateManager.Registry.Put(Key, StateManager.Info);
	}

	/**
	 * Adds or updates the {@link StateStoreRegistryKey} referenced by the providede key. Uses
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * and {@link StateStoreData#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, byte[])} to
	 * update its internal structures. The value returend is the returned by the
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * call
	 *
	 * @param key The key to associate the provided payload with
	 * @param bytearray The payload to store
	 * @return The return value of {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * @throws java.lang.Exception The addition could not be performed
	 */
	public static synchronized Boolean Put(String key, byte[] bytearray) throws Exception
	{
		if (bytearray == null)
		{
			throw new NullPointerException("Provided object not set");
		}
		StateStoreRegistryKey Key = new StateStoreRegistryKey(key, 0, 0, 0, StateStoreRegistryKey.EntryType.Bytearray);
		StateStoreData ssdata = new StateStoreData(StateManager.Info);
		ssdata.Put(Key, bytearray);
		return StateManager.Registry.Put(Key, StateManager.Info);
	}

	/**
	 * Adds or updates the {@link StateStoreRegistryKey} referenced by the providede key. Uses
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * and {@link StateStoreData#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.data.ISerializable)} to
	 * update its internal structures. The value returend is the returned by the
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * call
	 *
	 * @param key The key to associate the provided payload with
	 * @param obj The object to store
	 * @return The return value of {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * @throws java.lang.Exception The addition could not be performed
	 */
	public static synchronized Boolean Put(String key, ISerializable obj) throws Exception
	{
		if (obj == null)
		{
			throw new NullPointerException("Provided object not set");
		}
		StateStoreRegistryKey Key = new StateStoreRegistryKey(key, 0, 0, 0, StateStoreRegistryKey.EntryType.ISerializable);
		StateStoreData ssdata = new StateStoreData(StateManager.Info);
		ssdata.Put(Key, obj);
		return StateManager.Registry.Put(Key, StateManager.Info);
	}

	/**
	 * Adds or updates the {@link StateStoreRegistryKey} referenced by the providede key. Uses
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * and {@link StateStoreData#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, java.io.Serializable)} to
	 * update its internal structures. The value returend is the returned by the
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * call
	 *
	 * @param key The key to associate the provided payload with
	 * @param obj The object to store
	 * @return The return value of {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * @throws java.lang.Exception The addition could not be performed
	 */
	public static synchronized Boolean Put(String key, Serializable obj) throws Exception
	{
		if (obj == null)
		{
			throw new NullPointerException("Provided object not set");
		}
		StateStoreRegistryKey Key = new StateStoreRegistryKey(key, 0, 0, 0, StateStoreRegistryKey.EntryType.Serializable);
		StateStoreData ssdata = new StateStoreData(StateManager.Info);
		ssdata.Put(Key, obj);
		return StateManager.Registry.Put(Key, StateManager.Info);
	}

	/**
	 * Adds or updates the {@link StateStoreRegistryKey} referenced by the providede key. Uses 
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * and {@link StateStoreData#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, java.io.File)} to
	 * update its internal structures. The value returend is the returned by the
	 * {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * call
	 *
	 * @param key The key to associate the provided payload with
	 * @param file The file whose payload to store
	 * @return The return value of {@link StateStoreRegistry#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, gr.uoa.di.madgik.commons.state.store.StateStoreInfo)}
	 * @throws java.lang.Exception The addition could not be performed
	 */
	public static synchronized Boolean Put(String key, File file) throws Exception
	{
		if (!file.exists() || file.isDirectory())
		{
			throw new FileNotFoundException("File " + file + " not found");
		}
		StateStoreRegistryKey Key = new StateStoreRegistryKey(key, 0, 0, 0, StateStoreRegistryKey.EntryType.File);
		StateStoreData ssdata = new StateStoreData(StateManager.Info);
		ssdata.Put(Key, file);
		return StateManager.Registry.Put(Key, StateManager.Info);
	}

	/**
	 * In case of deletes and updates in the registry and data file, there will be data kept in the files that
	 * are no longer needed but simply remain increrasing the size of the repository files. This method
	 * reorganizes the registry and data files into new temporary files using
	 * {@link StateStoreRegistry#MoveToClean()} and {@link StateStoreData#MoveToClean(java.util.Map)}
	 * methods, updates the {@link StateStoreRegistryKey} entries in the registry file, removes the old
	 * repository files, and renames the new clean ones to the ones dictated by {@link StateStoreInfo}
	 *
	 * @throws java.lang.Exception The cleanup could not be perofrmed
	 */
	public static synchronized void Compact() throws Exception
	{
		File registryFile = null;
		File dataFile = null;
		try
		{
			registryFile = StateManager.Registry.MoveToClean();
			StateStoreData ssdata = new StateStoreData(StateManager.Info);
			dataFile = ssdata.MoveToClean(StateManager.Registry.GetDictionary());
			for (Map.Entry<String, StateStoreRegistryKey> entry : StateManager.Registry.GetDictionary().entrySet())
			{
				StateManager.Registry.StoreUpdate(new StateStoreInfo(registryFile, dataFile), entry.getValue(), true);
			}
			StateManager.Info.GetEntryDataFile().delete();
			StateManager.Info.GetEntryRegistryFile().delete();
			FileUtils.Copy(registryFile, StateManager.Info.GetEntryRegistryFile());
			FileUtils.Copy(dataFile, StateManager.Info.GetEntryDataFile());
			//registryFile.renameTo(StateManager.Info.GetEntryRegistryFile());
			//dataFile.renameTo(StateManager.Info.GetEntryDataFile());
			registryFile.delete();
			dataFile.delete();
		} catch (Exception ex)
		{
			if (registryFile != null)
			{
				registryFile.delete();
			}
			if (dataFile != null)
			{
				dataFile.delete();
			}
			throw ex;
		}
	}
}
