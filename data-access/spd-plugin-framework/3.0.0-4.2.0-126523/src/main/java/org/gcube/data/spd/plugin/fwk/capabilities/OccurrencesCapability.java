package org.gcube.data.spd.plugin.fwk.capabilities;

import java.util.Iterator;

import org.gcube.data.spd.model.PropertySupport;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;

public abstract class OccurrencesCapability implements PropertySupport, Searchable<OccurrencePoint>{

	/**
	 * retrieves all occurrences for the given ids
	 * 
	 * @param writer the stream where the elements must be wrote
	 * @param keys a list of products keys
	 * @throws Exception
	 */
	public abstract void getOccurrencesByProductKeys(ClosableWriter<OccurrencePoint> writer, Iterator<String> keys) throws ExternalRepositoryException; 
	
	/**
	 * retrieves all occurrences for the given ids
	 * 
	 * @param writer the stream where the elements must be wrote
	 * @param ids a list of occurrence ids
	 * @throws Exception
	 */
	public abstract void getOccurrencesByIds(ClosableWriter<OccurrencePoint> writer, Iterator<String> ids) throws ExternalRepositoryException;

	@Override
	public Class<OccurrencePoint> getHandledClass() {
		return OccurrencePoint.class;
	} 
	
	
	
}
