package gr.uoa.di.madgik.environment.gcube;

import gr.uoa.di.madgik.environment.exception.EnvironmentStorageSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.ss.IStorageSystemProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**TODO: Update, locking for Index support*/
public class GCubeStorageSystemProvider implements IStorageSystemProvider
{
	private static Logger logger=LoggerFactory.getLogger(GCubeStorageSystemProvider.class);
	
	private static Random randGen=new Random();
	
	public static final String GCubeActionScopeHintName="GCubeActionScope";
	public static final String DeleteOnExitHintName="StorageSystemDeleteOnExit";
	public static final String LocalFileSystemBufferPathHintName="StorageSystemLocalFileSystemBufferPath";
	public static final String RetryOnErrorCountHintName="RetryOnErrorCount";
	public static final String RetryOnErrorIntervalHintName="RetryOnErrorInterval";
	public static final String OwnerHintName="Owner";
	public static final String DirectoryPathHintName="DirectoryPath";
	public static final String DirectoryFileNameHintName="DirectoryFileName";
	public static final String UseTTLHintName="UseTTL";
	public static final String StorageSystemRIContainerServiceClassHintName="StorageSystemRIContainerServiceClass";
	public static final String StorageSystemRIContainerServiceNameHintName="StorageSystemRIContainerServiceName";
	
	private static final String DefaultLocalFileSystemBufferPath="/tmp/";
	private static final String DefaultCollectionPrefix="ExecutionEngineStagingBuffer";
	private static final String DefaultOwnerName="GCubeStorageSystemProvider";
	private static final String DefaultDocumentMimeType="application/octet-stream";
	private static final String DefaultStorageSystemRIContainerServiceClass="Execution";
	private static final String DefaultStorageSystemRIContainerServiceName="ExecutionEngineService";
	
	private static final int DefaultRetryOnError=0;
	//private static final int DefaultInvocationTimeout=1000*60*10;
	
	//private static Map<String,ReaderWriterPair> CollectionAccessorsPerScope=new HashMap<String, ReaderWriterPair>();
	private static final Object lockMe=new Object();
	
	public void SessionInit(EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
//		try
//		{
//			
//		}catch(Exception ex)
//		{
//			throw new EnvironmentStorageSystemException("Could not initialize session", ex);
//		}
	}
	
	public File Retrieve(String ID, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		InputStream is = null;
		BufferedOutputStream bout = null;
		try
		{
			String Scope=GCubeStorageSystemProvider.GetActionScope(Hints);
			if(Scope==null) throw new EnvironmentStorageSystemException("No scope provided");
			ScopeProvider.instance.set(Scope);
			
			String newID=UUID.randomUUID().toString();
			File newtmp=GCubeStorageSystemProvider.GetLocalFileSystemBufferFile(newID, Hints);
			if(GCubeStorageSystemProvider.ShouldDeleteOnExit(Hints)) newtmp.deleteOnExit();
			
			IClient client = new StorageClient(GetRIContainerServiceClass(Hints), GetRIContainerServiceName(Hints), GetOwnerName(Hints), 
					AccessType.SHARED).getClient();
			
			client.get().LFile(newtmp.getAbsolutePath()).RFileById(ID);
			
			return newtmp;
			
		}catch(Exception ex)
		{
			throw new EnvironmentStorageSystemException("Could not retrieve the document requested", ex);
		}finally 
		{
			try 
			{
				if(is != null) is.close();
				if(bout != null)
				{
					bout.flush();
					bout.close();
				}
			}catch(Exception e) { }
		}
	}
	
	public void Delete(String ID, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		try
		{
			String Scope=GCubeStorageSystemProvider.GetActionScope(Hints);
			if(Scope==null) throw new EnvironmentStorageSystemException("No scope provided");
			ScopeProvider.instance.set(Scope);
			
			IClient client = new StorageClient(GetRIContainerServiceClass(Hints), GetRIContainerServiceName(Hints), GetOwnerName(Hints), 
					AccessType.SHARED).getClient();
			
			client.remove().RFileById(ID);
			
		}catch(Exception ex)
		{
			throw new EnvironmentStorageSystemException("Could not delete the document requested", ex);
		}
	}
	
	public String Store(File file, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		return this.Store(file, false, Hints);
	}
	
	public String Store(File file, boolean volatileStore, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		MemoryType memType;
		if (volatileStore)
			memType = MemoryType.VOLATILE;
		else
			memType = MemoryType.PERSISTENT;
		
		try
		{
			String Scope=GCubeStorageSystemProvider.GetActionScope(Hints);
			if(Scope==null) throw new EnvironmentStorageSystemException("No scope provided");
			ScopeProvider.instance.set(Scope);
			
			IClient client = new StorageClient(GetRIContainerServiceClass(Hints), GetRIContainerServiceName(Hints), GetOwnerName(Hints), 
					AccessType.SHARED, memType).getClient();
			
			String remotePath = getRemotePath(Scope)+"/"+file.getName()+"."+UUID.randomUUID().toString();
			String id = client.put(false).LFile(file.getAbsolutePath()).RFile(remotePath);
			//System.out.println("URL of stored file: "+client.getUrl().RFile(remotePath)); //TODO URL by id does not work
			return id;
			
		}catch(Exception ex)
		{
			throw new EnvironmentStorageSystemException("Could not store the document requested", ex);
		}
	}
	
	public String Store(URL location, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		return this.Store(location, false, Hints);
	}
	
