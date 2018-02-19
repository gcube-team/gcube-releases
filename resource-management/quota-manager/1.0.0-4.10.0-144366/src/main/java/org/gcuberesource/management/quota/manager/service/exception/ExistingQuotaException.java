package org.gcuberesource.management.quota.manager.service.exception;

/**
 * ExistingQuotaException
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
public class ExistingQuotaException  extends  Exception{
	private static final long serialVersionUID = -2255657546267656458L;
	/**
	 * 
	 */
	public ExistingQuotaException() {
		super();
	}
	/**
	 * @param message
	 */
	public ExistingQuotaException(String message) {
		super(message);
	}
	public ExistingQuotaException(String message,Throwable t) {
		super(message,t);
	}
	

}