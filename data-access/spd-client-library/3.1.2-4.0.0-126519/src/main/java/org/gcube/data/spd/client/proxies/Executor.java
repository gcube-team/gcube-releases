package org.gcube.data.spd.client.proxies;

import org.gcube.data.spd.stubs.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.stubs.types.Status;
import org.gcube.data.streams.Stream;

public interface Executor {

	public String createDwCAByChildren(String taxonKey) throws Exception;
	
	public String getResultLink(String jobId) throws InvalidIdentifierException;
	
	public String getErrorLink(String jobId) throws InvalidIdentifierException;
	
	public Status getStatus(String jobId) throws InvalidIdentifierException;
	
	public void removeJob(String jobId) throws InvalidIdentifierException;
	
	public String createDwCAByIds(final Stream<String> ids) throws Exception;
	
	public String createCSV(final Stream<String> ids) throws Exception;
	
	public String createCSVforOM(final Stream<String> ids) throws Exception;
	
	public String createDarwincoreFromOccurrenceKeys(final Stream<String> ids) throws Exception;
	
}
