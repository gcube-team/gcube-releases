/**
 * 
 */
package org.gcube.accounting.persistence;

import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.persistence.PersistenceBackend;
import org.gcube.documentstore.persistence.PersistenceBackendFactory;
import org.gcube.documentstore.records.Record;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccountingPersistence {
	
	protected String scope;
	
	protected AccountingPersistence(String scope){
		this.scope = scope;
	}

	private PersistenceBackend getAccountingPersistence(){
		return PersistenceBackendFactory.getPersistenceBackend(this.scope);
	}
	
	/**
	 * Persist the {@link #UsageRecord}.
	 * The Record is validated first, then accounted, in a separated thread. 
	 * So that the program can continue the execution.
	 * If the persistence fails the class write that the record in a local file
	 * so that the {@link #UsageRecord} can be recorder later.
	 * @param record the {@link #UsageRecord} to persist
	 * @throws InvalidValueException 
	 */
	public void account(final Record record) throws InvalidValueException {
		try {
			getAccountingPersistence().account(record);
		} catch (org.gcube.documentstore.exception.InvalidValueException e) {
			throw new InvalidValueException(e);
		}
	}
	
	public void flush(long timeout, TimeUnit timeUnit) throws Exception {
		getAccountingPersistence().flush(timeout, timeUnit);
	}
	
	public void close() throws Exception{
		getAccountingPersistence().close();
	}
	
}
