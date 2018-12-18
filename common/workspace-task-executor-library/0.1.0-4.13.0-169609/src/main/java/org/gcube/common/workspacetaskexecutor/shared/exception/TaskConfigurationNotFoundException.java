package org.gcube.common.workspacetaskexecutor.shared.exception;


/**
 * The Class NoValidTaskConfigurationException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 3, 2018
 */
public class TaskConfigurationNotFoundException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 5569218683782613183L;

	/**
	 * Instantiates a new item not synched.
	 */
	public TaskConfigurationNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new item not synched.
	 *
	 * @param arg0 the arg 0
	 */
	public TaskConfigurationNotFoundException(String arg0) {
		super(arg0);
	}

}
