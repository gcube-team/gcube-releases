/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.util.HashSet;
import java.util.Set;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class AggregatedUsageRecordTest {
	public static Set<String> getExpectedRequiredFields(){
		Set<String> expectedRequiredFields = new HashSet<String>();
		expectedRequiredFields.add(AggregatedUsageRecord.AGGREGATED);
		expectedRequiredFields.add(AggregatedUsageRecord.END_TIME);
		expectedRequiredFields.add(AggregatedUsageRecord.START_TIME);
		expectedRequiredFields.add(AggregatedUsageRecord.OPERATION_COUNT);
		return expectedRequiredFields;
	}
}
