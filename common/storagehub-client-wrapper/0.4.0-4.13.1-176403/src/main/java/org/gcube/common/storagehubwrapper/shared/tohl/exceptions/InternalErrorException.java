/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;


/**
 * The Class InternalErrorException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class InternalErrorException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -8885298303989967692L;


	/**
	 * Instantiates a new internal error exception.
	 *
	 * @param arg0 the arg0
	 */
	public InternalErrorException(String arg0) {
		super(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {

		// TODO Auto-generated method stub
		return super.toString();
	}
}
