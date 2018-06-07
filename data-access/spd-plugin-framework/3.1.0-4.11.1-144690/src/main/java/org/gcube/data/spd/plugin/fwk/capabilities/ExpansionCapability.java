package org.gcube.data.spd.plugin.fwk.capabilities;

import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public interface ExpansionCapability {

	public void getSynonyms(ObjectWriter<String> writer, String scientifcName ) throws ExternalRepositoryException;
	
}
