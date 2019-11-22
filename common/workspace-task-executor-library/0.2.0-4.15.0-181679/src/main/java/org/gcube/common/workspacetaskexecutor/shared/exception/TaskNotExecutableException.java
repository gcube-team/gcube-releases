package org.gcube.common.workspacetaskexecutor.shared.exception;


/**
 * The Class TaskNotExecutableException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 27, 2018
 */
public class TaskNotExecutableException extends Exception {


	/**
	 *
	 */
	private static final long serialVersionUID = 1266304637564891443L;

	/**
	 * Instantiates a new item not synched.
	 */
	public TaskNotExecutableException() {
		super();
	}

	/**
	 * Instantiates a new item not synched.
	 *
	 * @param arg0 the arg 0
	 */
	public TaskNotExecutableException(String arg0) {
		super(arg0);
	}

}
