package org.gcube.rest.resourcemanager.harvester.exceptions;

public class ResourceHarvesterException extends Exception {

	private static final long serialVersionUID = 5432613571072824074L;

	public ResourceHarvesterException(String string, Exception e) {
		super(string, e);
	}
	
	public ResourceHarvesterException(Exception e) {
		super(e);
	}
	
	public ResourceHarvesterException(String string) {
		super(string);
	}
}
