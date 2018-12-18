package org.gcube.data.tm.state;

import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.data.tmf.api.SourceReader;
import org.globus.wsrf.ResourceException;

/**
 * A stateful resource of the T-Reader service.
 * 
 * @author Fabio Simeoni
 * @author Lucio Lelii (lucio.lelii@isti.cnr.it)
 */
public class TReaderResource extends AccessorResource {

	/**
	 * Returns the {@link SourceReader} of the plugin bound to this resource.
	 * @return the reader
	 * @throws GCUBEException if the writer could not be retrieved
	 */
	public SourceReader reader() throws GCUBEException {
		try {
			return getLocalResource().source().reader();
		} catch (ResourceException e) {
			throw new GCUBEUnrecoverableException(e);
		}
	}

}
