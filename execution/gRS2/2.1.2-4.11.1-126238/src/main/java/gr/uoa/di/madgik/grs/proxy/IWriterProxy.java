package gr.uoa.di.madgik.grs.proxy;

import java.net.URI;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;

/**
 * The {@link IProxy} specialization that needs to be implemented by the proxy implementations
 * that are used by writer clients
 * 
 * @author gpapanikos
 *
 */
public interface IWriterProxy extends IProxy
{
	/**
	 * Sets the key that was assigned to the serving {@link IBuffer} through the buffer registry
	 * 
	 * @param key the registry key
	 * @throws GRS2ProxyException the status of the proxy does not permit this operation to be completed
	 */
	public void setKey(String key) throws GRS2ProxyException;
	/**
	 * After setting the registry key, this method is used to retrieve the {@link IMirror} that can
	 * serve the mirroring procedure for a reader
	 * 
	 * @return the associated {@link IMirror}
	 * @throws GRS2ProxyException the {@link IMirror} cannot be created
	 */
	public IMirror bind() throws GRS2ProxyException;
	/**
	 * Retrieves a locator through which a reader proxy can contact the {@link IMirror}
	 * serving the registered {@link IBuffer}
	 * 
	 * @return the URI locator
	 * @throws GRS2ProxyException the status of the proxy does not permit this operation to be completed
	 */
	public URI getLocator() throws GRS2ProxyException;

}