	public String Store(URL location, boolean volatileStore, EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		MemoryType memType;
		if (volatileStore)
			memType = MemoryType.VOLATILE;
		else
			memType = MemoryType.PERSISTENT;
		try
		{
			String Scope=GCubeStorageSystemProvider.GetActionScope(Hints);
			if(Scope==null) throw new EnvironmentStorageSystemException("No scope provided");
			ScopeProvider.instance.set(Scope);
			
			IClient client = new StorageClient(GetRIContainerServiceClass(Hints), GetRIContainerServiceName(Hints), GetOwnerName(Hints), 
					AccessType.SHARED, memType).getClient();
			String remotePath = getRemotePath(Scope)+"/"+UUID.randomUUID().toString();
			String id = client.put(false).LFile(location.openConnection().getInputStream()).RFile(remotePath);
		//	System.out.println("URL of stored file: "+client.getUrl().RFileById(id));
			return id;
		}catch(Exception ex)
		{
			throw new EnvironmentStorageSystemException("Could not store the document requested", ex);
		}
	}

	public File GetLocalFSBufferLocation(EnvHintCollection Hints) throws EnvironmentStorageSystemException
	{
		if(Hints==null || !Hints.HintExists(GCubeStorageSystemProvider.LocalFileSystemBufferPathHintName))
			return new File(GCubeStorageSystemProvider.DefaultLocalFileSystemBufferPath);
		String loc=Hints.GetHint(GCubeStorageSystemProvider.LocalFileSystemBufferPathHintName).Hint.Payload;
		if(loc==null || loc.trim().length()==0) return new File(GCubeStorageSystemProvider.DefaultLocalFileSystemBufferPath);
		return new File(loc);
	}
	
	private static int GetSleepBetweenInterval(EnvHintCollection Hints)
	{
		if(Hints==null || !Hints.HintExists(GCubeStorageSystemProvider.RetryOnErrorIntervalHintName))
			return 0;
		try
		{
			return randGen.nextInt(Integer.parseInt(Hints.GetHint(GCubeStorageSystemProvider.RetryOnErrorIntervalHintName).Hint.Payload));
		}catch(Exception ex)
		{
			logger.warn("provided hint not valid. returning default value");
			return 0;
		}
		
	}
	
	private static int GetNumberOfTries(EnvHintCollection Hints)
	{
		if(Hints==null || !Hints.HintExists(GCubeStorageSystemProvider.RetryOnErrorCountHintName))
			return GCubeStorageSystemProvider.DefaultRetryOnError+1;
		try
		{
			return Integer.parseInt(Hints.GetHint(GCubeStorageSystemProvider.RetryOnErrorCountHintName).Hint.Payload)+1;
		}catch(Exception ex)
		{
			logger.warn("provided hint not valid. returning default value");
			return GCubeStorageSystemProvider.DefaultRetryOnError+1;
		}
	}
	
	private static File GetLocalFileSystemBufferFile(String ID, EnvHintCollection Hints)
	{
		if(Hints==null || !Hints.HintExists(GCubeStorageSystemProvider.LocalFileSystemBufferPathHintName))
			return new File(GCubeStorageSystemProvider.DefaultLocalFileSystemBufferPath,ID+".ss.tmp");
		String loc=Hints.GetHint(GCubeStorageSystemProvider.LocalFileSystemBufferPathHintName).Hint.Payload;
		if(loc==null || loc.trim().length()==0) return new File(GCubeStorageSystemProvider.DefaultLocalFileSystemBufferPath,ID+".ss.tmp");
		return new File(loc,ID+".ss.tmp");
	}

	private static boolean ShouldDeleteOnExit(EnvHintCollection Hints)
	{
		if(Hints==null) return true;
		if(!Hints.HintExists(GCubeStorageSystemProvider.DeleteOnExitHintName)) return true;
		return Boolean.parseBoolean(Hints.GetHint(GCubeStorageSystemProvider.DeleteOnExitHintName).Hint.Payload);
	}
	
	private static String GetActionScope(EnvHintCollection Hints)
	{
		if(Hints==null) return null;
		if(!Hints.HintExists(GCubeStorageSystemProvider.GCubeActionScopeHintName)) return null;
		return Hints.GetHint(GCubeStorageSystemProvider.GCubeActionScopeHintName).Hint.Payload;
	}
	
	private static String GetRIContainerServiceClass(EnvHintCollection Hints)
	{
		if(Hints==null) return GCubeStorageSystemProvider.DefaultStorageSystemRIContainerServiceClass;
		if(!Hints.HintExists(GCubeStorageSystemProvider.StorageSystemRIContainerServiceClassHintName)) return GCubeStorageSystemProvider.DefaultStorageSystemRIContainerServiceClass;
		return Hints.GetHint(GCubeStorageSystemProvider.StorageSystemRIContainerServiceClassHintName).Hint.Payload;
	}
	
	private static String GetRIContainerServiceName(EnvHintCollection Hints)
	{
		if(Hints==null) return GCubeStorageSystemProvider.DefaultStorageSystemRIContainerServiceName;
		if(!Hints.HintExists(GCubeStorageSystemProvider.StorageSystemRIContainerServiceNameHintName)) return GCubeStorageSystemProvider.DefaultStorageSystemRIContainerServiceName;
		return Hints.GetHint(GCubeStorageSystemProvider.StorageSystemRIContainerServiceNameHintName).Hint.Payload;
	}
	
	private static String GetOwnerName(EnvHintCollection Hints)
	{
		if(Hints==null) return GCubeStorageSystemProvider.DefaultOwnerName;
		if(!Hints.HintExists(GCubeStorageSystemProvider.OwnerHintName)) return GCubeStorageSystemProvider.DefaultOwnerName;
		return Hints.GetHint(GCubeStorageSystemProvider.OwnerHintName).Hint.Payload;
	}

	private static String getRemotePath(String Scope)
	{
		String remotePath =  GCubeStorageSystemProvider.DefaultCollectionPrefix;
		if(!remotePath.startsWith("/")) remotePath = "/" + remotePath;
		return remotePath;
	}
}
