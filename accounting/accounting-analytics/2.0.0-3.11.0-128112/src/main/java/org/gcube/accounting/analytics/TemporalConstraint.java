/**
 * 
 */
package org.gcube.accounting.analytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class TemporalConstraint {
	
	private static final Logger logger = LoggerFactory.getLogger(TemporalConstraint.class);
	
	private static final String UTC_TIME_ZONE = "UTC";
	
	public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone(UTC_TIME_ZONE); 
	
	/**
	 * Valid Aggregation Mode
	 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
	 */
	public enum AggregationMode {
		YEARLY, MONTHLY, DAILY, HOURLY, MINUTELY, SECONDLY, MILLISECONDLY  
	}
	
	/**
	 * Used to map the Calendar constant to an enum value which has the same
	 * ordinal of {@link AggregationMode} 
	 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
	 */
	public enum CalendarEnum {
		YEAR(Calendar.YEAR),
		MONTH(Calendar.MONTH),
		DAY(Calendar.DAY_OF_MONTH),
		HOUR(Calendar.HOUR_OF_DAY),
		MINUTE(Calendar.MINUTE),
		SECOND(Calendar.SECOND),
		MILLISECOND(Calendar.MILLISECOND);
		
		private final int calendarValue;
		
		CalendarEnum(int calendarValue){
			this.calendarValue = calendarValue;
		}
		
		public int getCalendarValue(){
			return calendarValue;
		}
		
	};
	
	protected long startTime;
	protected long endTime;
	protected AggregationMode aggregationMode;
	
	/**
	 * @param startTime StartTime
	 * @param endTime End Time
	 * @param aggregationMode Aggregation Mode
	 */
	public TemporalConstraint(long startTime, long endTime, AggregationMode aggregationMode){
		this.startTime = startTime;
		this.endTime = endTime;
		this.aggregationMode = aggregationMode;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the aggregationMode
	 */
	public AggregationMode getAggregationMode() {
		return aggregationMode;
	}

	/**
	 * @param aggregationMode the aggregationMode to set
	 */
	public void setAggregationMode(AggregationMode aggregationMode) {
		this.aggregationMode = aggregationMode;
	}

	public static String timeInMillisToString(long timeInMillis){
		Date date = new Date(timeInMillis);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS z");
		simpleDateFormat.setTimeZone(DEFAULT_TIME_ZONE);
		return String.format("%s (%d millis)", simpleDateFormat.format(date), timeInMillis);
	}
	
	public static Calendar getAlignedCalendar(long millis, AggregationMode aggregationMode){
		Calendar alignedCalendar = Calendar.getInstance();
		alignedCalendar.setTimeZone(DEFAULT_TIME_ZONE);
		alignedCalendar.setTimeInMillis(millis);
		
		CalendarEnum[] calendarValues = CalendarEnum.values();

		for(int i=aggregationMode.ordinal()+1; i<calendarValues.length; i++){
			int calendarValue = calendarValues[i].getCalendarValue();

			if(calendarValue == Calendar.DAY_OF_MONTH){
				alignedCalendar.set(calendarValue, 1);
			} else{
				alignedCalendar.set(calendarValue, 0);
			}
			
		}

		return alignedCalendar;
	}
	
	public Calendar getAlignedStartTime(){
		return getAlignedCalendar(startTime, aggregationMode);
	}
	
	public Calendar getAlignedEndTime(){
		return getAlignedCalendar(endTime, aggregationMode);
	}
	
	public SortedSet<Calendar> getCalendarSequence(){
		SortedSet<Calendar> sequence = new TreeSet<>();
		
		CalendarEnum[] calendarValues = CalendarEnum.values();
		int calendarValue = calendarValues[aggregationMode.ordinal()].getCalendarValue();
		
		Calendar alignedStartTime = getAlignedStartTime();
		logger.trace("Aligned StartTime : {}", timeInMillisToString(alignedStartTime.getTimeInMillis()));
		
		Calendar alignedEndTime = getAlignedEndTime();
		long alignedEndTimeInMillis = alignedEndTime.getTimeInMillis();
		logger.trace("Aligned EndTime : {}", timeInMillisToString(alignedEndTime.getTimeInMillis()));
		
		Calendar progressTime = Calendar.getInstance();
		progressTime.setTimeZone(DEFAULT_TIME_ZONE);
		progressTime.setTimeInMillis(alignedStartTime.getTimeInMillis());
		
		while(progressTime.getTimeInMillis() <= alignedEndTimeInMillis){
			//logger.trace("Progress Time : {}", timeInMillisToString(progressTime.getTimeInMillis()));
			Calendar item = Calendar.getInstance();
			item.setTimeZone(DEFAULT_TIME_ZONE);
			item.setTimeInMillis(progressTime.getTimeInMillis());
			
			sequence.add(item);
			progressTime.add(calendarValue, 1);
		}
		
		return sequence;
	}
	
	
	public static List<String> getSequenceAsStringList(Collection<Calendar> sequence){
		List<String> stringSequence = new ArrayList<String>();
		for(Calendar calendar : sequence){
			stringSequence.add(timeInMillisToString(calendar.getTimeInMillis()));
		}
		return stringSequence;
	}

	@Override
	public String toString(){
		return String.format("StartTime : %s, EndTime : %s, Aggregated %s", 
				timeInMillisToString(startTime), 
				timeInMillisToString(endTime),
				aggregationMode.toString());
	}
	
}
