/**
 * 
 */
package org.gcube.accounting.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.documentstore.persistence.PersistenceBackendFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccountingPersistenceFactory {

	private AccountingPersistenceFactory(){}
	
	protected final static Map<String, AccountingPersistence> persistences;
	
	public static void initAccountingPackages(){
		PersistenceBackendFactory.addRecordPackage(ServiceUsageRecord.class.getPackage());
		PersistenceBackendFactory.addRecordPackage(AggregatedServiceUsageRecord.class.getPackage());
		
	}
	
	static {
		persistences = new HashMap<String, AccountingPersistence>();
		initAccountingPackages();
	}
	
	public static void setFallbackLocation(String path){
		PersistenceBackendFactory.setFallbackLocation(path);
	}
	
	public synchronized static AccountingPersistence getPersistence() {
		String context = BasicUsageRecord.getContextFromToken();
		AccountingPersistence accountingPersistence = persistences.get(context);
		if(accountingPersistence==null){
			accountingPersistence = new AccountingPersistence(context);
			persistences.put(context, accountingPersistence);
		}
		return accountingPersistence;
	}
	
	public static void flushAll(){
		PersistenceBackendFactory.flushAll();
	}
	
	/**
	 * Use {@link AccountingPersistenceFactory#flushAll() instead}
	 * @param timeout
	 * @param timeUnit
	 */
	@Deprecated
	public static void flushAll(long timeout, TimeUnit timeUnit){
		AccountingPersistenceFactory.flushAll();
	}
	
	public static void shutDown(){
		//flush all and shutdown connection and thread
		PersistenceBackendFactory.flushAll();
		PersistenceBackendFactory.shutdown();
		
		
	}
	
	/**
	 * Use {@link AccountingPersistenceFactory#shutDown() instead}
	 * @param timeout
	 * @param timeUnit
	 */
	@Deprecated
	public static void shutDown(long timeout, TimeUnit timeUnit){
		AccountingPersistenceFactory.shutDown();
	}
}
