package gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * The CompareTokens class exports a single method called compare.
 * It is designed to be used in a static context, meaning that no CompareTokens object is created.
 * The comparisons made for string are case insensitive
 * 
 * @author UoA
 */
public class CompareTokens {
	/**
	 * The compare operation returned equal
	 */
	public static final short COMPARE_EQUAL = 0;
	/**
	 * The compare operation returned greater
	 */
	public static final short COMPARE_GREATER = 1;
	/**
	 * The compare operation returned lower
	 */
	public static final short COMPARE_LOWER = -1;
	
	/**
	 * Compare in ascending order
	 */
	public static final short ASCENDING_ORDER = 0;
	/**
	 * Compare in descending order
	 */
	public static final short DESCENDING_ORDER = 1;
	
	private static ComparisonMode mode = null;
	
	/**
	 * The isNumeric method checks if its string paramater can be translated into a number.
	 * If the string is consisted of digits and a single dot, then it is numeric; else it is not.
	 * algorithm complexity: 0(n), n: string length
	 * @param alphanum The string being checked
	 * @return True if the string parameter can be translated into a number; otherwise False 
	 */
	private static boolean isNumeric(String alphanum) {
	   int i=0;
		boolean dotFound = false;
		boolean isNumeric = true;

		if(alphanum.length() == 0)
			return false;
		
		int start = 0;
		if(alphanum.charAt(0) == '-' || alphanum.charAt(0) == '+')
			start++;
			
		for(i=start;i<alphanum.length();i++)
		{
			if(Character.isDigit(alphanum.charAt(i)))
				continue;
			if(alphanum.charAt(i) == '.' && dotFound==false)
			{
				dotFound=true;
				continue;
			}
			isNumeric = false;
			break;
		}

		return isNumeric;
	}
	
//	private static boolean isNumeric(String alphanum) {
//		try{  
//			Double.parseDouble(alphanum);  
//			return true;  
//		} catch(NumberFormatException e) {  
//			return false;  
//		} 
//	}

	/**
	 * The isInteger method checks if its string paramater can be translated into an integer number
	 * If the  string is actually a numeric and does not contain any dot, then it is an integer number,
	 * Else it is not.
	 * Algorithm complexity: O(n), n: string length
	 * @param num The string being checked 
	 * @return True if the string parameter can be translated into an integer number; otherwise False 
	 */
	private static boolean isInteger(String num)
	{
		if(isNumeric(num) && num.indexOf('.')==-1)
			return true;
		return false;
	}
	
//	private static boolean isInteger(String num) {
//		try{  
//			Integer.parseInt(num);  
//			return true;  
//		}catch(NumberFormatException e) {  
//			return false;  
//		} 
//	}
	
	/**
	 * The isDate method determines whether a dateStr is actually a date, or not
	 * @param dateStr the date to check
	 * @return True if the string can be translated into a date; False, otherwise
	 */
	private static boolean isDate(String dateStr)
	{
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);

		try{
			df.parse(dateStr);
		}catch(ParseException e){
			return false;
		}
		
