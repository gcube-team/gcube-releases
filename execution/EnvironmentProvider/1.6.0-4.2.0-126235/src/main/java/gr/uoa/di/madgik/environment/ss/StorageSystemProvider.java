package gr.uoa.di.madgik.environment.ss;

import java.io.File;
import java.net.URL;
import gr.uoa.di.madgik.environment.exception.EnvironmentStorageSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

public class StorageSystemProvider implements IStorageSystemProvider 
{
	private static IStorageSystemProvider StaticProvider=null;
	private static final Object lockMe=new Object();
	
	private IStorageSystemProvider Provider=null;
	private EnvHintCollection InitHints=null;
	
	public static IStorageSystemProvider Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException
	{
		try
		{
			synchronized(StorageSystemProvider.lockMe)
			{
				if(StorageSystemProvider.StaticProvider==null)
				{
					if(ProviderName.equals(StorageSystemProvider.class.getName())) throw new EnvironmentValidationException("Class "+StorageSystemProvider.class.getName()+" cannot be defined as provider");
					Class<?> c=Class.forName(ProviderName);
					Object o=c.newInstance();
					if(!(o instanceof IStorageSystemProvider)) throw new EnvironmentValidationException("");
					StorageSystemProvider prov=new StorageSystemProvider();
					prov.Provider=(IStorageSystemProvider)o;
					prov.InitHints=Hints;
					prov.Provider.SessionInit(Hints);
					StorageSystemProvider.StaticProvider=prov;
				}
			}
			return StorageSystemProvider.StaticProvider;
		}catch(Exception ex)
		{
			throw new EnvironmentValidationException("Could not initialize Storage System Provider", ex);
		}
	}
	
	public static boolean IsInit()
	{
		synchronized(StorageSystemProvider.lockMe)
		{
			return (StorageSystemProvider.StaticProvider!=null);
		}		
	}

	public void SessionInit(EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(this.Provider==null) throw new EnvironmentStorageSystemException("Provider not initialized");
		this.Provider.SessionInit(this.MergeHints(Hints));
	}

	public File Retrieve(String ID, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(this.Provider==null) throw new EnvironmentStorageSystemException("Provider not initialized");
		return this.Provider.Retrieve(ID, this.MergeHints(Hints));
	}

	public void Delete(String ID, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(this.Provider==null) throw new EnvironmentStorageSystemException("Provider not initialized");
		this.Provider.Delete(ID, this.MergeHints(Hints));
	}

	public String Store(File file, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(this.Provider==null) throw new EnvironmentStorageSystemException("Provider not initialized");
		return this.Provider.Store(file, this.MergeHints(Hints));
	}

	public String Store(URL location, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(this.Provider==null) throw new EnvironmentStorageSystemException("Provider not initialized");
		return this.Provider.Store(location, this.MergeHints(Hints));
	}

	public File GetLocalFSBufferLocation(EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(this.Provider==null) throw new EnvironmentStorageSystemException("Provider not initialized");
		return this.Provider.GetLocalFSBufferLocation(this.MergeHints(Hints));
	}
	
	private EnvHintCollection MergeHints(EnvHintCollection Hints)
	{
		if(this.InitHints==null && Hints==null) return new EnvHintCollection();
		if(this.InitHints==null) return Hints;
		else if(Hints==null) return this.InitHints;
		else return this.InitHints.Merge(Hints);
	}
}
