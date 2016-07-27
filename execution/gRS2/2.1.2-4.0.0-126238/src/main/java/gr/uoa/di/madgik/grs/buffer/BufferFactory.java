package gr.uoa.di.madgik.grs.buffer;

/**
 * This utility class is used to initialize an instance of an {@link IBuffer} according
 * to system configuration. Since the only available implementation of {@link IBuffer}
 * is currently {@link QueueBuffer}, an instance of {@link QueueBuffer} is always created 
 * 
 * @author gpapanikos
 *
 */
public class BufferFactory
{
	/**
	 * Instantiates the appropriate {@link IBuffer} implementation. Since the only available 
	 * implementation of {@link IBuffer} is currently {@link QueueBuffer}, an instance of 
	 * {@link QueueBuffer} is always returned
	 * 
	 * @return the {@link IBuffer} implementation
	 */
	public static IBuffer getBuffer()
	{
		return new QueueBuffer();
	}
}
