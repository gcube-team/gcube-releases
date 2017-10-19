/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.shared;

/**
 * @author ceras
 *
 */
public class StringUtil {

	public static String getCapitalWords(String string) {
		String ris = "";
			
		boolean precUnderscore = true;
		for (int i=0; i<string.length(); i++) {
			char c = string.charAt(i);
			
			if (c == '_') {
				precUnderscore = true;
				ris += " ";
			} else {
				ris += (precUnderscore ? Character.toUpperCase(c) : Character.toLowerCase(c));
				if (precUnderscore == true)
					precUnderscore = false;
			}
		}
		return ris;
	}

	public static String clean(String string) {
		return (string == null ? "" : string);
	}
}
