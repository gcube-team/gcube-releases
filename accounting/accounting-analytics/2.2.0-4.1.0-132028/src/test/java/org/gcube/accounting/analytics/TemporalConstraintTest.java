/**
 * 
 */
package org.gcube.accounting.analytics;

import java.util.Calendar;
import java.util.Collection;

import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class TemporalConstraintTest {

	private static Logger logger = LoggerFactory.getLogger(TemporalConstraintTest.class);
	
	
	long testTime = new Long("1437481211396"); // 2015-07-21 12:20:11:396 UTC
	long[] results = new long[]{
			new Long("1420070400000"), // 2015-01-01 00:00:00:00 UTC
			new Long("1435708800000"), // 2015-07-01 00:00:00:00 UTC
			new Long("1437436800000"), // 2015-07-21 00:00:00:00 UTC
			new Long("1437480000000"), // 2015-07-21 12:00:00:00 UTC
			new Long("1437481200000"), // 2015-07-21 12:20:00:00 UTC
			new Long("1437481211000"), // 2015-07-21 12:20:11:00 UTC
			testTime  // 2015-07-21 12:20:11:396 UTC
	};
	
	@Test
	public void getAlignedCalendarTest(){
		logger.debug(TemporalConstraint.timeInMillisToString(testTime));
		for(AggregationMode aggregationMode : AggregationMode.values()){
			Calendar alignedCalendar = TemporalConstraint.getAlignedCalendar(testTime, aggregationMode);
			long alignedInMillis = alignedCalendar.getTimeInMillis();
			logger.debug("With AggregationMode {} The aligned value of {} is {}",
					aggregationMode,
					TemporalConstraint.timeInMillisToString(testTime),
					TemporalConstraint.timeInMillisToString(alignedInMillis));
			Assert.assertEquals(results[aggregationMode.ordinal()], alignedInMillis);
		}
		
	}
	
	@Test
	public void getCalendarSequenceTest(){
		Calendar endTime = Calendar.getInstance();
		
		Calendar startTime = Calendar.getInstance();
		// 3 days
		int[] timeShift = new int[]{
			3, // days
			24, // hour in a day
			60, // min per hour
			60, // sec per min
			1000 // millisec per sec
		};
		
		long shift = 1;
		for(int i=0; i<timeShift.length; i++){
			shift = shift * timeShift[i];
		}
		startTime.setTimeInMillis(endTime.getTimeInMillis()-(shift));
		
		int[] expectedSequenceSize = new int[AggregationMode.values().length];
		expectedSequenceSize[0] = 1; // AggregationMode.YEARLY
		expectedSequenceSize[1] = 1; // AggregationMode.MONTHLY
		
		for(int j=0; j<timeShift.length; j++){
			expectedSequenceSize[j+2] = expectedSequenceSize[j+1] * timeShift[j];
		};
		
		for(AggregationMode aggregationMode : AggregationMode.values()){
			TemporalConstraint temporalConstraint = new TemporalConstraint(startTime.getTimeInMillis(), endTime.getTimeInMillis(), aggregationMode);
			
			if(aggregationMode==AggregationMode.MILLISECONDLY){
				break;
			}
			
			Collection<Calendar> sequence = temporalConstraint.getCalendarSequence();
			if(aggregationMode.ordinal()<=AggregationMode.HOURLY.ordinal()){
				logger.debug("{} generate the following sequence (size {}) {}", 
						temporalConstraint, sequence.size(), 
						TemporalConstraint.getSequenceAsStringList(sequence));
			}else{
				logger.debug("{} generate asequence with size {}", 
						temporalConstraint, sequence.size());
			}
			
			int expected = expectedSequenceSize[aggregationMode.ordinal()];
			
			// Expected has 1 more value because the extremes are contained.
			// DAILY because the difference is 3 DAYS
			if(aggregationMode.ordinal()>=AggregationMode.DAILY.ordinal()){
				expected++;
			}
			
			Assert.assertEquals(expected, sequence.size());
		}
	}
	
	
	
}
