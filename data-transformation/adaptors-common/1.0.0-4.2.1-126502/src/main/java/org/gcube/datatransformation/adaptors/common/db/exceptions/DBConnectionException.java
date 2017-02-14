package org.gcube.datatransformation.adaptors.common.db.exceptions;

public class DBConnectionException extends Exception{

	public DBConnectionException(String msg, Throwable e){
		super(msg, e);
	}
	
}
