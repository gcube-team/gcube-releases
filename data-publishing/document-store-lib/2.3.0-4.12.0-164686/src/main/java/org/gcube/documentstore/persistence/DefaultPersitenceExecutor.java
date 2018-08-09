/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.gcube.documentstore.records.Record;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class DefaultPersitenceExecutor implements PersistenceExecutor {

	public final PersistenceBackend persistenceBackend;
	
	DefaultPersitenceExecutor(PersistenceBackend persistenceBackend){
		this.persistenceBackend = persistenceBackend;
	}
	
	@Override
	public void persist(Record... records) throws Exception {
		persistenceBackend.accountWithFallback(records);	
	}
	
}
