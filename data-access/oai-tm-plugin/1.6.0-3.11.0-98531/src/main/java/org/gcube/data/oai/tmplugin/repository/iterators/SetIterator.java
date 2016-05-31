/**
 * 
 */
package org.gcube.data.oai.tmplugin.repository.iterators;

import java.util.Iterator;
import java.util.List;

import org.gcube.common.data.RecordIterator;
import org.gcube.data.oai.tmplugin.repository.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Fabio Simeoni
 *
 */
public abstract class SetIterator extends RecordIter {

	private static final Logger log = LoggerFactory.getLogger(SetIterator.class);

	Iterator<Set> iterator; 
	
	public SetIterator(List<Set> sets) {
		iterator = sets.iterator();
	}
	
	
	@Override 
	public boolean hasNext() {

		try {
			if (records.hasNext())
				return true;
		}
		catch (Exception asFalseMovetoNextSet) {}
		
		while (iterator.hasNext()) {
				Set set = iterator.next();
				try {//read from next set
					log.trace("retrieving records in set "+set.id());
					
//					System.out.println("retrieving records in set "+set.id());
					records = fetchRecords(set);
					log.trace("expecting to iterate over a max of "+records.getCompleteListSize());	
					return hasNext();
				}
				catch(Exception moveToNextSet) {
					log.error("could not list records for set "+set.id(),moveToNextSet);
				}
			}
		
		//no more sets to try
		return records.hasNext();	
	}
	
	protected abstract RecordIterator fetchRecords(Set set) throws Exception;
}
