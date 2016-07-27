package gr.uoa.di.madgik.grs.registry;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link LifecycleManager} is a utility class that monitors the items registered with the {@link GRSRegistry}
 * and makes sure that they are properly purged when their lifecycle properties dictates their disposal. The property
 * that is taken into account in this decision is the last activity time of each entry. Whenever its inactivity
 * time has extended over the timeout set for each one, the item is disposed. The timeout that is taken into account
 * for {@link IBuffer}s, is {@link IBuffer#getInactivityTimeout()} and {@link IBuffer#getInactivityTimeUnit()}. For 
 * {@link IBufferStore}s, is {@link IBufferStore#getInactivityTimeout()} and {@link IBufferStore#getInactivityTimeUnit()}.<br/>
 * The check is performed with a dynamically defined period. Initially this period is set to 
 * {@link LifecycleManager#DefaultCheckPeriod}. During each iteration, the minimum of the non to be disposed remaining lifetime
 * is selected for both {@link IBuffer} and {@link IBufferStore} entries. At every subsequent iteration, this period is recomputed
 * to avoid unnecessary checks
 * 
 * @author gpapanikos
 *
 */
public class LifecycleManager extends Thread
{
	private static final Logger logger=Logger.getLogger(LifecycleManager.class.getName());
	/**
	 * The default check period. Currently set to 2 minutes
	 */
	public static final long DefaultCheckPeriod=1000*60*2;
	
	public void run()
	{
		try
		{
			ArrayList<IBuffer> buffersToDispose=new ArrayList<IBuffer>();
			ArrayList<IBufferStore> storesToDispose=new ArrayList<IBufferStore>();
			while(true)
			{
				long recheck=LifecycleManager.DefaultCheckPeriod;
				try
				{
					buffersToDispose.clear();
					for(IBuffer entry : GRSRegistry.Registry.getBufferEntries())
					{
						try
						{
							if(entry==null) continue;
							long current=System.currentTimeMillis();
							long lastActivity=entry.getLastActivityTime();
							long timeout=entry.getInactivityTimeout();
							TimeUnit unit=entry.getInactivityTimeUnit();
							
							long currentInactivity=current-lastActivity;
							long thresholdInactivity=unit.toMillis(timeout);
							
							long newRecheck=thresholdInactivity-currentInactivity;
							if(currentInactivity>=thresholdInactivity) buffersToDispose.add(entry);
							else if(newRecheck < recheck && newRecheck>0) recheck=newRecheck;
						}catch(Exception ex)
						{
							if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Could not check lifecycle properties for buffer",ex);
						}
					}
					
					storesToDispose.clear();
					for(IBufferStore entry : GRSRegistry.Registry.getStoreEntries())
					{
						try
						{
							if(entry==null) continue;
							long current=System.currentTimeMillis();
							long lastActivity=entry.getLastActivityTime();
							long timeout=entry.getInactivityTimeout();
							TimeUnit unit=entry.getInactivityTimeUnit();
							
							long currentInactivity=current-lastActivity;
							long thresholdInactivity=unit.toMillis(timeout);
							
							long newRecheck=thresholdInactivity-currentInactivity;
							if(currentInactivity>=thresholdInactivity) 
							{
//								System.out.println("currentInactivity "+currentInactivity+" thresholdInactivity "+thresholdInactivity);
								storesToDispose.add(entry);
							}
							else if(newRecheck < recheck && newRecheck>0) recheck=newRecheck;
						}catch(Exception ex)
						{
							if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Could not check lifecycle properties for buffer",ex);
						}
					}

					for(IBuffer entry : buffersToDispose)
					{
						try
						{
							if(entry==null) continue;
//							System.out.println("deisposing buffer from lifecycle");
							entry.dispose();
							GRSRegistry.Registry.remove(entry.getKey());
						}catch(Exception ex)
						{
							if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Could not dispose buffer",ex);
						}
					}
					for(IBufferStore entry : storesToDispose)
					{
						try
						{
							if(entry==null) continue;
//							System.out.println("deisposing buffer store from lifecycle");
							entry.dispose();
							GRSRegistry.Registry.remove(entry.getKey());
						}catch(Exception ex)
						{
							if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Could not dispose buffer",ex);
						}
					}
					
					if(logger.isLoggable(Level.FINE))logger.log(Level.FINE,"disposed "+buffersToDispose.size()+" buffers and "+storesToDispose.size()+" stores");

				}catch(Exception ex)
				{
					if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Could not complete lifecycle check cycle",ex);
				}
				try { Thread.sleep(recheck); } catch(Exception ex){}
			}
		}catch(Exception ex)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.SEVERE, "Could not initialize lifecycle manager",ex);
		}
	}
}
