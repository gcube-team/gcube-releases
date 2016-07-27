/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.gcube.documentstore.records.Record;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
class DefaultPersitenceExecutor implements PersistenceExecutor {

	final PersistenceBackend persistenceBackend;
	
	DefaultPersitenceExecutor(PersistenceBackend persistenceBackend){
		this.persistenceBackend = persistenceBackend;
	}
	
	@Override
	public void persist(Record... records) throws Exception {
		persistenceBackend.accountWithFallback(records);	
	}
	
}
