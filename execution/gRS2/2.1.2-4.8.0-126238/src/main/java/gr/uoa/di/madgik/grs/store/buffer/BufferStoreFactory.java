package gr.uoa.di.madgik.grs.store.buffer;

/**
 * This utility class is used to initialize an instance of an {@link IBufferStore} according
 * to system configuration. Since the only available implementation of {@link IBufferStore}
 * is currently {@link FileBufferStore}, an instance of {@link FileBufferStore} is always created 
 * 
 * @author gpapanikos
 *
 */
public class BufferStoreFactory
{
	/**
	 * Instantiates the appropriate {@link IBufferStore} implementation. Since the only available implementation 
	 * of {@link IBufferStore} is currently {@link FileBufferStore}, an instance of {@link FileBufferStore}
	 * is always returned
	 * 
	 * @return the {@link IBufferStore} implementation
	 */
	public static IBufferStore getManager()
	{
		return new FileBufferStore();
	}
}
