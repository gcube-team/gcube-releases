/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.util.Set;

import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecordTest;
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
public class AggregatedServiceUsageRecordTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(AggregatedServiceUsageRecordTest.class);
	
	@Test
	public void testRequiredFields() throws InvalidValueException {
		ServiceUsageRecord serviceUsageRecord = TestUsageRecord.createTestServiceUsageRecord();
		Assert.assertTrue(serviceUsageRecord.getScope()==null);
		serviceUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		AggregatedServiceUsageRecord aggregatedServiceUsageRecord = new AggregatedServiceUsageRecord(serviceUsageRecord);
		
		Set<String> expectedRequiredFields = ServiceUsageRecordTest.getExpectedRequiredFields();
		expectedRequiredFields.add(AggregatedServiceUsageRecord.MAX_INVOCATION_TIME);
		expectedRequiredFields.add(AggregatedServiceUsageRecord.MIN_INVOCATION_TIME);
		
		expectedRequiredFields.addAll(AggregatedUsageRecordTest.getExpectedRequiredFields());
		
		
		logger.debug("Expected Required Fields : {}", expectedRequiredFields);
		
		Set<String> gotRequiredFields = aggregatedServiceUsageRecord.getRequiredFields();

		logger.debug("Got Required Fields : {}", gotRequiredFields);
		
		Assert.assertTrue(expectedRequiredFields.containsAll(gotRequiredFields));
		Assert.assertTrue(gotRequiredFields.containsAll(expectedRequiredFields));
		
	}
	
	@Test
	public void secondAsNotAggregated() throws InvalidValueException, NotAggregatableRecordsExceptions {
		ServiceUsageRecord serviceUsageRecord = TestUsageRecord.createTestServiceUsageRecord();
		Assert.assertTrue(serviceUsageRecord.getScope()==null);
		serviceUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		serviceUsageRecord.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		serviceUsageRecord.validate();
		logger.debug("ServiceUsageRecord : {}", serviceUsageRecord);
		
		AggregatedServiceUsageRecord aggregated = new AggregatedServiceUsageRecord(serviceUsageRecord);
		logger.debug("ServiceUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		ServiceUsageRecord serviceUsageRecord2 = TestUsageRecord.createTestServiceUsageRecord();
		Assert.assertTrue(serviceUsageRecord2.getScope()==null);
		serviceUsageRecord2.setScope(TestUsageRecord.TEST_SCOPE);
		
		serviceUsageRecord2.validate();
		logger.debug("ServiceUsageRecord 2 : {}", serviceUsageRecord2);
		
		long firstDuration = serviceUsageRecord.getDuration();
		long secondDuration = serviceUsageRecord2.getDuration();
		
		aggregated.aggregate(serviceUsageRecord2);
		logger.debug("Resulting Aggregated ServiceUsageRecord: {}", aggregated);
		aggregated.validate();
		
		Assert.assertTrue(aggregated.getDuration() == ((firstDuration + secondDuration)/2));
		Assert.assertTrue(aggregated.getOperationCount() == 2);
		
		if(firstDuration >= secondDuration){
			Assert.assertTrue(aggregated.getMaxInvocationTime() == firstDuration);
			Assert.assertTrue(aggregated.getMinInvocationTime() == secondDuration);
		}else{
			Assert.assertTrue(aggregated.getMaxInvocationTime() == secondDuration);
			Assert.assertTrue(aggregated.getMinInvocationTime() == firstDuration);
		}
		
		Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
		
		Assert.assertTrue(aggregated.getRecordType().compareTo(ServiceUsageRecord.class.getSimpleName())==0);
		
	}
	
	@Test
	public void secondAsAggregated() throws InvalidValueException, NotAggregatableRecordsExceptions {
		ServiceUsageRecord serviceUsageRecord = TestUsageRecord.createTestServiceUsageRecord();
		Assert.assertTrue(serviceUsageRecord.getScope()==null);
		serviceUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		serviceUsageRecord.validate();
		logger.debug("ServiceUsageRecord : {}", serviceUsageRecord);
		
		AggregatedServiceUsageRecord aggregated = new AggregatedServiceUsageRecord(serviceUsageRecord);
		logger.debug("ServiceUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		ServiceUsageRecord serviceUsageRecord2 = TestUsageRecord.createTestServiceUsageRecord();
		Assert.assertTrue(serviceUsageRecord2.getScope()==null);
		serviceUsageRecord2.setScope(TestUsageRecord.TEST_SCOPE);
		serviceUsageRecord2.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		serviceUsageRecord2.validate();
		logger.debug("ServiceUsageRecord 2 : {}", serviceUsageRecord2);
		AggregatedServiceUsageRecord converted = new AggregatedServiceUsageRecord(serviceUsageRecord2);
		logger.debug("ServiceUsageRecord 2 Converted to Aggregated: {}", converted);
		converted.validate();
		
		long firstduration = aggregated.getDuration();
		long secondDuration = converted.getDuration();
		
		aggregated.aggregate(converted);
		logger.debug("Resulting Aggregated ServiceUsageRecord: {}", aggregated);
		aggregated.validate();
		
		Assert.assertTrue(aggregated.getDuration() == ((firstduration + secondDuration)/2));
		Assert.assertTrue(aggregated.getOperationCount() == 2);
		
		if(firstduration >= secondDuration){
			Assert.assertTrue(aggregated.getMaxInvocationTime() == firstduration);
			Assert.assertTrue(aggregated.getMinInvocationTime() == secondDuration);
		}else{
			Assert.assertTrue(aggregated.getMaxInvocationTime() == secondDuration);
			Assert.assertTrue(aggregated.getMinInvocationTime() == firstduration);
		}
		Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
		
		Assert.assertTrue(aggregated.getRecordType().compareTo(ServiceUsageRecord.class.getSimpleName())==0);
	}
	
	protected long durationWeightedAverage(int numberA, long durationA, int numberB, long durationB){
		return ((numberA * durationA) + (numberB * durationB)) / (numberA + numberB);
	}
	
	@Test
	public void aggregationStressTest() throws InvalidValueException, NotAggregatableRecordsExceptions {
		ServiceUsageRecord serviceUsageRecord = TestUsageRecord.createTestServiceUsageRecord();
		Assert.assertTrue(serviceUsageRecord.getScope()==null);
		serviceUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		serviceUsageRecord.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		serviceUsageRecord.validate();
		logger.debug("ServiceUsageRecord : {}", serviceUsageRecord);
		
		AggregatedServiceUsageRecord aggregated = new AggregatedServiceUsageRecord(serviceUsageRecord);
		logger.debug("ServiceUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		for(int i=2; i<1002; i++){
			
			ServiceUsageRecord sur = TestUsageRecord.createTestServiceUsageRecord();
			sur.setScope(TestUsageRecord.TEST_SCOPE);
			sur.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
			sur.validate();
			logger.debug("Cycle ServiceUsageRecord {}: {}", i, sur);
			
			long minInvocationTime = aggregated.getMinInvocationTime();
			long maxInvocationTime = aggregated.getMaxInvocationTime();
			long oldDuration = aggregated.getDuration();
			long surDuration = sur.getDuration();
			
			aggregated.aggregate(sur);
			logger.debug("Resulting Aggregated ServiceUsageRecord: {}", aggregated);
			aggregated.validate();
			
			long avgDuration = durationWeightedAverage(i-1, oldDuration, 1, surDuration);
			Assert.assertTrue(aggregated.getDuration() == (avgDuration));
			Assert.assertTrue(aggregated.getOperationCount() == i);
			
			if(minInvocationTime >= surDuration){
				Assert.assertTrue(aggregated.getMinInvocationTime() == surDuration);
			}else{
				Assert.assertTrue(aggregated.getMinInvocationTime() == minInvocationTime);
			}
			
			if(maxInvocationTime >= surDuration){
				Assert.assertTrue(aggregated.getMaxInvocationTime() == maxInvocationTime);
			}else{
				Assert.assertTrue(aggregated.getMaxInvocationTime() == surDuration);
			}
			
			Assert.assertFalse(aggregated.getResourceProperties().containsKey(TestUsageRecord.TEST_PROPERTY_NAME));
		}
		
		Assert.assertTrue(aggregated.getRecordType().compareTo(ServiceUsageRecord.class.getSimpleName())==0);
		
		logger.debug("Resulting Aggregated ServiceUsageRecord: {}", aggregated);
	}
	
}
