/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SearchServiceException extends Exception {

	private static final long serialVersionUID = 7385940364610061433L;

	public SearchServiceException(){}
	
	/**
	 * @param message
	 */
	public SearchServiceException(String message) {
		super(message);
	}
}
