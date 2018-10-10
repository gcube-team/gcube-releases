package org.gcube.common.workspacetaskexecutor.shared.exception;



/**
 * The Class ItemNotExecutableException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 27, 2018
 */
public class ItemNotExecutableException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3965350221961538838L;

	/**
	 * Instantiates a new item not synched.
	 */
	public ItemNotExecutableException() {
		super();
	}

	/**
	 * Instantiates a new item not synched.
	 *
	 * @param arg0 the arg 0
	 */
	public ItemNotExecutableException(String arg0) {
		super(arg0);
	}

}
