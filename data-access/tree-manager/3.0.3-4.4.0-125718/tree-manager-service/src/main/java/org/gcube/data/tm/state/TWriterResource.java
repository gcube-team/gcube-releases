package org.gcube.data.tm.state;

import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.data.tmf.api.SourceWriter;
import org.globus.wsrf.ResourceException;

/**
 * A {@link AccessorResource} for write-only operations over a {@link SourceResource}.
 * 
 * @author Fabio Simeoni
 */
public final class TWriterResource extends AccessorResource {

	/**
	 * Returns the {@link SourceWriter} of the plugin bound to this resource.
	 * @return the writer
	 * @throws GCUBEException if the writer could not be retrieved
	 */
	public SourceWriter writer() throws GCUBEException {
		try {
			return getLocalResource().source().writer();
		} catch (ResourceException e) {
			throw new GCUBEUnrecoverableException(e);
		}
	}

}
