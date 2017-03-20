/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.util.Set;

import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecordTest;
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
 */
public class AggregatedJobUsageRecordTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(AggregatedJobUsageRecordTest.class);
	
	@Test
	public void testRequiredFields() throws InvalidValueException {
		JobUsageRecord jobUsageRecord = TestUsageRecord.createTestJobUsageRecord();
		Assert.assertTrue(jobUsageRecord.getScope()==null);
		jobUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		AggregatedJobUsageRecord aggregatedJobUsageRecord = new AggregatedJobUsageRecord(jobUsageRecord);
		
		Set<String> expectedRequiredFields = JobUsageRecordTest.getExpectedRequiredFields();
		expectedRequiredFields.add(AggregatedJobUsageRecord.MAX_INVOCATION_TIME);
		expectedRequiredFields.add(AggregatedJobUsageRecord.MIN_INVOCATION_TIME);
		
		expectedRequiredFields.addAll(AggregatedUsageRecordTest.getExpectedRequiredFields());
		
		
		logger.debug("Expected Required Fields : {}", expectedRequiredFields);
		
		Set<String> gotRequiredFields = aggregatedJobUsageRecord.getRequiredFields();

		logger.debug("Got Required Fields : {}", gotRequiredFields);
		
		Assert.assertTrue(expectedRequiredFields.containsAll(gotRequiredFields));
		Assert.assertTrue(gotRequiredFields.containsAll(expectedRequiredFields));
		
	}
	
	@Test
	public void secondAsNotAggregated() throws InvalidValueException, NotAggregatableRecordsExceptions {
		JobUsageRecord jobUsageRecord = TestUsageRecord.createTestJobUsageRecord();
		Assert.assertTrue(jobUsageRecord.getScope()==null);
		jobUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		jobUsageRecord.setJobQualifier("TEST QUALIFIER JOB USAGE RECORD 1");
		jobUsageRecord.validate();
		AggregatedJobUsageRecord aggregated = new AggregatedJobUsageRecord(jobUsageRecord);
		logger.debug("jobUsageRecord Converted to Aggregated: {}", aggregated);	
		aggregated.validate();
		logger.debug("jobUsageRecord Converted to Aggregated post validate: {}", aggregated);
		
		JobUsageRecord jobUsageRecord2 = TestUsageRecord.createTestJobUsageRecord();
		Assert.assertTrue(jobUsageRecord2.getScope()==null);
		jobUsageRecord2.setScope(TestUsageRecord.TEST_SCOPE);
		
		jobUsageRecord2.validate();
		logger.debug("JobUsageRecord 2 : {}", jobUsageRecord2);
		
		
		
		long firstDuration = jobUsageRecord.getDuration();
		long secondDuration = jobUsageRecord2.getDuration();
		
		aggregated.aggregate(jobUsageRecord2);
		logger.debug("jobUsageRecord2 Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		logger.debug("jobUsageRecord2 Converted to Aggregated post validate: {}", aggregated);
		/*
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
		
		Assert.assertTrue(aggregated.getRecordType().compareTo(JobUsageRecord.class.getSimpleName())==0);
		
		*/
		
	}
	
	@Test
	public void secondAsAggregated() throws InvalidValueException, NotAggregatableRecordsExceptions {
		JobUsageRecord jobUsageRecord = TestUsageRecord.createTestJobUsageRecord();
		Assert.assertTrue(jobUsageRecord.getScope()==null);
		jobUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		jobUsageRecord.validate();
		logger.debug("ServiceUsageRecord : {}", jobUsageRecord);
		
		AggregatedJobUsageRecord aggregated = new AggregatedJobUsageRecord(jobUsageRecord);
		logger.debug("ServiceUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		JobUsageRecord jobUsageRecord2 = TestUsageRecord.createTestJobUsageRecord();
		Assert.assertTrue(jobUsageRecord2.getScope()==null);
		jobUsageRecord2.setScope(TestUsageRecord.TEST_SCOPE);
		jobUsageRecord2.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		jobUsageRecord2.validate();
		logger.debug("ServiceUsageRecord 2 : {}", jobUsageRecord2);
		AggregatedJobUsageRecord converted = new AggregatedJobUsageRecord(jobUsageRecord2);
		logger.debug("ServiceUsageRecord 2 Converted to Aggregated: {}", converted);
		converted.validate();
		
		long firstduration = aggregated.getDuration();
		long secondDuration = converted.getDuration();
		
		aggregated.aggregate(converted);
		logger.debug("Resulting Aggregated JobUsageRecord: {}", aggregated);
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
		
		Assert.assertTrue(aggregated.getRecordType().compareTo(JobUsageRecord.class.getSimpleName())==0);
	}
	
	protected long durationWeightedAverage(int numberA, long durationA, int numberB, long durationB){
		return ((numberA * durationA) + (numberB * durationB)) / (numberA + numberB);
	}
	
	@Test
	public void aggregationStressTest() throws InvalidValueException, NotAggregatableRecordsExceptions {
		JobUsageRecord jobUsageRecord = TestUsageRecord.createTestJobUsageRecord();
		Assert.assertTrue(jobUsageRecord.getScope()==null);
		jobUsageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		jobUsageRecord.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
		jobUsageRecord.validate();
		logger.debug("JobUsageRecord : {}", jobUsageRecord);
		
		AggregatedJobUsageRecord aggregated = new AggregatedJobUsageRecord(jobUsageRecord);
		logger.debug("JobUsageRecord Converted to Aggregated: {}", aggregated);
		aggregated.validate();
		
		for(int i=2; i<1002; i++){
			
			JobUsageRecord sur = TestUsageRecord.createTestJobUsageRecord();
			sur.setScope(TestUsageRecord.TEST_SCOPE);
			sur.setResourceProperty(TestUsageRecord.TEST_PROPERTY_NAME, TestUsageRecord.TEST_PROPERTY_VALUE);
			sur.validate();
			logger.debug("Cycle JobUsageRecord {}: {}", i, sur);
			
			long minInvocationTime = aggregated.getMinInvocationTime();
			long maxInvocationTime = aggregated.getMaxInvocationTime();
			long oldDuration = aggregated.getDuration();
			long surDuration = sur.getDuration();
			
			aggregated.aggregate(sur);
			logger.debug("Resulting Aggregated JobUsageRecord: {}", aggregated);
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
		
		Assert.assertTrue(aggregated.getRecordType().compareTo(JobUsageRecord.class.getSimpleName())==0);
		
		logger.debug("Resulting Aggregated JobUsageRecord: {}", aggregated);
	}
	
}
