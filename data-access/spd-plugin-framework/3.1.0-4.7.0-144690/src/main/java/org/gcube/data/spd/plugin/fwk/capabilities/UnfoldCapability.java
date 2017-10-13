package org.gcube.data.spd.plugin.fwk.capabilities;

import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public interface UnfoldCapability {

	public void unfold(ObjectWriter<String> writer, String scientificName) throws ExternalRepositoryException;
	
}
