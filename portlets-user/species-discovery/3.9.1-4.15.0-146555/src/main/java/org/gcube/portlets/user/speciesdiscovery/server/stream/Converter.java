/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface Converter<I,O> {
	
	public O convert(I input) throws Exception;

}
