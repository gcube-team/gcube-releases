/**
 * 
 */
package org.gcube.data.oai.tmplugin.repository.iterators;

import org.gcube.common.data.RecordIterator;
import org.gcube.data.oai.tmplugin.OAIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Fabio Simeoni
 *
 */
public abstract class RepositoryIterator extends RecordIter {

	private static final Logger log = LoggerFactory.getLogger(RepositoryIterator.class);

	@Override
	public boolean hasNext() {

		try {
			if (records==null) {
				records = fetchRecords();
				log.trace("expecting to iterate over a max of {}",records.getCompleteListSize());
				return hasNext();
			}
			else{
				if (records.hasNext()) return true;
				else {
					//					records.close();
					return false;
				}
			}

		}
		catch(Exception moveToNextSet) {
			log.error("could not list records from {}",moveToNextSet);
			return false;
		}


	}
	
	
	protected abstract RecordIterator fetchRecords() throws Exception;
}
