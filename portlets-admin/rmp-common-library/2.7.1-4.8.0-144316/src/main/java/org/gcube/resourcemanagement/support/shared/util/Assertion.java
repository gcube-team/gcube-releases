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
 * Filename: Assertion.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.util;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * General purpose assertion handler.
 * Assertion can be generalized to check a boolean expression and
 * to raise an exception in correspondence to a failure happening
 * during checking.
 * <pre>
 * <b>Example:</b>
 *
 *     <b>Assertion</b>&lt;<i>TheExceptionType</i>&gt; assertion = new Assertion&lt;ParamException&gt; ();
 *     assertion.<b>validate</b> (param != null, new <i>TheExceptionType</i>("invalid parameter null"));
 *
 * <b>or</b>, in a more compact form:
 *    <i>// The exception to throw in case of failure
 *    // during the evaluation of the expected condition</i>
 *    new <b>Assertion</b>&lt;<i>TheExceptionType</i>&gt;().<b>validate</b>(
 *    	i>5,                                                     <i>// The expected boolean <b>condition</b></i>
 *    	new <i>TheExceptionType</i>("Parameter must be greater than 5")); <i>//The <b>error</b> message</i>
 *
 * </pre>
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class Assertion <T extends Throwable> implements Serializable, IsSerializable {
	private static final long serialVersionUID = -2007903339251667541L;

	/**
	 * Makes an assertion and if the expression evaluation fails, throws an
	 * exception of type T.
	 * <pre>
	 * Example:
	 * 	new Assertion&lt;MyException&gt;().validate(whatExpected, new MyException("guard failed"));
	 * </pre>
	 * @param assertion the boolean expression to evaluate
	 * @param exc the exception to throw if the condition does not hold
	 * @throws T the exception extending {@link java.lang.Throwable}
	 */
	public final void validate(final boolean assertion, final T exc)
	throws T {
		if (!assertion) {
			throw exc;
		}
	}
}

