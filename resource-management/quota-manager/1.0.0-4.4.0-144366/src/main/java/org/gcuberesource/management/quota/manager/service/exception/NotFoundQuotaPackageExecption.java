package org.gcuberesource.management.quota.manager.service.exception;

public class NotFoundQuotaPackageExecption  extends  Exception{
	private static final long serialVersionUID = -3481086211695773825L;
	
	public NotFoundQuotaPackageExecption() {
		super();
	}
	/**
	 * @param message
	 */
	public NotFoundQuotaPackageExecption(String message) {
		super(message);
	}
	public NotFoundQuotaPackageExecption(String message,Throwable t) {
		super(message,t);
	}
	

}