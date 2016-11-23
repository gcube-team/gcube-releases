package gr.uoa.di.madgik.ss;

import gr.uoa.di.madgik.environment.exception.EnvironmentStorageSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.ss.IStorageSystemProvider;
import gr.uoa.di.madgik.environment.ss.StorageSystemProvider;
import java.io.File;
import java.net.URL;

public class StorageSystem
{
	private static IStorageSystemProvider Provider=null;
	private static final Object lockMe=new Object();
	
	public static void Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException
	{
		synchronized (StorageSystem.lockMe)
		{
			if(StorageSystem.Provider==null) StorageSystem.Provider = StorageSystemProvider.Init(ProviderName, Hints);
		}
	}
	
	public static String Store(Object file,EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(file instanceof String) return StorageSystem.Store(new File((String)file),Hints);
		else if(file instanceof File) return StorageSystem.Store((File)file,Hints);
		else throw new IllegalArgumentException("Could not use provided argument "+file);
	}
	
	public static String Store(File file,EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		return StorageSystem.Provider.Store(file, Hints);
	}
	
	public static String Store(URL location,EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		return StorageSystem.Provider.Store(location, Hints);
	}
	
	public static File Retrieve(Object ID,EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(ID instanceof String) return StorageSystem.Retrieve((String)ID,Hints);
		else throw new IllegalArgumentException("Could not use provided argument");
	}
	
	public static File Retrieve(String ID,EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		return StorageSystem.Provider.Retrieve(ID, Hints);
	}
	
	public static void Delete(String ID,EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		 StorageSystem.Provider.Delete(ID, Hints);
	}
	
	public static File GetLocalFSBufferLocation(EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		return StorageSystem.Provider.GetLocalFSBufferLocation(Hints);
	}
}
