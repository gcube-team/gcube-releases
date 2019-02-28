package org.gcube.data.spd.plugin.fwk;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;



public interface Searchable<T extends ResultElement>{

	public void searchByScientificName(String word, ObjectWriter<T> writer, Condition ... properties) throws ExternalRepositoryException;
	
	public Class<T> getHandledClass();
}
