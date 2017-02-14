package org.gcube.datapublishing.sdmx.impl.exceptions;

public class NoResultsException extends SDMXRegistryClientException {

	private static final long serialVersionUID = -1878011325765896655L;

	public NoResultsException() {
		super("No results found");
	}
}
