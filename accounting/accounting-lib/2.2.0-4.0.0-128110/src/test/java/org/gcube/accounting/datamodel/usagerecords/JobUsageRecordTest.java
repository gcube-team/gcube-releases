/**
 * 
 */
package org.gcube.accounting.datamodel.usagerecords;

import java.util.HashSet;
import java.util.Set;

import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractJobUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.testutility.ScopedTest;
import org.gcube.testutility.TestUsageRecord;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class JobUsageRecordTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(JobUsageRecordTest.class);
	
	public static Set<String> getExpectedRequiredFields(){
		Set<String> expectedRequiredFields = new HashSet<String>();
		expectedRequiredFields.add(Record.ID);
		expectedRequiredFields.add(UsageRecord.CONSUMER_ID);
		expectedRequiredFields.add(UsageRecord.CREATION_TIME);
		expectedRequiredFields.add(UsageRecord.RECORD_TYPE);
		expectedRequiredFields.add(UsageRecord.SCOPE);
		expectedRequiredFields.add(UsageRecord.OPERATION_RESULT);
		expectedRequiredFields.add(AbstractJobUsageRecord.JOB_ID);
		expectedRequiredFields.add(AbstractJobUsageRecord.WALL_DURATION);
		return expectedRequiredFields;
	}
	
	@Test(expected=InvalidValueException.class)
	public void scopeNotSetValidationError() throws InvalidValueException {
		JobUsageRecord usageRecord = TestUsageRecord.createTestJobUsageRecord();
		usageRecord.validate();
		logger.debug("{}", usageRecord);
	}
	
	@Test
	public void testRequiredFields() throws InvalidValueException{
		JobUsageRecord usageRecord = TestUsageRecord.createTestJobUsageRecord();
		Assert.assertTrue(usageRecord.getScope()==null);
		usageRecord.setScope(TestUsageRecord.TEST_SCOPE);
		
		Set<String> expectedRequiredFields = getExpectedRequiredFields();
		logger.debug("Expected Required Fields : {}", expectedRequiredFields);
		
		Set<String> gotRequiredFields = usageRecord.getRequiredFields();
		logger.debug("Got Required Fields : {}", gotRequiredFields);
		
		Assert.assertTrue(expectedRequiredFields.containsAll(gotRequiredFields));
		Assert.assertTrue(gotRequiredFields.containsAll(expectedRequiredFields));
		
		usageRecord.validate();
		logger.debug("{}", usageRecord);
	}
	
}
