package org.gcube.application.framework.search.library.exception;

public class URIRetrievalFromISCacheException extends Exception{
	
	public URIRetrievalFromISCacheException(Throwable cause) {
		super("Error while retrieving EPRs for Service, from ISCache", cause);
	}

}
