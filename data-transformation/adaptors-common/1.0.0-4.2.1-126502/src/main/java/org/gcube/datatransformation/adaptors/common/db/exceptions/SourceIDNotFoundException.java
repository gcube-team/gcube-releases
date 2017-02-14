package org.gcube.datatransformation.adaptors.common.db.exceptions;

public class SourceIDNotFoundException extends Exception{

	public SourceIDNotFoundException(Throwable e){
		super("Could not find a matching source for the given sourceid", e);
	}
	
}
