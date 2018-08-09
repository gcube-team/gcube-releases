package gr.uoa.di.madgik.grs.proxy;

import java.net.URI;

import gr.uoa.di.madgik.grs.buffer.IBuffer;

/**
 * The {@link IProxy} specialization that needs to be implemented by the proxy implementations
 * that are used by reader clients
 * 
 * @author gpapanikos
 *
 */
public interface IReaderProxy extends IProxy
{
	/**
	 * Populates an instance of the {@link IReaderProxy} implementer with the information
	 * provided through the specific locator URI
	 * 
	 * @param locator
	 * @throws GRS2ProxyException The proxy initialization could not be completed
	 */
	public void fromLocator(URI locator) throws GRS2ProxyException;
	/**
	 * After the proxy has been initialized, this method can be used to provide a hint to the
	 * {@link IReaderProxy} to use a capacity different than that of the producer's buffer.
	 * Whether or not this hint is taken into account depends on the {@link IReaderProxy} implementation.
	 * For example, it is highly unlikely that it will be honored in cases when both the producer and
	 * the consumer run into the same address space.
	 * 
	 * @param capacity
	 * @param GRS2ProxyException The capacity of the buffer cannot be overriden
	 */
	public void overrideBufferCapacity(int capacity) throws GRS2ProxyException;
	
	/**
	 * After the proxy has been initialized, through this method, the {@link IBuffer} that
	 * can be used by the reader to access the writer side {@link IBuffer} can be retrieved 
	 * 
	 * @return The {@link IBuffer} that is serving the reader
	 * @throws GRS2ProxyException the {@link IBuffer} could not be retrieved
	 */
	public IBuffer getBuffer() throws GRS2ProxyException;

}
