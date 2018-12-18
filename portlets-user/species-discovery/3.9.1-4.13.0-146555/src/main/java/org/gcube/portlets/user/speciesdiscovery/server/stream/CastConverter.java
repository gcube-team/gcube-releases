/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class CastConverter<I, O> implements Converter<I, O> {

	@SuppressWarnings("unchecked")
	@Override
	public O convert(I input) throws Exception {
		return (O)input;
	}

}
