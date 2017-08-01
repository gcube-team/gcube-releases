/**
 * 
 */
package org.gcube.accounting.analytics.exception;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ValueException extends Exception {

	/**
	 * Generated serial Version UID
	 */
	private static final long serialVersionUID = -327144230654860518L;

	/**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
	public ValueException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause 
	 * is not initialized, and may subsequently be initialized by a call to 
	 * initCause.
	 * @param message the detail message. The detail message is saved for later 
	 * retrieval by the getMessage() method.
	 */
	public ValueException(String message) {
		super(message);
	}

	 /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public ValueException(Throwable cause) {
		super(cause);
	}
	
	/**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public ValueException(String message, Throwable cause) {
		super(message, cause);
	}
}
