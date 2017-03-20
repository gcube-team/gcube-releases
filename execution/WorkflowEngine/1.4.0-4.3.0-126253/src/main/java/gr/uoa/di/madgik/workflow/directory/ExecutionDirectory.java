package gr.uoa.di.madgik.workflow.directory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionDirectory extends Thread
{
	private static Logger logger=LoggerFactory.getLogger(ExecutionDirectory.class);
	private static ExecutionDirectory CleanUpInstance=null;
	private static final Object lockMe=new Object();
	private static final long DefaultCleanupPeriod=1000*60*60*6;
	private static long CleanUpPeriod=ExecutionDirectory.DefaultCleanupPeriod;
	
	static
	{
		if(ExecutionDirectory.CleanUpInstance==null)
		{
			ExecutionDirectory.CleanUpInstance=new ExecutionDirectory();
		}
	}
	
	public enum DirectoryEntryType
	{
		JDL,
		Grid,
		Condor,
		Hadoop,
		Generic
	}
	
	private static final Map<String,ExecutionObserver> Directory=new HashMap<String, ExecutionObserver>();
	
	public static void SetCleanupPeriod(long Period)
	{
		if(Period<=0) ExecutionDirectory.CleanUpPeriod=ExecutionDirectory.DefaultCleanupPeriod;
		else ExecutionDirectory.CleanUpPeriod=Period;
	}
	
	public static String ReserveKey()
	{
		return UUID.randomUUID().toString();
	}
	
	public static boolean Register(ExecutionObserver observer)
	{
		if(observer.GetExecutionID()==null || observer.GetExecutionID().trim().length()==0) return false;
		synchronized(ExecutionDirectory.lockMe)
		{
			ExecutionDirectory.Directory.put(observer.GetExecutionID(), observer);
		}
		return true;
	}
	
	public static ExecutionObserver Retrieve(String ExecutionID)
	{
		synchronized(ExecutionDirectory.lockMe)
		{
			return ExecutionDirectory.Directory.get(ExecutionID);
		}
	}
	
	public ExecutionDirectory()
	{
		this.setName(ExecutionDirectory.class.getName());
		this.setDaemon(true);
		this.start();
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				List<String> ids=new ArrayList<String>();
				synchronized(ExecutionDirectory.lockMe)
				{
					for(Map.Entry<String,ExecutionObserver> entry : ExecutionDirectory.Directory.entrySet())
					{
						if(entry.getValue().IsCompleted()) ids.add(entry.getKey());
					}
				}
				List<String> cleanup=new ArrayList<String>();
				for(String k : ids)
				{
					ExecutionObserver obs=ExecutionDirectory.Retrieve(k);
					if(obs==null) continue;
					if(obs.ShouldCleanup()) cleanup.add(k);
					if(!obs.HasPerformedStorageSystermCleanup()) obs.PerformedStorageSystermCleanup();
				}
				for(String k : cleanup)
				{
					ExecutionDirectory.Cleanup(k);
				}
				ids.clear();
				cleanup.clear();
				try{Thread.sleep(ExecutionDirectory.CleanUpPeriod);}catch(Exception ex){}
			}
			catch(Exception ex)
			{
				logger.warn("Could not complete Execution directory cleanup check iteration",ex);
			}
		}
	}
	
	private static void Cleanup(String ExecutionID)
	{
		if(ExecutionDirectory.Directory.containsKey(ExecutionID))
		{
			try
			{
				ExecutionDirectory.Retrieve(ExecutionID).Cleanup();
			}catch(Exception ex)
			{
				logger.warn("Could not cleanup Execution observer. Proceeding to remove",ex);
			}
			synchronized(ExecutionDirectory.lockMe)
			{
				ExecutionDirectory.Directory.remove(ExecutionID);
			}
		}
	}
}
