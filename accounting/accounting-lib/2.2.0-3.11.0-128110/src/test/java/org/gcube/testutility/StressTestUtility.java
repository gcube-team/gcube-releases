/**
 * 
 */
package org.gcube.testutility;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class StressTestUtility {

	private static final Logger logger = LoggerFactory.getLogger(StressTestUtility.class);
	
	public final static int DEFAULT_NUMBER_OF_RECORDS = 3000;
	
	public static void stressTest(TestOperation operation) throws Exception {
		stressTest(operation, DEFAULT_NUMBER_OF_RECORDS);
	}
	
	public static void stressTest(TestOperation operation, int runs) throws Exception {
		Calendar startTestTime = new GregorianCalendar();
		for(int i=0; i< runs; i++){
			operation.operate(i);
		}
		Calendar stopTestTime = new GregorianCalendar();
		double startMillis = startTestTime.getTimeInMillis();
		double stopMillis = stopTestTime.getTimeInMillis();
		double duration = stopMillis - startMillis;
		double average =  (duration/runs);
		logger.debug("Duration (in millisec) : " + duration);
		logger.debug("Average (in millisec) : " + average);
	}
}
