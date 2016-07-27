package org.gcube.common.dbinterface.types;

public class TypeMappingException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TypeMappingException(String type){
		super("cannot map the "+type+" with any type in the db");
	}
	
}
