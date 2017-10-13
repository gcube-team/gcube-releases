package org.gcube.accounting.aggregator.utility;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class Constant {

	public static final int NUM_RETRY = 6;
	
	public static final String HOME_SYSTEM_PROPERTY = "user.home";
	public static final File ROOT_DIRECTORY;
	
	public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS Z";
	public static final DateFormat DEFAULT_DATE_FORMAT;
	
	
	public static final int CALENDAR_FIELD_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED;
	public static final int UNIT_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED;
	
	static {
		String rootDirectoryPath = System.getProperty(Constant.HOME_SYSTEM_PROPERTY);
		ROOT_DIRECTORY = new File(rootDirectoryPath);
		
		DEFAULT_DATE_FORMAT = Utility.getUTCDateFormat(DATETIME_PATTERN);
		
		CALENDAR_FIELD_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED = Calendar.HOUR_OF_DAY;
		UNIT_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED = 8;
	}

}
