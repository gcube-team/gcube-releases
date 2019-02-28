package org.gcube.common.workspacetaskexecutor.shared.exception;



/**
 * The Class ItemNotExecutableException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 27, 2018
 */
public class ItemNotConfiguredException extends Exception {


	/**
	 *
	 */
	private static final long serialVersionUID = -6125541690663669233L;

	/**
	 * Instantiates a new item not synched.
	 */
	public ItemNotConfiguredException() {
		super();
	}

	/**
	 * Instantiates a new item not synched.
	 *
	 * @param arg0 the arg 0
	 */
	public ItemNotConfiguredException(String arg0) {
		super(arg0);
	}

}
