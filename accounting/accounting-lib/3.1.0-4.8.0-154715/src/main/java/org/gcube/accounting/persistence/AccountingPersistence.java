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
	
	protected String context;
	
	protected AccountingPersistence(String context){
		this.context = context;
	}

	private PersistenceBackend getAccountingPersistence(){
		return PersistenceBackendFactory.getPersistenceBackend(this.context);
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
	
	public void flush() throws Exception {
		getAccountingPersistence().flush();
	}
	
	/**
	 * Used {@link AccountingPersistence#flush() instead}
	 * @param timeout
	 * @param timeUnit
	 * @throws Exception
	 */
	@Deprecated
	public void flush(long timeout, TimeUnit timeUnit) throws Exception {
		this.flush();
	}
	
	public void close() throws Exception{
		getAccountingPersistence().close();
	}
	
	public boolean isConnectionActive() throws Exception {
		return getAccountingPersistence().isConnectionActive();
	}
		
}
