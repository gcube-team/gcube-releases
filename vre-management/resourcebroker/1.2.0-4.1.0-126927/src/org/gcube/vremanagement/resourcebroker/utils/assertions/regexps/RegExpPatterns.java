/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software 
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors 
 * "as is" and no expressed or 
 * implied warranty is given for its use, quality or fitness for a 
 * particular case.
 ****************************************************************************
 * Filename: RegExpPatterns.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a> 
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.assertions.regexps;


/**
 * A set of generic patterns based on regular expressions.
 * Usually used for Assertions on the parameters.
 *
 * 	<pre>
 * Example:
 *
 * 	// Checks if the id is compliant to the ClassName
 * 	// regular expression
 * 	Assertion.paramCheck.validate(
 *		id.trim().matches(RegExpPatterns.REClassName),
 *		new ParamException ("invalid format " + id));
 * 	</pre>
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class RegExpPatterns {
	/** The object (variable) names are assumed to start with a character
	 * followed by any alpha-numeric symbol.
	 */
	public static final String ObjectName = "[a-z[A-Z]]+[a-zA-Z0-9]*";
	/**
	 * Regular expression for class names in the form:
	 * <pre>
	 * 	<i>package.subpackage.ClassName</i>
	 * </pre>
	 */
	public static final String REClassName =
		"[a-z[A-Z]]+[a-zA-Z0-9]*(\\.[a-z[A-Z]]+[a-zA-Z0-9]*)*";
	/**
	 * Convention on the file names.
	 * Here assumed to use "/" as path separator.
	 * Valid names are:
	 * <pre>
	 * 	<i>folder/subfolder/file.ext</i>
	 * </pre>
	 */
	public static final String REFileName =
		"[a-z[A-Z]]+[a-zA-Z0-9]*(\\/[a-z[A-Z]]+[a-zA-Z0-9]*)*(\\.[a-z[A-Z]]+)";
}

