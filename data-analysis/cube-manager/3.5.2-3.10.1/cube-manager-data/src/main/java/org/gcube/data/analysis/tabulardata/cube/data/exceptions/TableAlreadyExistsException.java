package org.gcube.data.analysis.tabulardata.cube.data.exceptions;


public class TableAlreadyExistsException extends Exception {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5871964853743672022L;

	public TableAlreadyExistsException(String name){
		super(String.format("The table with name '%1$s' already exists in the db.",name));
	}



}
