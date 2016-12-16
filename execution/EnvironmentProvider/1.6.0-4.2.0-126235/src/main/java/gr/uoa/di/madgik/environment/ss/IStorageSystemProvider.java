package gr.uoa.di.madgik.environment.ss;

import gr.uoa.di.madgik.environment.IEnvironmentProvider;
import gr.uoa.di.madgik.environment.exception.EnvironmentStorageSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import java.io.File;
import java.net.URL;

public interface IStorageSystemProvider extends IEnvironmentProvider
{
	public void SessionInit(EnvHintCollection Hints) throws EnvironmentStorageSystemException;
	
	public String Store(File file, EnvHintCollection Hints) throws EnvironmentStorageSystemException;
	
	public String Store(URL Location, EnvHintCollection Hints) throws EnvironmentStorageSystemException;
	
	public File Retrieve(String ID, EnvHintCollection Hints) throws EnvironmentStorageSystemException;
	
	public void Delete(String ID, EnvHintCollection Hints) throws EnvironmentStorageSystemException;
	
	public File GetLocalFSBufferLocation(EnvHintCollection Hints) throws EnvironmentStorageSystemException;
}
