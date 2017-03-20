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
		String scope = BasicUsageRecord.getScopeFromToken();
		AccountingPersistence accountingPersistence = persistences.get(scope);
		if(accountingPersistence==null){
			accountingPersistence = new AccountingPersistence(scope);
		}
		return accountingPersistence;
	}
	
	public static void flushAll(long timeout, TimeUnit timeUnit){
		PersistenceBackendFactory.flushAll(timeout, timeUnit);
	}
	
}
