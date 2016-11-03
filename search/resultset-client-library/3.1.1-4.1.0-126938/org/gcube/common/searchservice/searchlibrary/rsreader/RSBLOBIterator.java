package org.gcube.common.searchservice.searchlibrary.rsreader;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase;

/**
 * This class acts as a simple iterator over the {@link org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBReader} though which it is initialized.
 * It provides functionality for accessing the <code>results</code> of the {@link org..searchlibrary.resultset.ResultSet}
 * the {@link org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBReader} points to.
 * 
 * @author UoA
 */
public class RSBLOBIterator {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSBLOBIterator.class);
	/**
	 * The reader to use
	 */
	private RSBLOBReader reader=null;
	
	/**
	 * Intantiates a new {@link RSBLOBIterator} using the provided {@link RSBLOBReader} 
	 * 
	 * @param reader The {@link RSBLOBReader} to use
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	protected RSBLOBIterator(RSBLOBReader reader) throws Exception{
		if(reader==null){
			log.error("Cannot initialize with null reader. Throwing Exception");
			throw new Exception("Cannot initialize with null reader");
		}
		this.reader=reader;
	}
	
	/**
	 * Checks if there are more results in the {@link org..searchlibrary.resultset.ResultSet} this
	 * {@link RSBLOBIterator} point to 
	 * 
	 * @return <code>true</code> if there are <code>false</code> otherwise. 
	 */
	public boolean hasNext(){
		try{
			return !reader.isLast();
		}catch(Exception e){
			log.error("Error while trying for has next. Returning false",e);
			return false;
		}
	}
	
	/**
	 * This operation retrieves the next available result. If no results are available or there was a
	 * problem during the creation of the result element, it returns <code>null</code>
	 * @see ResultElementBLOBBase
	 * 
	 * @param template The {@link ResultElementBLOBBase} extending class type that must be used to retrieve the result
	 * @return The result element that was retrieved
	 */
	public ResultElementBLOBBase next(Class template){
		try{
			ResultElementBLOBBase tmp=reader.getResults(template);
			return tmp;
		}catch(Exception e){
			log.info("could not retrieve result. Returning null",e);
			return null;
		}
	}
}
