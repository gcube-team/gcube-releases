package gr.uoa.di.madgik.commons.state.store.data;

import gr.uoa.di.madgik.commons.configuration.ConfigurationManager;
import gr.uoa.di.madgik.commons.state.StateManager;
import gr.uoa.di.madgik.commons.state.store.StateStoreInfo;
import gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class responsible of handling the persistency and retrieval of the actual state data clients want to
 * persiste with the State Store Repository. This class dows not keep any internal state and is
 * instanciated every time a new operation needs to be perofrmed on the repository payload data.
 * The operations this class provides are not thread safe and even though it represents a shared
 * resource it does not provide any kind of of resource locking policy. This is left to the wrapping
 * class, {@link StateManager}. This class needs a value kith key <code>EncodingCharset</code> in the
 * {@link ConfigurationManager} representing the charaset name to use during alphanumeric storage and
 * retrieval through {@link StateStoreData#Put(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey, java.lang.String)}
 * and {@link StateStoreData#GetAlphanumeric(gr.uoa.di.madgik.commons.state.store.registry.StateStoreRegistryKey)}
 * 
 * @author gpapanikos
 */
public class StateStoreData
{

	private static final Logger logger = Logger.getLogger(StateStoreData.class.getName());
	private StateStoreInfo Info = null;

	/**
	 * Creates a new instance
	 *
	 * @param Info The repository persistency files information
	 */
	public StateStoreData(StateStoreInfo Info)
	{
		this.Info = Info;
	}

	/**
	 * Appends the provided payload to the repository data file. The provided metadata
	 * {@link StateStoreRegistryKey} entry is updated with the offsets of the stored data
	 * 
	 * @param Key The metadata entry representing the data.
	 * @param Alphanumeric The payload to store as a sequence of bytes using the configuration
	 * value with key <code>EncodingCharset</code> from the {@link ConfigurationManager}
	 * @throws java.lang.Exception the store could not be performed
	 */
	public void Put(StateStoreRegistryKey Key, String Alphanumeric) throws Exception
	{
		RandomAccessFile acc = null;
		try
		{
			acc = new RandomAccessFile(this.Info.GetEntryDataFile(), "rw");
			acc.seek(this.Info.GetEntryDataFile().length());
			long start = acc.getFilePointer();
			acc.write(Alphanumeric.getBytes(ConfigurationManager.GetStringParameter("EncodingCharset")));
			long end = acc.getFilePointer();
			Key.SetStartingDataOffset(start);
			Key.SetEndingDataOffset(end);
			acc.close();
		} catch (Exception ex)
		{
			try
			{
				if (acc != null)
				{
					acc.close();
				}
			} catch (Exception exx)
			{
			}
			throw ex;
		}
	}

	/**
	 * Retrieves the actual payload referenced by the provided metadata entry and returns it
	 * as the expected type.
	 *
	 * @param Key The metadate entry referencing the data to retrieve
	 * @return The payload after decoding it using the value with key <code>EncodingCharset</code>
	 * from the {@link ConfigurationManager} back to a string from a byte array
	 * @throws java.lang.Exception The retrieval could not be performed
	 */
	public String GetAlphanumeric(StateStoreRegistryKey Key) throws Exception
	{
		DataInputStream din = null;
		try
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Retrieving " + Key.GetKey() + " from " + Key.GetStartingDataOffset() + "-" + Key.GetEndingDataOffset());
			din = new DataInputStream(new BufferedInputStream(new FileInputStream(this.Info.GetEntryDataFile())));
			din.skip(Key.GetStartingDataOffset());
			int size = (int) (Key.GetEndingDataOffset() - Key.GetStartingDataOffset());
			byte[] buf = new byte[size];
			din.readFully(buf);
			String ret = new String(buf, ConfigurationManager.GetStringParameter("EncodingCharset"));
			din.close();
			return ret;
		} catch (Exception ex)
		{
			if (din != null)
			{
				din.close();
			}
			throw ex;
		}
	}

	/**
	 * Appends the provided payload to the repository data file. The provided metadata
	 * {@link StateStoreRegistryKey} entry is updated with the offsets of the stored data
	 *
	 * @param Key The metadata entry representing the data.
	 * @param bytearray The payload to store as a sequence of bytes
	 * @throws java.lang.Exception The store could not be performed
	 */
	public void Put(StateStoreRegistryKey Key, byte[] bytearray) throws Exception
	{
		RandomAccessFile acc = null;
		try
		{
			acc = new RandomAccessFile(this.Info.GetEntryDataFile(), "rw");
			acc.seek(this.Info.GetEntryDataFile().length());
			long start = acc.getFilePointer();
			acc.write(bytearray);
			long end = acc.getFilePointer();
			Key.SetStartingDataOffset(start);
			Key.SetEndingDataOffset(end);
			acc.close();
		} catch (Exception ex)
		{
			try
			{
				if (acc != null)
				{
					acc.close();
				}
			} catch (Exception exx)
			{
			}
			throw ex;
		}
	}

	/**
	 * Retrieves the actual payload referenced by the provided metadata entry and returns it
	 * as the expected type.
	 *
	 * @param Key The metadate entry referencing the data to retrieve
	 * @return The payload
	 * @throws java.lang.Exception The retrieval could not be performed
	 */
	public byte[] GetByteArray(StateStoreRegistryKey Key) throws Exception
	{
		DataInputStream din = null;
		try
		{
			din = new DataInputStream(new BufferedInputStream(new FileInputStream(this.Info.GetEntryDataFile())));
			din.skip(Key.GetStartingDataOffset());
			int size = (int) (Key.GetEndingDataOffset() - Key.GetStartingDataOffset());
			byte[] buf = new byte[size];
			din.readFully(buf);
			din.close();
			return buf;
		} catch (Exception ex)
		{
			if (din != null)
			{
				din.close();
			}
			throw ex;
		}
	}

	/**
	 * Appends the provided payload to the repository data file. The provided metadata
	 * {@link StateStoreRegistryKey} entry is updated with the offsets of the stored data
	 *
	 * @param Key The metadata entry representing the data.
	 * @param obj The object to store serializing it through {@link ISerializable#Serialize()}
	 * @throws java.lang.Exception The store could not be performed
	 */
	public void Put(StateStoreRegistryKey Key, ISerializable obj) throws Exception
	{
		RandomAccessFile acc = null;
		try
		{
			acc = new RandomAccessFile(this.Info.GetEntryDataFile(), "rw");
			acc.seek(this.Info.GetEntryDataFile().length());
			long start = acc.getFilePointer();
			byte[] classname = obj.getClass().getName().getBytes(ConfigurationManager.GetStringParameter("EncodingCharset"));
			acc.writeInt(classname.length);
			acc.write(classname);
			acc.write(obj.Serialize());
			long end = acc.getFilePointer();
			Key.SetStartingDataOffset(start);
			Key.SetEndingDataOffset(end);
			acc.close();
		} catch (Exception ex)
		{
			try
			{
				if (acc != null)
				{
					acc.close();
				}
			} catch (Exception exx)
			{
			}
			throw ex;
		}
	}

	/**
	 * Retrieves the actual payload referenced by the provided metadata entry and returns it
	 * as the expected type.
	 *
	 * @param Key The metadate entry referencing the data to retrieve
	 * @return The object instantiated with its default constructor and populated through the {@link ISerializable#Deserialize(byte[])}
	 * @throws java.lang.Exception The retrieval could not be performed
	 */
	public ISerializable GetISerializable(StateStoreRegistryKey Key) throws Exception
	{
		DataInputStream din = null;
		try
		{
			din = new DataInputStream(new BufferedInputStream(new FileInputStream(this.Info.GetEntryDataFile())));
			din.skip(Key.GetStartingDataOffset());
			int fullentrysize = (int) (Key.GetEndingDataOffset() - Key.GetStartingDataOffset());
			byte[] classname = new byte[din.readInt()];
			din.readFully(classname);
			String objname = new String(classname, ConfigurationManager.GetStringParameter("EncodingCharset"));
			Class<?> ModuleClass = Class.forName(objname);
			Object Instance = null;
			Instance = ModuleClass.newInstance();
			
			if (!(Instance instanceof ISerializable))
			{
				throw new Exception("Found instance does not implement ISerializable interface");
			}
			byte[] buf = new byte[fullentrysize - Integer.SIZE/8 - classname.length];
			din.readFully(buf);
			din.close();
			((ISerializable) Instance).Deserialize(buf);
			return (ISerializable) Instance;
		} catch (Exception ex)
		{
			if (din != null)
			{
				din.close();
			}
			throw ex;
		}
	}

	/**
	 * Appends the provided payload to the repository data file. The provided metadata
	 * {@link StateStoreRegistryKey} entry is updated with the offsets of the stored data
	 *
	 * @param Key The metadata entry representing the data.
	 * @param obj The object to store serializing it through an {@link ObjectOutputStream}
	 * @throws java.lang.Exception The store could not be performed
	 */
	public void Put(StateStoreRegistryKey Key, Serializable obj) throws Exception
	{
		RandomAccessFile acc = null;
		try
		{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(bout);
			oout.writeObject(obj);
			oout.flush();
			bout.flush();
			oout.close();
			bout.close();
			acc = new RandomAccessFile(this.Info.GetEntryDataFile(), "rw");
			acc.seek(this.Info.GetEntryDataFile().length());
			long start = acc.getFilePointer();
			acc.write(bout.toByteArray());
			long end = acc.getFilePointer();
			Key.SetStartingDataOffset(start);
			Key.SetEndingDataOffset(end);
			acc.close();
		} catch (Exception ex)
		{
			try
			{
				if (acc != null)
				{
					acc.close();
				}
			} catch (Exception exx)
			{
			}
			throw ex;
		}
	}

	/**
	 * Retrieves the actual payload referenced by the provided metadata entry and returns it
	 * as the expected type.
	 *
	 * @param Key The metadate entry referencing the data to retrieve
	 * @return The object retrieved using an {@link ObjectInputStream}
	 * @throws java.lang.Exception The retrieval could not be performed
	 */
	public Serializable GetSerializable(StateStoreRegistryKey Key) throws Exception
	{
		DataInputStream din = null;
		try
		{
			din = new DataInputStream(new BufferedInputStream(new FileInputStream(this.Info.GetEntryDataFile())));
			din.skip(Key.GetStartingDataOffset());
			int fullentrysize = (int) (Key.GetEndingDataOffset() - Key.GetStartingDataOffset());
			byte[] buf = new byte[fullentrysize];
			din.readFully(buf);
			din.close();
			ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(buf));
			Object Instance = oin.readObject();
			if (!(Instance instanceof Serializable))
			{
				throw new Exception("Found instance does not implement Serializable interface");
			}
			return (Serializable) Instance;
		} catch (Exception ex)
		{
			if (din != null)
			{
				din.close();
			}
			throw ex;
		}
	}

	/**
	 * Appends the provided payload to the repository data file. The provided metadata
	 * {@link StateStoreRegistryKey} entry is updated with the offsets of the stored data
	 *
	 * @param Key The metadata entry representing the data.
	 * @param file The file whose payload to retrieve and store
	 * @throws java.lang.Exception The store could not be performed
	 */
	public void Put(StateStoreRegistryKey Key, File file) throws Exception
	{
		RandomAccessFile acc = null;
		BufferedInputStream bin = null;
		try
		{
			bin = new BufferedInputStream(new FileInputStream(file));
			acc = new RandomAccessFile(this.Info.GetEntryDataFile(), "rw");
			acc.seek(this.Info.GetEntryDataFile().length());
			long start = acc.getFilePointer();
			byte[] buf = new byte[4 * 1024];
			while (true)
			{
				int n = bin.read(buf);
				if (n < 0)
				{
					break;
				}
				acc.write(buf, 0, n);
			}
			long end = acc.getFilePointer();
			Key.SetStartingDataOffset(start);
			Key.SetEndingDataOffset(end);
			acc.close();
		} catch (Exception ex)
		{
			try
			{
				if (acc != null)
				{
					acc.close();
				}
			} catch (Exception exx)
			{
			}
			try
			{
				if (bin != null)
				{
					bin.close();
				}
			} catch (Exception exx)
			{
			}
			throw ex;
		}
	}

	/**
	 * Retrieves the actual payload referenced by the provided metadata entry and returns it
	 * as the expected type.
	 *
	 * @param Key The metadate entry referencing the data to retrieve
	 * @return The temporary file created where the payload is stored as it is retrieved by the repository data file
	 * @throws java.lang.Exception The retrieval could not be performed
	 */
	public File GetFile(StateStoreRegistryKey Key) throws Exception
	{
		DataInputStream din = null;
		File tmp = null;
		BufferedOutputStream bout = null;
		try
		{
			tmp = File.createTempFile(UUID.randomUUID().toString(), ".state.entry.data.tmp");
			bout = new BufferedOutputStream(new FileOutputStream(tmp));
			din = new DataInputStream(new BufferedInputStream(new FileInputStream(this.Info.GetEntryDataFile())));
			din.skip(Key.GetStartingDataOffset());
			long fullentrysize = (Key.GetEndingDataOffset() - Key.GetStartingDataOffset());
			byte[] buf = new byte[4 * 1024];
			long count = 0;
			while (true)
			{
				if (count >= fullentrysize)
				{
					break;
				}
				int remaining = buf.length;
				if (count + remaining > fullentrysize)
				{
					remaining = (int) (fullentrysize - count);
				}
				int n = din.read(buf, 0, remaining);
				if (n < 0)
				{
					break;
				}
				bout.write(buf, 0, n);
			}
			bout.flush();
			bout.close();
			din.close();
			return tmp;
		} catch (Exception ex)
		{
			if (din != null)
			{
				din.close();
			}
			if (tmp != null)
			{
				tmp.delete();
			}
			if (bout != null)
			{
				bout.close();
			}
			throw ex;
		}
	}

	private void Move(Map.Entry<String, StateStoreRegistryKey> entry, RandomAccessFile din, RandomAccessFile acc) throws Exception
	{
		long startoffset = acc.getFilePointer();
		long length = entry.getValue().GetEndingDataOffset() - entry.getValue().GetStartingDataOffset();
		long count = 0;
		din.seek(entry.getValue().GetStartingDataOffset());
		byte[] buf = new byte[1024 * 4];
		while (true)
		{
			if (count >= length)
			{
				break;
			}
			int remaining = buf.length;
			if ((length - count) < remaining)
			{
				remaining = (int) (length - count);
			}
			int n = din.read(buf, 0, remaining);
			if (n < 0)
			{
				break;
			}
			count += n;
			acc.write(buf, 0, n);
		}
		long endoffset = acc.getFilePointer();
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Moved entry " + entry.getKey() + " from " + entry.getValue().GetStartingDataOffset() + "-" + entry.getValue().GetEndingDataOffset() + " to " + startoffset + "-" + endoffset);
		entry.getValue().SetStartingDataOffset(startoffset);
		entry.getValue().SetEndingDataOffset(endoffset);
	}

	/**
	 * In case of deletes and updates in the registry and data file, there will be data kept in the file that
	 * are no longer needed but simply remain increrasing the size of the repository files. This method
	 * reorganizes the data file into a new temporary file it creates that holds only the needed valid
	 * data records. During the process each entry that is valid as provided by the caller it is persisted in
	 * the new clean file. The metadata entries provided are also updated to point to the new offsets where their
	 * respective data are stored. This leaves the registry part of the repository in an inconsistend state until 
	 * the file that the {@link StateStoreInfo} points to as the data repository file is deleted and replaced with
	 * the file that this method returns
	 *
	 * @param registry the in memeory structure holding the entries that should be kept from the data file
	 * @return The file created holding the new clean file to be used as the repository data file
	 * @throws java.lang.Exception The clean operation could not be performed
	 */
	public File MoveToClean(Map<String, StateStoreRegistryKey> registry) throws Exception
	{
		File tmp = null;
		RandomAccessFile acc = null;
		RandomAccessFile din = null;
		try
		{
			tmp = File.createTempFile(UUID.randomUUID().toString(), ".madgik.state.data.tmp");
			acc = new RandomAccessFile(tmp, "rw");
			if(!this.Info.GetEntryDataFile().exists()) { acc.close(); return tmp; }//if data file does not exist, e.g. upon the very first run
			din = new RandomAccessFile(this.Info.GetEntryDataFile(), "rw");
			for (Map.Entry<String, StateStoreRegistryKey> entry : registry.entrySet())
			{
				this.Move(entry, din, acc);
			}
			din.close();
			acc.close();
			return tmp;
		} catch (Exception ex)
		{
			try
			{
				if (din != null)
				{
					din.close();
				}
			} catch (Exception exx)
			{
			}
			try
			{
				if (acc != null)
				{
					acc.close();
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
}
