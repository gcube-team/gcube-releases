/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.utility.TestUsageRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 *
 */
public class PersistenceAccountingServiceTest  {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceAccountingServiceTest.class);
	

	
	@Before
	public void before() throws Exception{
			//SecurityTokenProvider.instance.set("36501a0d-a205-4bf1-87ad-4c7185faa0d6-98187548");
			//SecurityTokenProvider.instance.set("3acdde42-6883-4564-b3ba-69f6486f6fe0-98187548");
			
		//	SecurityTokenProvider.instance.set("2580f89f-d7a8-452d-a131-b3859bd771fd-98187548");
		
			//ScopeProvider.instance.set("/gcube/devNext");
			//ScopeProvider.instance.set("/gcube");
			
			
		//	ScopeProvider.instance.set("/gcube/devNext/NextNext");
	}
	
	@After
	public void after(){
		//SecurityTokenProvider.instance.reset();
	}
	
	
	@Test
	public void testSingleInsertService() throws Exception{
		// Production-Preproduction Nodes
		PersistenceBackendConfiguration persitenceConfiguration = null;
		PersistenceAccountingService accountingService = new PersistenceAccountingService();
		accountingService.prepareConnection(persitenceConfiguration);
		
		Record record = TestUsageRecord.createTestServiceUsageRecord();
		accountingService.reallyAccount(record);
		
	}
	

	@Test
	public void testMultipleInsertService() throws Exception{
		// Production-Preproduction Nodes
		PersistenceBackendConfiguration persitenceConfiguration = null;
		PersistenceAccountingService accountingService = new PersistenceAccountingService();
		accountingService.prepareConnection(persitenceConfiguration);
		Integer count=2;
		Record[] records = new Record[count]; 
		for(int i=0; i<count; i++){
			records[i] =TestUsageRecord.createTestServiceUsageRecord();
		}
		accountingService.accountWithFallback(records);
		logger.debug("send a :{} record",count);
		
	}
	
	@Test
	public void testMultipleInsertStorage() throws Exception{
		// Production-Preproduction Nodes
		PersistenceBackendConfiguration persitenceConfiguration = null;
		PersistenceAccountingService accountingService = new PersistenceAccountingService();
		accountingService.prepareConnection(persitenceConfiguration);
		Integer count=10;
		Record[] records = new Record[count]; 
		for(int i=0; i<count; i++){
			records[i] =TestUsageRecord.createTestStorageUsageRecord();
		}
		accountingService.accountWithFallback(records);
		logger.debug("send a :{} record",count);
		
	}

	
	@Test
	public void testMultipleInsertTask() throws Exception{
		// Production-Preproduction Nodes
		PersistenceBackendConfiguration persitenceConfiguration = null;
		PersistenceAccountingService accountingService = new PersistenceAccountingService();
		accountingService.prepareConnection(persitenceConfiguration);
		Integer count=12;
		Record[] records = new Record[count]; 
		for(int i=0; i<count; i++){
			records[i] =TestUsageRecord.createTestTaskUsageRecord();
		}
		accountingService.accountWithFallback(records);
		logger.debug("send a :{} record",count);
		
	}
	
	@Test
	public void testMultipleInsertJob() throws Exception{
		// Production-Preproduction Nodes
		PersistenceBackendConfiguration persitenceConfiguration = null;
		PersistenceAccountingService accountingService = new PersistenceAccountingService();
		accountingService.prepareConnection(persitenceConfiguration);
		Integer count=12;
		Record[] records = new Record[count]; 
		for(int i=0; i<count; i++){
			records[i] =TestUsageRecord.createTestJobUsageRecord();
		}
		accountingService.accountWithFallback(records);
		logger.debug("send a :{} record",count);
		
	}
	
	@Test
	public void testMultipleInsertPortlet() throws Exception{
		// Production-Preproduction Nodes
		PersistenceBackendConfiguration persitenceConfiguration = null;
		PersistenceAccountingService accountingService = new PersistenceAccountingService();
		accountingService.prepareConnection(persitenceConfiguration);
		Integer count=12;
		Record[] records = new Record[count]; 
		for(int i=0; i<count; i++){
			records[i] =TestUsageRecord.createTestPortletUsageRecord();
		}
		accountingService.accountWithFallback(records);
		logger.debug("send a :{} record",count);
		
	}
	
	
	@Test
	public void testSearchConfiguration() throws Exception{	
		// Production-Preproduction Nodes
		PersistenceBackendConfiguration persitenceConfiguration = null;
		PersistenceAccountingService accountingService = new PersistenceAccountingService();
		accountingService.prepareConnection(persitenceConfiguration);
		
	}
	
	
	
}