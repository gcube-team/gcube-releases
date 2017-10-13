package org.gcube.data.analysis.tabulardata.model;

import static org.gcube.data.analysis.tabulardata.expression.dsl.Texts.*;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Conditionals.*;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Arithmetics.*;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Casts.*;
import static org.gcube.data.analysis.tabulardata.expression.dsl.Types.*;
import static org.gcube.data.analysis.tabulardata.model.Converter.converter;
import static org.gcube.data.analysis.tabulardata.model.ValueFormat.format;

public class NumberConstant {

	public static ValueFormat STORAGE_FORMAT_DEC=format("TD NUMERIC","(^[\\+-]?\\d+((\\.|,)\\d+)?$|^\\d+(\\.\\d+)?[Ee][\\+-]?\\d+$|^\\s*$)", "[-]d*[[,|.]d+] (eg 7295.13)",
			converter(conditional(
						whenThen(matchesPosix(textCustomPlaceholder(), text("^\\s*$")), numeric(0)),
						whenThen(matchesPosix(textCustomPlaceholder(), text("^[\\+-]?\\d*((\\.|,)\\d+)?$")), toNumeric(regexprReplace(textCustomPlaceholder(), ",", "."))),
						//scientific notation case
						whenThen(matchesPosix(textCustomPlaceholder(), text("^\\d+(\\.+\\d+)?[Ee][\\+-]?\\d+$")), 
								mul(
									toNumeric(substring(textCustomPlaceholder() ,text("([^Ee]+)"))),
										exp(
											integer(10),
											toInt(substring(textCustomPlaceholder(), text("[Ee]([\\+-]?\\d+)$")))
											)
										)
								)
						)
					)
			);
	
	public static ValueFormat STORAGE_FORMAT_INT=format("TD INTEGER","(^[\\+-]?\\d+$|^\\s*$)", "[-]d* (eg 7295)",
			converter(regexprReplace(textCustomPlaceholder(), "^\\s*$" , "0")));
		
	public static ValueFormat US_FORMAT=format("US CURRENCY","^\\d{1,3}(,\\d{3})*(\\.\\d+)?$", "4,294,967,295.00",
			converter(regexprReplace(textCustomPlaceholder(), "," , ""))); 
	
	public static ValueFormat FRENCH_FORMAT=format("FRENCH CURRENCY","^\\d{1,3}(\\s\\d{3})*(,\\d+)?$", "4 294 967 295,000",
			converter(regexprReplace(
					regexprReplace(textCustomPlaceholder(), ",", "."),
					"\\s",""))); 
	
	public static ValueFormat GERMAN_FORMAT=format("GERMAN CURRENCY","^\\d{1,3}((\\s\\d{3})*\\.\\d{3})?(,\\d+)?$", "4 294 967.295,000",
			converter(regexprReplace(
					regexprReplace(
					regexprReplace(textCustomPlaceholder(), "\\.", ""),
					"\\s","")
					,",","."))); 

	public static ValueFormat ITALIAN_FORMAT = format("ITALIAN CURRENCY","^\\d{1,3}(\\.\\d{3})*(,\\d+)?$", "4.294.967.295,00",
			converter(regexprReplace(
					regexprReplace(textCustomPlaceholder(), "\\.", ""),
					",",".")));	
	
}
