package org.gcube.rest.commons.db.dao.core;


public abstract class ConverterRecord<T> extends BaseRecord {

	private static final long serialVersionUID = 1L;

	public ConverterRecord(){
		
	}
	
	public abstract void copyFrom(T base);
	public abstract T copyTo() throws IllegalStateException;
	
	
	
}
