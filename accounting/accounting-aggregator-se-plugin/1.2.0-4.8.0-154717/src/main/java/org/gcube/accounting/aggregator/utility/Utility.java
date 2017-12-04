package org.gcube.accounting.aggregator.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.plugin.AccountingAggregatorPlugin;
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) 
 */
public class Utility {
	
	private static Logger logger = LoggerFactory.getLogger(Utility.class);
	
	public static String getCurrentContext() throws Exception {
		String token = SecurityTokenProvider.instance.get();
		return Constants.authorizationService().get(token).getContext();
	}
	
	
	public static String getHumanReadableDuration(long duration){
		return String.format("%d hours %02d minutes %02d seconds %03d milliseconds", 
				duration/(1000*60*60), 
				(duration/(1000*60))%60, 
				(duration/1000)%60, 
				(duration%1000));
	}
	
	
	public static void printLine(File file, String line) throws Exception {
		synchronized (file) {
			try (FileWriter fw = new FileWriter(file, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.println(line);
				out.flush();
			} catch (IOException e) {
				throw e;
			}
		}
	}
	
	public static TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
	
	public static DateFormat getUTCDateFormat(String pattern){
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setTimeZone(UTC_TIMEZONE);
		return dateFormat;
	}
	
	public static Calendar getUTCCalendarInstance(){
		return Calendar.getInstance(UTC_TIMEZONE);
	}
	
	private static final String LOCALE_FORMAT_PATTERN = "Z";
	private static final DateFormat LOCALE_DATE_FORMAT;
	
	static {
		LOCALE_DATE_FORMAT = new SimpleDateFormat(LOCALE_FORMAT_PATTERN);
	}
	
	public static String getPersistTimeParameter(int hour, int minute) {
		// Used from Clients. Not in UTC but in locale
		Calendar persistEndTime = Calendar.getInstance();
		persistEndTime.set(Calendar.HOUR_OF_DAY, hour);
		persistEndTime.set(Calendar.MINUTE, minute);
		
		String persistEndTimeParameter = AccountingAggregatorPlugin.PERSIST_TIME_DATE_FORMAT
				.format(persistEndTime.getTime());
		
		return persistEndTimeParameter;
	}
	
	public static Date getPersistTimeDate(String persistTimeString) throws ParseException{
		Date date = new Date();
		persistTimeString = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.format(
				date) + " " + persistTimeString + " " + LOCALE_DATE_FORMAT.format(date);

		// Local Date Format (not UTC)
		DateFormat dateFormat = new SimpleDateFormat(
				AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT_PATTERN + " "
						+ AccountingAggregatorPlugin.LOCAL_TIME_DATE_FORMAT_PATTERN);
		
		Date persistTime = dateFormat.parse(persistTimeString);
		
		return persistTime;
	}
	
	public static boolean isTimeElapsed(Calendar now, Date date) throws ParseException {
		try {
			boolean elapsed = now.getTime().after(date);
			logger.info("{} is {}elapsed.",
						AccountingAggregatorPlugin.LOCAL_TIME_DATE_FORMAT.format(date),elapsed? "" : "NOT ");
			
	
			return elapsed;
		}catch (Exception e) {
			logger.error("Unable to check if " + date.toString() + " is elapsed", e);
			throw e;
		}
	}
	
	public static Calendar getAggregationStartCalendar(int year, int month, int day){
		Calendar aggregationStartCalendar = getUTCCalendarInstance();
		aggregationStartCalendar.set(Calendar.YEAR, year);
		aggregationStartCalendar.set(Calendar.MONTH, month);
		aggregationStartCalendar.set(Calendar.DAY_OF_MONTH, day);
		aggregationStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
		aggregationStartCalendar.set(Calendar.MINUTE, 0);
		aggregationStartCalendar.set(Calendar.SECOND, 0);
		aggregationStartCalendar.set(Calendar.MILLISECOND, 0);
		
		logger.debug("{}", Constant.DEFAULT_DATE_FORMAT.format(aggregationStartCalendar.getTime()));
		
		return aggregationStartCalendar;
	}
	
	
	public static Date getEndDateFromStartDate(AggregationType aggregationType, Date aggregationStartDate, int offset) {
		Calendar aggregationEndDate = getUTCCalendarInstance();
		aggregationEndDate.setTimeInMillis(aggregationStartDate.getTime());
		aggregationEndDate.add(aggregationType.getCalendarField(), offset);
		return aggregationEndDate.getTime();
	}
	
	protected static ClientInfo getClientInfo() throws Exception {
		Caller caller = AuthorizationProvider.instance.get();
		if(caller!=null){
			return caller.getClient();
		}else{
			String token = SecurityTokenProvider.instance.get();
			AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
			return authorizationEntry.getClientInfo();
		}
	}
	
	public static String getUsername() throws Exception{
		try {
			ClientInfo clientInfo = getClientInfo();
			String clientId = clientInfo.getId();
			if (clientId != null && clientId.compareTo("") != 0) {
				return clientId;
			}
			throw new Exception("Username null or empty");
		} catch (Exception e) {
			logger.error("Unable to retrieve user.");
			throw new Exception("Unable to retrieve user.", e);
		}
	}
	
	
	public static File getMalformatedFile(File aggregateRecordsBackupFile){
		return new File(aggregateRecordsBackupFile.getParent(), aggregateRecordsBackupFile.getName().replaceAll("aggregated", "malformed"));
	}
}
