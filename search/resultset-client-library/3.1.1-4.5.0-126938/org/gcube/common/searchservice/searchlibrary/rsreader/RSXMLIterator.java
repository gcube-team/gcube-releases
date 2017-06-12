package org.gcube.common.searchservice.searchlibrary.rsreader;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;

/**
 * This class acts as a simple iterator over the {@link org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader} though which it is initialized.
 * It provides functionality for accessing the <code>results</code> of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * the {@link org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader} points to.
 * 
 * @author UoA
 */
public class RSXMLIterator {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSXMLIterator.class);
	/**
	 * The reader to use
	 */
	private RSXMLReader reader=null;
	/**
	 * The current record
	 */
	private int currentRecord=-1;
	/**
	 * The default timeout
	 */
	private int waittime=RSConstants.sleepMax;
	
	/**
	 * Intantiates a new {@link RSXMLIterator} using the provided {@link RSXMLReader} 
	 * 
	 * @param reader The {@link RSXMLReader} to use
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	protected RSXMLIterator(RSXMLReader reader) throws Exception{
		if(reader==null){
			log.error("Cannot initialize with null reader. Throwing Exception");
			throw new Exception("Cannot initialize with null reader");
		}
		this.reader=reader;
		this.currentRecord=-1;
	}
	
	/**
	 * Intantiates a new {@link RSXMLIterator} using the provided {@link RSXMLReader} 
	 * 
	 * @param reader The {@link RSXMLReader} to use
	 * @param waittime the timeout
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	protected RSXMLIterator(RSXMLReader reader,int waittime) throws Exception{
		if(reader==null){
			log.error("Cannot initialize with null reader. Throwing Exception");
			throw new Exception("Cannot initialize with null reader");
		}
		this.reader=reader;
		this.currentRecord=-1;
		this.waittime=waittime;
	}
	
	/**
	 * Checks if there are more results in the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} this
	 * {@link RSXMLIterator} point to 
	 * 
	 * @return <code>true</code> if there are <code>false</code> otherwise. 
	 */
	public boolean hasNext(){
		try{
			if(this.currentRecord<0){
				while(reader.getNumberOfResults()<=0){
					if(!reader.getNextPart(this.waittime)) return false;
				}
				this.currentRecord=0;
				return true;
			}
			else if(this.currentRecord<=reader.getNumberOfResults()-1){
				return true;
			}
			else{
				if(!reader.getNextPart(this.waittime)){
					return false;
				}
				while(reader.getNumberOfResults()<=0){
					if(!reader.getNextPart(this.waittime)){
						return false;
					}
				}
				this.currentRecord=0;
				return true;
			}
		}catch(Exception e){
			log.error("Error while trying for has next. Returning false",e);
			return false;
		}
	}
	
	/**
	 * This operation retrieves the next available result. If no results are available or there was a
	 * problem during the creation of the result element, it returns <code>null</code>
	 * @see ResultElementBase
	 * 
	 * @param template The {@link ResultElementBase} extending class type that must be used to retrieve the result
	 * @return The result element that was retrieved
	 */
	public ResultElementBase next(Class template){
		try{
			ResultElementBase tmp=reader.getResults(template,this.currentRecord);
			if(tmp==null){
				log.error("retrieved record is null. Throwing Exception");
				throw new Exception("retrieved record is null");
			}
			this.currentRecord+=1;
			return tmp;
		}catch(Exception e){
			log.error("could not retrieve result. Returning null",e);
			this.currentRecord+=1;
			return null;
		}
	}
}
