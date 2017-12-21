/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface KeyProvider<T> {
	
	public String getKey(T value);

}
