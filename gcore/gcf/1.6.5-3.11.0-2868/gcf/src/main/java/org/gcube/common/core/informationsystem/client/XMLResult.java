package org.gcube.common.core.informationsystem.client;

import java.util.List;


/**
 * Models arbitrary XML results and exposes a simple XPath-based query interface over them.
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR)
 *
 */
public interface XMLResult {
	
	/**
	 * Returns the result serialisation.
	 * @return the serialisation.
	 */
	public String toString();
	
	/**
	 * Returns the values of an XPath query against the result.
	 * @param xpath the XPath expression.
	 * @return the values.
	 */
	public List<String> evaluate(String xpath) throws ISResultEvaluationException;
		
	/**
	 * An exception returned during xpath evaluation.
	 * @author Fabio Simeoni (University of Strathclyde) 
	 **/
	public class ISResultEvaluationException extends Exception {
		/** Serialisation ID */
		private static final long serialVersionUID = 1L;
		/** Creates an instance with a given cause. */
		public ISResultEvaluationException(Exception cause) {super(cause);}
	}
	
	/**
	 * An exception returned during result initialisation.
	 * @author Fabio Simeoni (University of Strathclyde) 
	 **/
	public class ISResultInitialisationException extends Exception {
		/** Serialisation ID */
		private static final long serialVersionUID = 1L;
		/** Creates an instance with a given cause. */
		public ISResultInitialisationException(Exception cause) {super(cause);}
	}
}
