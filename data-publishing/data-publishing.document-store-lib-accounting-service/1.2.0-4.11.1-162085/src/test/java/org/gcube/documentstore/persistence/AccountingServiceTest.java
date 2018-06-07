/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.Arrays;
import java.util.List;

import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.utility.TestUsageRecord;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class AccountingServiceTest extends ScopedTest {

	private static final Logger logger = LoggerFactory.getLogger(AccountingServiceTest.class);
	
	protected PersistenceBackend persistenceBackend;
	
	
	@Test
	public void testSingleInsertService() throws Exception {
		Record record = TestUsageRecord.createTestServiceUsageRecord();
		persistenceBackend.accountWithFallback(record);

	}

	@Test
	public void testMultipleInsertService() throws Exception {
		int count = 2;
		Record[] records = new Record[count];
		for (int i = 0; i < count; i++) {
			records[i] = TestUsageRecord.createTestServiceUsageRecord();
		}
		
		
		List<Record> recordList = Arrays.asList(records);
		String ret = DSMapper.marshal(recordList);
		logger.debug(ret);
		
		persistenceBackend.accountWithFallback(records);

	}

	@Test
	public void testMultipleInsertStorage() throws Exception {

		int count = 10;
		Record[] records = new Record[count];
		for (int i = 0; i < count; i++) {
			records[i] = TestUsageRecord.createTestStorageUsageRecord();
		}
		persistenceBackend.accountWithFallback(records);

	}

	@Test
	public void testMultipleInsertJob() throws Exception {
		int count = 10;
		Record[] records = new Record[count];
		for (int i = 0; i < count; i++) {
			records[i] = TestUsageRecord.createTestJobUsageRecord();
		}
		persistenceBackend.accountWithFallback(records);

	}

	@Test
	public void testMultipleInsertPortlet() throws Exception {
		int count = 10;
		Record[] records = new Record[count];
		for (int i = 0; i < count; i++) {
			records[i] = TestUsageRecord.createTestPortletUsageRecord();
		}
		persistenceBackend.accountWithFallback(records);

	}
	
}