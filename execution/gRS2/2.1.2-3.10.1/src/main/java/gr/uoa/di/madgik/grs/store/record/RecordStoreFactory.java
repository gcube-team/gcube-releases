package gr.uoa.di.madgik.grs.store.record;

import java.io.IOException;

/**
 * This utility class is used to initialize an instance of an {@link IRecordStore} according
 * to system configuration. Since the only available implementation of {@link IRecordStore}
 * is currently {@link FileRecordStore}, an instance of {@link FileRecordStore} is always created 
 * 
 * @author gpapanikos
 *
 */
public class RecordStoreFactory
{
	/**
	 * Instantiates the appropriate {@link IRecordStore} implementation. Since the only available implementation 
	 * of {@link IRecordStore} is currently {@link FileRecordStore}, an instance of {@link FileRecordStore}
	 * is always returned
	 * 
	 * @return the {@link IRecordStore} implementation
	 */
	public static IRecordStore getManager() throws GRS2RecordStoreException
	{
		try
		{
			return new FileRecordStore();
		} catch (IOException e)
		{
			throw new GRS2RecordStoreException("Could not initialize persistency manager", e);
		}
	}
}
