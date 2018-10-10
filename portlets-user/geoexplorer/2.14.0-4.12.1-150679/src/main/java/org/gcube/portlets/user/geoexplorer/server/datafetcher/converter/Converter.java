/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.datafetcher.converter;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface Converter<I,O> {
	
	public O convert(I input) throws Exception;

}