		return true;
	}

	/**
	 * The Str2Date method translates a string into its corresponding date representation.
	 * The adopted format is the US one: mm/dd/yyyy OR mm/dd/yy
	 * @param dateStr The date convrt
	 * @return The Date object which corresponds to the string argument
	 * @throws ParseException A parsing error occured
	 */
	public static Date Str2Date(String dateStr) throws ParseException
	{
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);

		Date myDate = null;

		try{
			myDate = df.parse(dateStr);
		}catch(ParseException e){
			throw new ParseException(e.toString(), e.getErrorOffset());
		}
		return myDate;
	}
	
	/**
	 * The compare method compares two tokens. If the tokens are plain strings, then it employs
	 * the compareToIgnoreCase method of the string object. If the tokens are numerics, then it translates them
	 * into longs or doubles and compares them, employing the usual operators ==, >, <.
	 * We use longs to handle integer types (short, integer, long) and doubles to handle floating point types (float, double)
	 * @param token1 First token to be compared
	 * @param token2 Second token to be compared
	 * @return <code>0 if token1==token2, 1 if token1&gt;token2, -1 if token1&lt;token2</code>
	 * @throws InvalidTokenFormatComparison tokens where not valid
	 */
	public static int compare(String token1, String token2) throws InvalidTokenFormatComparison
	{
		token1=token1.trim();
		token2=token2.trim();
		try{
			if(!isNumeric(token1) && !isDate(token1)){
				if(token1.compareToIgnoreCase(token2)>0) return COMPARE_GREATER;
				if(token1.compareToIgnoreCase(token2)<0) return COMPARE_LOWER;
				return COMPARE_EQUAL;
			}
			else if(!isNumeric(token2) && !isDate(token2)){
				if(token1.compareToIgnoreCase(token2)>0) return COMPARE_GREATER;
				if(token1.compareToIgnoreCase(token2)<0) return COMPARE_LOWER;
				return COMPARE_EQUAL;
			}
			int compareResult=token1.compareToIgnoreCase(token2);
	
			// if token1 cannot be compared with token2, then throw an InvalidTokenFormatComparison exception
			if(isNumeric(token1) != isNumeric(token2))
			{
				throw new InvalidTokenFormatComparison(token1, token2);
			}

			// if the two tokens are numerics, then cast them to either longs or doubles and compare them with the ==,<,> operators
			if(isNumeric(token1))
			{
				Long LongToken1 = null;
				Long LongToken2 = null;
				Double DoubleToken1 = null;
				Double DoubleToken2 = null;
	
				long longtok1 = Long.MAX_VALUE;
				long longtok2 = Long.MAX_VALUE;
				double doubletok1 = Double.NaN;
				double doubletok2 = Double.NaN;
	
				if(isInteger(token2))
				{
					LongToken2 = Long.valueOf(token2);
					longtok2 = LongToken2.longValue();
				}
				else
				{
					DoubleToken2 = Double.valueOf(token2);
					doubletok2 = DoubleToken2.doubleValue();
				}
	
				if(isInteger(token1))
				{
					LongToken1 = Long.valueOf(token1);
					longtok1 = LongToken1.longValue();
	
					if(LongToken2 == null)
					{
						if(longtok1 == doubletok2)
							compareResult=COMPARE_EQUAL;
						else if(longtok1 > doubletok2)
							compareResult=COMPARE_GREATER;
						else
							compareResult=COMPARE_LOWER;
					}
					else
						compareResult = LongToken1.compareTo(LongToken2);
				}
				else
				{
					DoubleToken1 = Double.valueOf(token1);
					doubletok1 = DoubleToken1.doubleValue();
	
					if(DoubleToken2 == null)
					{
						if(longtok2 == doubletok1)
							compareResult=COMPARE_EQUAL;
						else if(longtok2 > doubletok1)
							compareResult=COMPARE_LOWER;
						else
							compareResult=COMPARE_GREATER;
					}
					else
						compareResult = DoubleToken1.compareTo(DoubleToken2);
				}
	
			}
			// else if the two tokens are dates then translate them into Date objectsand call the compareTo method of the Date class  
			else if(isDate(token1) && isDate(token2))
			{
				Date myDate1 = null;
				Date myDate2 = null;
				
				try{
					myDate1 = Str2Date(token1);
					myDate2 = Str2Date(token2);
				}catch(ParseException e){
					throw new InvalidTokenFormatComparison("Unexpected ParseError in Date parsing\n" + e.toString());
				}
				compareResult = myDate1.compareTo(myDate2);

			}
			// else just use the compareTo method of the String object
			else
				compareResult = token1.compareTo(token2);
				
			if(compareResult < 0)
				compareResult = COMPARE_LOWER;
			else if(compareResult > 0)
				compareResult = COMPARE_GREATER;
			return compareResult;
		}catch(Exception e){
			try{
				if(token1.compareToIgnoreCase(token2)<0){
					return COMPARE_LOWER;
				}
				if(token1.compareToIgnoreCase(token2)>0){
					return COMPARE_GREATER;
				}
				return COMPARE_EQUAL;
			}catch(Exception ee){
				throw new InvalidTokenFormatComparison();
			}
		}
	}
	/**
	 * 
	 * @param token1 First token to be compared
	 * @param token2 Second token to be compared
	 * @return the mode of comparison
	 * @throws InvalidTokenFormatComparison if tokens were not valid
	 */
	public static ComparisonMode getMode(String token1, String token2) throws InvalidTokenFormatComparison {
		
		if(token1 == null || token2 == null)
			return ComparisonMode.COMPARE_STRINGS;
		
		token1 = token1.trim();
		token2 = token2.trim();
		
		if( (!isNumeric(token1) && !isDate(token1)) || (!isNumeric(token2) && !isDate(token2)) )
			return ComparisonMode.COMPARE_STRINGS;
		
		if(isNumeric(token1) != isNumeric(token2))
			throw new InvalidTokenFormatComparison(token1, token2);
		
		if(isDate(token1) && isDate(token2))
			return ComparisonMode.COMPARE_DATES;
		
		if(isNumeric(token1)) {
			if(isInteger(token1)) { 
				if(isInteger(token2))
					return ComparisonMode.COMPARE_LONGINTS;
				return ComparisonMode.COMPARE_LONGINT_DOUBLE;
			}
			else {
				if(isInteger(token2))
					return ComparisonMode.COMPARE_DOUBLE_LONGINT;
				else if(isNumeric(token2))
					return ComparisonMode.COMPARE_DOUBLES;
			}
		}
		
		return ComparisonMode.COMPARE_STRINGS;
	}
	
	/**
	 * Gets the stored mode of comparison
	 * @return the mode of comparison
	 * @throws Exception if mode is not set
	 */
	public static ComparisonMode getMode() throws Exception {
		if(mode == null)
			throw new Exception("Mode not set");
		return mode;
	}
	
	/**
	 * Sets a new value to comparison mode
	 * @param mode New comparison mode
	 */
	public static void setMode(ComparisonMode mode) {
		CompareTokens.mode = mode;
	}
	
	/**
	 * Updates the mode of comparison according to the token parameter.
	 * If mode is not already initialized, it is done so according to the type of token.
	 * The most general mode of comparison is considered to be string comparison so if mode
	 * is already set to string comparison, it remains unchanged.
	 * If mode is set to string comparison and token is not of date format, mode is updated to string comparison,
	 * else it remains unchanged.
	 * Likewise, if mode is set to double comparison and token is not of numeric format, mode is updated to string
	 * comparison, else it remains unchanged.
	 * Finally, if mode is set to long integer comparison, it is updated to double comparison if
	 * token is of numeric (but not integral) type, to string comparison if token is not of numeric type
	 * or remains unchanged if token is of integral type.
	 * @param token token used to update mode
	 * @return the updated ComparisonMode
	 * @throws InvalidTokenFormatComparison tokens where not valid
	 */
	public static ComparisonMode updateMode(String token) throws InvalidTokenFormatComparison {
		
		if( mode == null ) {
			if(isDate(token))
				return mode = ComparisonMode.COMPARE_DATES;
			
			if(isNumeric(token)) {
				if(isInteger(token))
					return mode = ComparisonMode.COMPARE_LONGINTS;
				return mode = ComparisonMode.COMPARE_DOUBLES;
			}
			
			return mode = ComparisonMode.COMPARE_STRINGS;
		}
		
		switch(mode) {
		case COMPARE_STRINGS:
			return mode;
		case COMPARE_DATES:
			if(!isDate(token))
				return mode = ComparisonMode.COMPARE_STRINGS;
			return mode;
		case COMPARE_LONGINTS:
			if(isNumeric(token)) {
				if(!isInteger(token))
					return mode = ComparisonMode.COMPARE_DOUBLES;
				return mode;
			}
				return mode = ComparisonMode.COMPARE_STRINGS;
		case COMPARE_DOUBLES:
			if(!isNumeric(token))
				return mode = ComparisonMode.COMPARE_STRINGS;
			return mode;
		}
		return mode = ComparisonMode.COMPARE_STRINGS;
	}
	
	
	/**
	 * This version of the compare method compares two tokens. Depending on mode, if the tokens are plain strings, it employs
	 * the compareToIgnoreCase method of the string object. If one or both of the tokens are doubles, then it translates them
	 * into doubles and compares them, employing the usual operators ==, >, <. If both of the tokens are integers, then it
	 * translates them into longs and compares them.
	 * We use longs to handle integer types (short, integer, long) and doubles to handle floating point types (float, double)
	 * @param token1 First token to be compared
	 * @param token2 Second token to be compared
	 * @param mode The mode of comparison, one of <code>COMPARE_INTS, COMPARE_DOUBLES</code> and <code>COMPARE_STRINGS</code>
	 * @return <code>0 if token1==token2, 1 if token1&gt;token2, -1 if token1&lt;token2</code>
	 * @throws InvalidTokenFormatComparison tokens where not valid
	 */
	public static int compare(String token1, String token2, ComparisonMode mode) throws InvalidTokenFormatComparison {
	
		token1=token1.trim();
		token2=token2.trim();
		
		int compareResult;
		switch(mode) {
		
		case COMPARE_LONGINTS:
			try {
				long longtok1 = Long.parseLong(token1);			
				long longtok2 = Long.parseLong(token2);
				if(longtok1 == longtok2)
					return COMPARE_EQUAL;
				if(longtok1 < longtok2)
					return COMPARE_LOWER;
				return COMPARE_GREATER;
			}catch(Exception e) {
				throw new InvalidTokenFormatComparison(token1, token2);
			}
		
		case COMPARE_LONGINT_DOUBLE:
			try {
				long longtok1 = Long.parseLong(token1);
				double doubletok2 = Double.parseDouble(token2);
				if(longtok1 == doubletok2)
					return COMPARE_EQUAL;
				if(longtok1 < doubletok2)
					return COMPARE_LOWER;
				return COMPARE_GREATER;
			}catch(NumberFormatException e) {
				throw new InvalidTokenFormatComparison(token1, token2);
			}
			
		case COMPARE_DOUBLE_LONGINT:
			try {
				double doubletok1 = Double.parseDouble(token1);
				long longtok2 = Long.parseLong(token2);
				if(doubletok1 == longtok2)
					return COMPARE_EQUAL;
				if(doubletok1 < longtok2)
					return COMPARE_LOWER;
				return COMPARE_GREATER;
			}catch(NumberFormatException e) {
				throw new InvalidTokenFormatComparison(token1, token2);
			}
			
		case COMPARE_DOUBLES:
			try {
				double doubletok1 = Double.parseDouble(token1);
				double doubletok2 = Double.parseDouble(token2);
				if(doubletok1 == doubletok2)
					return COMPARE_EQUAL;
				if(doubletok1 < doubletok2)
					return COMPARE_LOWER;
				return COMPARE_GREATER;
			}catch(NumberFormatException e) {
				throw new InvalidTokenFormatComparison(token1, token2);
			}
			
		case COMPARE_STRINGS:
			compareResult = token1.compareToIgnoreCase(token2);
			if(compareResult > 0) 
				return COMPARE_GREATER;
			if(compareResult < 0) 
				return COMPARE_LOWER;
			return COMPARE_EQUAL;
			
		case COMPARE_DATES:
			Date Date1 = null;
			Date Date2 = null;
			try{
				Date1 = Str2Date(token1);
				Date2 = Str2Date(token2);
			}catch(ParseException e){
				throw new InvalidTokenFormatComparison("Unexpected ParseError in Date parsing\n" + e.toString());
			}
			compareResult = Date1.compareTo(Date2);
			if(compareResult < 0)
				return COMPARE_LOWER;
			if(compareResult > 0)
				return COMPARE_GREATER;
			return COMPARE_EQUAL;
				
			default:
				throw new InvalidTokenFormatComparison(token1, token2);
		}
		
	}
	
}

		
