/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.util.Set;

import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.StorageUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.StorageUsageRecordTest;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.testutility.ScopedTest;
import org.gcube.testutility.TestUsageRecord;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class AggregatedStorageUsageRecordTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(AggregatedStorageUsageRecordTest.class);
	
	@Test
	public void testRequiredFields() throws InvalidValueException {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		StorageUsageRecord storageUsageRecord = TestUsageRecord.createTestStorageUsageRecord();
		Assert.assertTrue(storageUsageRecord.getScope()==null);
		storageUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		AggregatedStorageUsageRecord aggregatedStorageUsageRecord = new AggregatedStorageUsageRecord(storageUsageRecord);
		
		Set<String> expectedRequiredFields = StorageUsageRecordTest.getExpectedRequiredFields();
		expectedRequiredFields.addAll(AggregatedUsageRecordTest.getExpectedRequiredFields());
		
		logger.debug("Expected Required Fields : {}", expectedRequiredFields);
		
		Set<String> gotRequiredFields = aggregatedStorageUsageRecord.getRequiredFields();

		logger.debug("Got Required Fields : {}", gotRequiredFields);
		
		Assert.assertTrue(expectedRequiredFields.containsAll(gotRequiredFields));
		Assert.assertTrue(gotRequiredFields.containsAll(expectedRequiredFields));
		
	}
	
	@Test
	public void secondAsNotAggregated() throws InvalidValueException, NotAggregatableRecordsExceptions {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		StorageUsageRecord storageUsageRecord = TestUsageRecord.createTestStorageUsageRecord();
		Assert.assertTrue(storageUsageRecord.getScope()==null);
		storageUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		storageUsageRecord.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		storageUsageRecord.validate();
		logger.debug("StorageUsageRecord : {}", storageUsageRecord);
		
		AggregatedStorageUsageRecord aggregated = new AggregatedStorageUsageRecord(storageUsageRecord);
		logger.debug("StorageUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		StorageUsageRecord storageUsageRecord2 = TestUsageRecord.createTestStorageUsageRecord();
		storageUsageRecord2.setScope(TestUsageRecord.TEST_SCOPE);
		storageUsageRecord2.validate();
		logger.debug("StorageUsageRecord 2 : {}", storageUsageRecord2);

		long firstDataVolume = storageUsageRecord.getDataVolume();
		long secondDataVolume = storageUsageRecord2.getDataVolume();
		
		aggregated.aggregate(storageUsageRecord2);
		logger.debug("Resulting Aggregated ServiceUsageRecord: {}", aggregated);
		aggregated.validate();
		
		Assert.assertTrue(aggregated.getDataVolume() == (firstDataVolume + secondDataVolume));
		Assert.assertTrue(aggregated.getOperationCount() == 2);
		
		Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
	}
	
	@Test
	public void secondAsAggregated() throws InvalidValueException, NotAggregatableRecordsExceptions {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		StorageUsageRecord storageUsageRecord = TestUsageRecord.createTestStorageUsageRecord();
		Assert.assertTrue(storageUsageRecord.getScope()==null);
		storageUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		storageUsageRecord.validate();
		logger.debug("StorageUsageRecord : {}", storageUsageRecord);
		
		AggregatedStorageUsageRecord aggregated = new AggregatedStorageUsageRecord(storageUsageRecord);
		logger.debug("StorageUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		StorageUsageRecord storageUsageRecord2 = TestUsageRecord.createTestStorageUsageRecord();
		Assert.assertTrue(storageUsageRecord2.getScope()==null);
		storageUsageRecord2.setScope(TestUsageRecord.TEST_SCOPE);
		storageUsageRecord2.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		storageUsageRecord2.validate();
		logger.debug("StorageUsageRecord 2 : {}", storageUsageRecord2);
		AggregatedStorageUsageRecord converted = new AggregatedStorageUsageRecord(storageUsageRecord2);
		logger.debug("StorageUsageRecord Converted to Aggregated: {}", converted);
		converted.validate();

		long firstDataVolume = aggregated.getDataVolume();
		long secondDataVolume = converted.getDataVolume();
		
		aggregated.aggregate(converted);
		logger.debug("Resulting Aggregated StorageUsageRecord: {}", aggregated);
		aggregated.validate();
		
		Assert.assertTrue(aggregated.getDataVolume() == (firstDataVolume + secondDataVolume));
		Assert.assertTrue(aggregated.getOperationCount() == 2);
		Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
	}
	
	@Test
	public void aggregationStressTest() throws InvalidValueException, NotAggregatableRecordsExceptions {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		StorageUsageRecord storageUsageRecord = TestUsageRecord.createTestStorageUsageRecord();
		Assert.assertTrue(storageUsageRecord.getScope()==null);
		storageUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		storageUsageRecord.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		storageUsageRecord.validate();
		logger.debug("StorageUsageRecord : {}", storageUsageRecord);
		
		AggregatedStorageUsageRecord aggregated = new AggregatedStorageUsageRecord(storageUsageRecord);
		logger.debug("StorageUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		for(int i=2; i<1002; i++){
			
			StorageUsageRecord sur = TestUsageRecord.createTestStorageUsageRecord();
			sur.setScope(TestUsageRecord.TEST_SCOPE);
			sur.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
			sur.validate();
			logger.debug("Cycle StorageUsageRecord {}: {}", i, sur);
			
			long oldDataVolume = aggregated.getDataVolume();
			long newDataVolume = sur.getDataVolume();
			
			aggregated.aggregate(sur);
			logger.debug("Resulting Aggregated StorageUsageRecord : {}", aggregated);
			aggregated.validate();
			
			Assert.assertTrue(aggregated.getDataVolume() == (oldDataVolume + newDataVolume));
			Assert.assertTrue(aggregated.getOperationCount() == i);
			Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
		}
		
		logger.debug("Resulting Aggregated StorageUsageRecord: {}", aggregated);
	}
	
	
	
	
	
	
}
