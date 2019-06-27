package org.gcube.common.workspacetaskexecutor.shared.exception;


/**
 * The Class TaskErrorException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 27, 2018
 */
public class TaskErrorException extends Exception {


	/**
	 *
	 */
	private static final long serialVersionUID = -5809192288746340819L;

	/**
	 * Instantiates a new item not synched.
	 */
	public TaskErrorException() {
		super();
	}

	/**
	 * Instantiates a new item not synched.
	 *
	 * @param arg0 the arg 0
	 */
	public TaskErrorException(String arg0) {
		super(arg0);
	}

}
