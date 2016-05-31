/**
 * 
 */
package gr.uoa.di.madgik.searchlibrary.operatorlibrary.google;

/**
 * @author paul
 *
 */
public class ResultLimitExceededException extends Exception {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 3538849989813638357L;

	public ResultLimitExceededException() {
		super("Reached maximum number of results.");
	}
}
