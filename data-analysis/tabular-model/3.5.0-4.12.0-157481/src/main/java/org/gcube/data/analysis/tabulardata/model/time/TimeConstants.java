package org.gcube.data.analysis.tabulardata.model.time;

import static org.gcube.data.analysis.tabulardata.expression.dsl.Texts.regexprReplace;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Types.textCustomPlaceholder;
import static org.gcube.data.analysis.tabulardata.model.Converter.converter;
import static org.gcube.data.analysis.tabulardata.model.ValueFormat.format;

import org.gcube.data.analysis.tabulardata.model.ValueFormat;

public class TimeConstants {
	
	public static final ValueFormat ISO_DATE_ANY_SEP = format("ISO DATE","^\\d\\d\\d\\d(-|/|\\.)(0?[1-9]|1[0-2])(-|/|\\.)(0?[1-9]|[1-2][0-9]|3[0-1])$",
			"yyyy[-|/|.]mm[-|/|.]dd (eg 1999-12-22)", converter(regexprReplace(
					regexprReplace(textCustomPlaceholder(),"(-|/|\\.)(\\d(?!\\d))","\\10\\2"),"(-|/|\\.)","-")));
		
	public static final ValueFormat EUROPEAN_DATE = format("EUROPEAN DATE","^(0?[1-9]|[1-2][0-9]|3[0-1])(-|/|\\.)(0?[1-9]|1[0-2])(-|/|\\.)\\d\\d\\d\\d$",
			"dd[-|/|.]mm[-|/|.]yyyy (eg 22/12/1999)", 
			converter(regexprReplace(
					regexprReplace(textCustomPlaceholder(),"(-|/|\\.|)(\\d(?!\\d))","\\10\\2")
					,"^(0[1-9]|[1-2][0-9]|3[0-1])(-|/|\\.)(0[1-9]|1[0-2])(-|/|\\.)(\\d\\d\\d\\d)$","\\5-\\3-\\1")));
	
	public static final ValueFormat US_DATE = format("US DATE","^(0[1-9]|1[0-2])(-|/|\\.)(0?[1-9]|[1-2][0-9]|3[0-1])(-|/|\\.)\\d\\d\\d\\d$",
			"mm[-|/|.]dd[-|/|.]yyyy (eg 12/22/1999)", 
			converter(regexprReplace(
					regexprReplace(textCustomPlaceholder(),"(-|/|\\.|)(\\d(?!\\d))","\\10\\2")
					,"^(0[1-9]|[1-2][0-9]|3[0-1])(-|/|\\.)(0[1-9]|[1-2][0-9]|3[0-1])(-|/|\\.)(\\d\\d\\d\\d)$","\\5-\\1-\\3")));

	
	public static final ValueFormat ISO_MONTH = format("ISO MONTH","^\\d\\d\\d\\d(-|/|\\.)(0?[1-9]|1[0-2])((-|/|\\.)(0[1-9]|[1-2][0-9]|3[0-1]))?$",
			"yyyy[-|/|.]mm[[-|/|.]dd] (eg 1999-12)", 
			converter(regexprReplace(
					regexprReplace(textCustomPlaceholder(),"(-|/|\\.)(\\d(?!\\d))","\\10\\2")
					,"^(\\d\\d\\d\\d)(-|/|\\.)(\\d\\d)*.$","\\1-\\3")));
		
	public static final ValueFormat ISO_YEAR = format("ISO YEAR","^\\d\\d\\d\\d(-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2][0-9]|3[0-1]))?$"
			,"yyyy[-mm[-dd]] (eg 1999)",
			converter(regexprReplace(textCustomPlaceholder(),"^(\\d\\d\\d\\d)*.$","\\1")));
	
	public static final ValueFormat ISO_QUARTER = format("ISO QUARTER","^\\d\\d\\d\\d-Q[1-4]$"
			,"yyyy-Qq (eg 1999-Q1)");
	
	public static final ValueFormat DECADE_FORMAT = format("DECADE","^\\d\\d\\d$","yyy (eg 199)");
	
	public static final ValueFormat DECADE_LITERAL_FORMAT = format("DECADE LITERAL","^\\d\\d\\d0s$","yyy (eg 1990s)", 
			converter(regexprReplace(textCustomPlaceholder(),"^(\\d\\d\\d)0s$","\\1")));
		
	public static final ValueFormat CENTURY_FORMAT = format("CENTURY","^\\d\\d$","yy (eg 19)");
}
