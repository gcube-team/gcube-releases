package org.gcube.portlets.user.td.expressionwidget.shared.replace;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum ReplaceType {
	Value("Value"),
	ColumnValue("Column Value"),
	Upper("Upper"),
	Lower("Lower"),
	Trim("Trim"),
	MD5("MD5"),
	SubstringByRegex("Substring by Regex"),
	SubstringByIndex("Substring by Index"),
	SubstringByCharSeq("Substring by Char Seq."),
	TextReplaceMatchingRegex("Replace Matching Regex"),
	Concat("Concat"),
	Addition("Addition"),
	Subtraction("Subtraction"),
	Modulus("Modulus"),
	Multiplication("Multiplication"),
	Division("Division");
	
	

	/**
	 * @param text
	 */
	private ReplaceType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}

}
