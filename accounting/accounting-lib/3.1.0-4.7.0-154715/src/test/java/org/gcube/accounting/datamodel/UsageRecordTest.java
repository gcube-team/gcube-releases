/**
 * 
 */
package org.gcube.accounting.datamodel;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.testutility.TestUsageRecord;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class UsageRecordTest {

	@Test
	public void testCompareToSameObject() throws InvalidValueException {
		UsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		UsageRecord ur = usageRecord;
		Assert.assertEquals(0, usageRecord.compareTo(ur));
		Assert.assertEquals(0, ur.compareTo(usageRecord));
	}
	/*	
	@Test
	public void testCompareToEqualsObject() throws Exception {
		UsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		UsageRecord ur = (UsageRecord) RecordUtility.getRecord(usageRecord.getResourceProperties());
		Assert.assertEquals(0, usageRecord.compareTo(ur));
		Assert.assertEquals(0, ur.compareTo(usageRecord));
	}
	
	@Test
	public void testCompareToComparedAddedProperty() throws Exception {
		UsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		UsageRecord ur = (UsageRecord) RecordUtility.getRecord(usageRecord.getResourceProperties());
		for(int i=1; i<31; i++){
			ur.setResourceProperty(Integer.toString(i), i);
			Assert.assertEquals(-i, usageRecord.compareTo(ur));
			Assert.assertEquals(i, ur.compareTo(usageRecord));
		}
	}
	
	@Test
	public void testCompareToDifferentForAddedProperties() throws Exception {
		UsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		UsageRecord ur = (UsageRecord) RecordUtility.getRecord(usageRecord.getResourceProperties());
		usageRecord.setResourceProperty(Integer.toString(1), 2);
		ur.setResourceProperty(Integer.toString(2), 2);
		Assert.assertEquals(1, usageRecord.compareTo(ur));
		Assert.assertEquals(1, ur.compareTo(usageRecord));
	}
	*/
	
	@Test
	public void testCompareToDifferentFromCreation() throws Exception {
		UsageRecord usageRecord = TestUsageRecord.createTestServiceUsageRecord();
		UsageRecord ur = TestUsageRecord.createTestServiceUsageRecord();
		Assert.assertEquals(1, usageRecord.compareTo(ur));
		Assert.assertEquals(1, ur.compareTo(usageRecord));
	}
	
}
