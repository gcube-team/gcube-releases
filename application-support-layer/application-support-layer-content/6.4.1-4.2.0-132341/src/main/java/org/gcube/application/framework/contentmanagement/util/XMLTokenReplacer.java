package org.gcube.application.framework.contentmanagement.util;


/**
 * @author UoA
 * @version 0.2.0
 *
 */
public class XMLTokenReplacer {

	/**
	 * 
	 * @param token to be replaced
	 * @param replacer to replace
	 * @param str to be processed
	 * @return the processed string
	 */
	private static String replaceXMLTokens(String token, char replacer, String str)
	{
		StringBuffer sb = new StringBuffer(str);
		int index = sb.indexOf(token);
		if(index == -1)
			return str;
		while(index != -1){
			sb.replace(index, index+token.length(), new Character(replacer).toString());
			index = sb.indexOf(token);
		}
		
		return sb.toString();
	}
	
	/**
	 * Replace all non-XML to XML tokens
	 * @param str string to be processed
	 * @return processed string
	 */
	public static String XMLResolve(String str)
	{
		String LT = "&lt;";
		String GT = "&gt;";
		String APOS = "&apos;";
		String AMP = "&amp;";
		String QUOT = "&quot;";
		String NULL = "&#x0;";
		String SLASH_R = "&#x0D;";
		char LTReplace = '<';
		char GTReplace = '>';
		char APOSReplace = '\'';
		char AMPReplace = '&';
		char QUOTReplace = '\"';
		char NULLReplace = '\0';
		char SLASH_RReplace = '\r';
		
		str = replaceXMLTokens(LT, LTReplace, str);
		str = replaceXMLTokens(GT, GTReplace, str);
		str = replaceXMLTokens(APOS, APOSReplace, str);
		str = replaceXMLTokens(QUOT, QUOTReplace, str);
		str = replaceXMLTokens(NULL, NULLReplace, str);
		str = replaceXMLTokens(SLASH_R, SLASH_RReplace, str);
		//the & last
		//because a &amp;gt; will become > instead of &gt;
		str = replaceXMLTokens(AMP, AMPReplace, str);
		
		

		return str;
	}
	
	/**
	 * Replace all XML to non-XML tokens
	 * @param str string gto be processed
	 * @return processed string
	 */
	public static String XMLUnresolve(String str)
	{
		String AMP = "&amp;";
		String LT = "&lt;";
		String GT = "&gt;";
		String APOS = "&apos;";
		String QUOT = "&quot;";
		String NULL = "&#x0;";
		String SLASH_R = "&#x0D;";
		char AMPReplace = '&';
		char LTReplace = '<';
		char GTReplace = '>';
		char APOSReplace = '\'';
		char QUOTReplace = '\"';
		char NULLReplace = '\0';
		char SLASH_RReplace = '\r';
		
		//first the &
		//because if later a &quot; will become &amp;quot;
		str = replace2XMLTokens(AMP, AMPReplace, str);
		str = replace2XMLTokens(LT, LTReplace, str);
		str = replace2XMLTokens(GT, GTReplace, str);
		str = replace2XMLTokens(APOS, APOSReplace, str);
		str = replace2XMLTokens(QUOT, QUOTReplace, str);
		str = replace2XMLTokens(NULL, NULLReplace, str);
		str = replace2XMLTokens(SLASH_R, SLASH_RReplace, str);
		
		return str;
	}
	
	/**
	 * 
	 * @param replacer to replace
	 * @param original to be replaced
	 * @param str string to be processed
	 * @return processed string
	 */
	private static String replace2XMLTokens(String replacer, char original, String str)
	{
		StringBuffer sb = new StringBuffer(str);
		int index = sb.indexOf(new Character(original).toString());
		if(index == -1)
			return str;
		while(index != -1){
			sb.replace(index, index+1, replacer);
			index = sb.indexOf(new Character(original).toString(), index+1);
		}
		
		return sb.toString();
	}
	
	/**
	 * @param args main arguments
	 */
	public static void main(String[] args) {

		System.out.println(args[0]);
		System.out.println(XMLUnresolve(args[0]));
		System.out.println(XMLResolve(XMLUnresolve(args[0])));
	}

}

