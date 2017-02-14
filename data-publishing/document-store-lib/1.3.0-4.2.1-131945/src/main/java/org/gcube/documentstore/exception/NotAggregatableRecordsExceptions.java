/**
 * 
 */
package org.gcube.documentstore.exception;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class NotAggregatableRecordsExceptions extends Exception {

	/**
	 * Generated serial Version UID
	 */
	private static final long serialVersionUID = -1477792189431118048L;

	public NotAggregatableRecordsExceptions() {
		super();
	}

	public NotAggregatableRecordsExceptions(String message) {
		super(message);
	}
	
	public NotAggregatableRecordsExceptions(Throwable cause) {
		super(cause);
	}
	
	public NotAggregatableRecordsExceptions(String message, Throwable cause) {
		super(message, cause);
	}
}
