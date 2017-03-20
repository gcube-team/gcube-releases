package org.gcube.datatransformation.adaptors.common.db.exceptions;

public class NonValidDBTablesFile extends Exception{

	private static final long serialVersionUID = 1L;

	public NonValidDBTablesFile(String msg, Throwable e){
		super(msg, e);
	}
	
}
