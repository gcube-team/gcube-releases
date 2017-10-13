/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.util.Set;

import org.gcube.accounting.datamodel.usagerecords.StorageStatusRecord;
import org.gcube.accounting.datamodel.usagerecords.StorageStatusRecordTest;
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
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 *
 */
public class AggregatedStorageStatusRecordTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(AggregatedStorageStatusRecordTest.class);
	
	@Test
	public void testRequiredFields() throws InvalidValueException {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		StorageStatusRecord storageVolumeUsageRecord = TestUsageRecord.createTestStorageVolumeUsageRecord();
		Assert.assertTrue(storageVolumeUsageRecord.getScope()==null);
		storageVolumeUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		AggregatedStorageStatusRecord aggregatedStorageUsageRecord = new AggregatedStorageStatusRecord(storageVolumeUsageRecord);
		
		Set<String> expectedRequiredFields = StorageStatusRecordTest.getExpectedRequiredFields();
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
		StorageStatusRecord storageVolumeUsageRecord = TestUsageRecord.createTestStorageVolumeUsageRecord();
		Assert.assertTrue(storageVolumeUsageRecord.getScope()==null);
		storageVolumeUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		storageVolumeUsageRecord.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		storageVolumeUsageRecord.validate();
		logger.debug("StorageVolumeUsageRecord : {}", storageVolumeUsageRecord);
		
		AggregatedStorageStatusRecord aggregated = new AggregatedStorageStatusRecord(storageVolumeUsageRecord);
		logger.debug("StorageVolumeUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		StorageStatusRecord storageVolumeUsageRecord2 = TestUsageRecord.createTestStorageVolumeUsageRecord();
		storageVolumeUsageRecord2.setScope(TestUsageRecord.TEST_SCOPE);
		
		storageVolumeUsageRecord2.validate();
		logger.debug("StorageVolumeUsageRecord 2 : {}", storageVolumeUsageRecord2);

	
		long secondDataVolume = storageVolumeUsageRecord2.getDataVolume();
		
		long secondDataCount = storageVolumeUsageRecord2.getDataCount();
		
		aggregated.aggregate(storageVolumeUsageRecord2);
		logger.debug("Resulting Aggregated StorageVolumeUsageRecord: {}", aggregated);
		aggregated.validate();
		
		Assert.assertTrue(aggregated.getDataVolume() == (secondDataVolume));
		Assert.assertTrue(aggregated.getDataCount() == (secondDataCount));
		Assert.assertTrue(aggregated.getOperationCount() == 2);
		Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
	}
	
	@Test
	public void secondAsAggregated() throws InvalidValueException, NotAggregatableRecordsExceptions {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		StorageStatusRecord storageStatusUsageRecord = TestUsageRecord.createTestStorageVolumeUsageRecord();
		Assert.assertTrue(storageStatusUsageRecord.getScope()==null);
		storageStatusUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		storageStatusUsageRecord.validate();
		logger.debug("StorageVolumeUsageRecord : {}", storageStatusUsageRecord);
		
		AggregatedStorageStatusRecord aggregated = new AggregatedStorageStatusRecord(storageStatusUsageRecord);
		logger.debug("StorageVolumeUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		StorageStatusRecord storageVolumeUsageRecord2 = TestUsageRecord.createTestStorageVolumeUsageRecord();
		Assert.assertTrue(storageVolumeUsageRecord2.getScope()==null);
		storageVolumeUsageRecord2.setScope(TestUsageRecord.TEST_SCOPE);
		storageVolumeUsageRecord2.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		storageVolumeUsageRecord2.validate();
		logger.debug("StorageVolumeUsageRecord 2 : {}", storageVolumeUsageRecord2);
		AggregatedStorageStatusRecord converted = new AggregatedStorageStatusRecord(storageVolumeUsageRecord2);
		logger.debug("StorageVolumeUsageRecord Converted to Aggregated: {}", converted);
		converted.validate();

		
		//long firstDataVolume = aggregated.getDataVolume();
		long secondDataVolume = converted.getDataVolume();
		
		long secondDataCount =storageVolumeUsageRecord2.getDataCount();
		
		aggregated.aggregate(converted);
		logger.debug("Resulting Aggregated StorageVolumeUsageRecord: {}", aggregated);
		aggregated.validate();
		
		Assert.assertTrue(aggregated.getDataVolume() == (secondDataVolume));
		Assert.assertTrue(aggregated.getDataCount() == (secondDataCount));
		Assert.assertTrue(aggregated.getOperationCount() == 2);
		Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
	}
	
	@Test
	public void aggregationStressTest() throws InvalidValueException, NotAggregatableRecordsExceptions {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		StorageStatusRecord storageVolumeUsageRecord = TestUsageRecord.createTestStorageVolumeUsageRecord();
		Assert.assertTrue(storageVolumeUsageRecord.getScope()==null);
		storageVolumeUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		storageVolumeUsageRecord.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		storageVolumeUsageRecord.validate();
		logger.debug("StorageVolumeUsageRecord : {}", storageVolumeUsageRecord);
		
		AggregatedStorageStatusRecord aggregated = new AggregatedStorageStatusRecord(storageVolumeUsageRecord);
		logger.debug("StorageVolumeUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		for(int i=2; i<10; i++){
			
			StorageStatusRecord sur = TestUsageRecord.createTestStorageVolumeUsageRecord();
			sur.setScope(TestUsageRecord.TEST_SCOPE);
			sur.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
			sur.validate();
			logger.debug("Cycle StorageVolumeUsageRecord {}: {}", i, sur);
			
			//long oldDataVolume = aggregated.getDataVolume();
			long newDataVolume = sur.getDataVolume();
			
			
			//long oldDataCount = aggregated.getDataCount();
			long newDataCount = sur.getDataCount();
			
			aggregated.aggregate(sur);
			logger.debug("Resulting Aggregated StorageUsageRecord : {}", aggregated);
			aggregated.validate();
			
			Assert.assertTrue(aggregated.getDataVolume() == (newDataVolume));
			Assert.assertTrue(aggregated.getDataCount() == (newDataCount));
			Assert.assertTrue(aggregated.getOperationCount() == i);
			Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
		}
		
		logger.debug("Resulting Aggregated StorageUsageRecord: {}", aggregated);
	}
	
	
	
	
	
	
}
