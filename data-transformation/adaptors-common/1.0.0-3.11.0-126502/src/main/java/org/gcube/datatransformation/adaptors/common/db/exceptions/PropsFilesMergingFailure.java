package org.gcube.datatransformation.adaptors.common.db.exceptions;

public class PropsFilesMergingFailure extends Exception{

	private static final long serialVersionUID = 1L;

	public PropsFilesMergingFailure(String msg, Throwable e){
		super(msg, e);
	}
	
}
