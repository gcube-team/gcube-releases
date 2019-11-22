package org.gcube.portlets.user.workspace.shared;



/**
 * The Class SHUBOperationNotAllowedException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Apr 30, 2019
 */
public class SHUBOperationNotAllowedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 378541477623075645L;
	
	/**
	 * Instantiates a new SHUB operation not allowed.
	 */
	public SHUBOperationNotAllowedException() {
		super();
	}
	
	/**
	 * Instantiates a new SHUB operation not allowed.
	 *
	 * @param arg0 the arg 0
	 */
	public SHUBOperationNotAllowedException(String arg0){
		super(arg0);
	}

}
