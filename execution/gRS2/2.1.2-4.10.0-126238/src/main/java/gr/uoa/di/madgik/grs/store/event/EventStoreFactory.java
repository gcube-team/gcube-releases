package gr.uoa.di.madgik.grs.store.event;

import java.io.IOException;

/**
 * This utility class is used to initialize an instance of an {@link IEventStore} according
 * to system configuration. Since the only available implementation of {@link IEventStore}
 * is currently {@link FileEventStore}, an instance of {@link FileEventStore} is always created 
 * 
 * @author gpapanikos
 *
 */
public class EventStoreFactory
{
	/**
	 * Instantiates the appropriate {@link IEventStore} implementation. Since the only available implementation 
	 * of {@link IEventStore} is currently {@link FileEventStore}, an instance of {@link FileEventStore}
	 * is always returned
	 * 
	 * @return the {@link IEventStore} implementation
	 */
	public static IEventStore getManager() throws GRS2EventStoreException
	{
		try
		{
			return new FileEventStore();
		} catch (IOException e)
		{
			throw new GRS2EventStoreException("Could not initialize persistency manager", e);
		}
	}
}
