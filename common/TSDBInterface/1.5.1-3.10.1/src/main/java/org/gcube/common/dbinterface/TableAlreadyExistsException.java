package org.gcube.common.dbinterface;

public class TableAlreadyExistsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7843687721642880870L;
	
	public TableAlreadyExistsException(String log){
		super(log);
	}
}