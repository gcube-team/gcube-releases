package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.io.InputStream;

/**
 * This class is the base class that must be extended by anyone wishing to implement a custom
 * Record element class for BLOB records. Every class extending this one must define an accessible default constructor
 * with an empty argument list
 * 
 * @author UoA
 */
public abstract class ResultElementBLOBBase extends ResultElementBase{
	/**
	 * Retrieves the blob content
	 * 
	 * @return the content
	 * @throws Exception An unrecoverbale for the operation error occured
	 */
	public abstract InputStream getContentOfBLOB() throws Exception;

	/**
	 * sets the blob content
	 * 
	 * @param content the content
	 * @throws Exception An unrecoverbale for the operation error occured
	 */
	public abstract void setContentOfBLOB(InputStream content) throws Exception;
	
	/**
	 * closes underlying structures, streams
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public abstract void close() throws Exception;
}
