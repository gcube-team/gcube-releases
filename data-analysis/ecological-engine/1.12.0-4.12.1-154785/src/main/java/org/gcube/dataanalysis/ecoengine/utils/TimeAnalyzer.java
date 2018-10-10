package org.gcube.dataanalysis.ecoengine.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.gcube.contentmanagement.graphtools.utils.DateGuesser;

public class TimeAnalyzer {

	
	private String pattern = "";
	
	public String getPattern(){
		return pattern;
	}
	
	public Date string2Date(String time) {
		try {
			time = time.replace("time:", "");
			String timepattern = DateGuesser.getPattern(time);
			SimpleDateFormat sdf = new SimpleDateFormat(timepattern, Locale.ENGLISH);
			Date timedate = (Date) sdf.parse(time);

			if (timepattern.equals("s")) {
				timepattern = "HH:mm:ss:SS";
				sdf = new SimpleDateFormat(timepattern, Locale.ENGLISH);
				timedate = (Date) sdf.parse(time);
			}
			pattern=timepattern;
			return timedate;
		} catch (Exception e) {
			return new Date(System.currentTimeMillis());
		}
	}
	
	public static int getTimeIndexInTimeRange(Date maxdate,Date mindate,Date currentdate, int chunks){
		
		long timemax = maxdate.getTime(); 
		long timemin = mindate.getTime();
		long timecurr = currentdate.getTime();
		
		return (int)Math.round((double)(timecurr-timemin)*(double)chunks/(double)(timemax-timemin));
		
	}
	
	
}
