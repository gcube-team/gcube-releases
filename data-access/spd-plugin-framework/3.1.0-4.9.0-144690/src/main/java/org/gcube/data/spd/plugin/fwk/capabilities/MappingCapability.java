package org.gcube.data.spd.plugin.fwk.capabilities;

import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public interface MappingCapability{

	/**
	 * 
	 * this method returns a set of scientific names related to the common name passed as input
	 *  
	 * @param commonName to Map
	 * @return a set of scientificNames
	 * @throws Exception
	 */
	public void getRelatedScientificNames(ObjectWriter<String> writer, String commonName) throws ExternalRepositoryException;
}
