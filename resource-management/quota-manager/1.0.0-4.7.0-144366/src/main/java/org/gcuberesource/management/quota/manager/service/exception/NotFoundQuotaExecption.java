package org.gcuberesource.management.quota.manager.service.exception;



/**
 * NotFoundQuotaExecption
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
public class NotFoundQuotaExecption  extends  Exception{
	private static final long serialVersionUID = -3481086211695773825L;
	
	public NotFoundQuotaExecption() {
		super();
	}
	/**
	 * @param message
	 */
	public NotFoundQuotaExecption(String message) {
		super(message);
	}
	public NotFoundQuotaExecption(String message,Throwable t) {
		super(message,t);
	}
	

}