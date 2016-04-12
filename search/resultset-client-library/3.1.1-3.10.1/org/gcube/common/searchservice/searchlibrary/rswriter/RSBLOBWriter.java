package org.gcube.common.searchservice.searchlibrary.rswriter;

import java.util.Date;


import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementGC;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementType;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPoolObject;

/**
 * This class provides basic functionality to author a local {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class RSBLOBWriter extends RSPoolObject {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSBLOBWriter.class);
	/**
	 * Used for synchronization
	 */
	private Object synchAddition=null;
	/**
	 * The writer to use
	 */
	private RSFullWriter writer=null;
	
//	private RSWriterCreationParams attributes = null;

	/**
	 * Creates a new {@link RSBLOBWriter}
	 * @param intParams RS creation parameters
	 * @return The RSBLOBWriter created
	 * @throws Exception when creation failed 
	 */
	public static RSBLOBWriter getRSBLOBWriter(RSWriterCreationParams intParams) throws Exception{
		intParams.properties.add(new PropertyElementGC(PropertyElementGC.unspecified));
		intParams.properties.add(new PropertyElementType(PropertyElementType.BLOB));
		return new RSBLOBWriter(intParams, RSFullWriter.getRSFullWriter(intParams));
	}

	
	/**
	 * Creates a new {@link RSBLOBWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified} declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @return The created {@link RSBLOBWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSBLOBWriter getRSBLOBWriter() throws Exception{
		try{
			return new RSBLOBWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.BLOB)}));
		}catch(Exception e){
			log.error("could not create RSBLOBWriter. Throwing Exception",e);
			throw new Exception("could not create RSBLOBWriter");
		}
	}
	
	/**
	 * Creates a new {@link RSBLOBWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified}
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSBLOBWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSBLOBWriter getRSBLOBWriter(boolean dataFlow) throws Exception{
		try{
			return new RSBLOBWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.BLOB)},dataFlow));
		}catch(Exception e){
			log.error("could not create RSBLOBWriter. Throwing Exception",e);
			throw new Exception("could not create RSBLOBWriter");
		}
	}

	/**
	 * Creates a new {@link RSBLOBWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @return The created {@link RSBLOBWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSBLOBWriter getRSBLOBWriter(PropertyElementBase []properties) throws Exception{
		try{
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.BLOB);
			return new RSBLOBWriter(RSFullWriter.getRSFullWriter(props));
		}catch(Exception e){
			log.error("could not create RSBLOBWriter. Throwing Exception",e);
			throw new Exception("could not create RSBLOBWriter");
		}
	}

	/**
	 * Creates a new {@link RSBLOBWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSBLOBWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSBLOBWriter getRSBLOBWriter(PropertyElementBase []properties,boolean dataFlow) throws Exception{
		try{
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.BLOB);
			return new RSBLOBWriter(RSFullWriter.getRSFullWriter(props,dataFlow));
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}
	
	/**
	 * Creates a new {@link RSBLOBWriter} using the default number of results per part and size per part
	 * 
	 * @param writer The {@link RSFullWriter} tat must be used to perform low level operations
	 * @throws Exception could not instantiate writer
	 */
	protected RSBLOBWriter(RSFullWriter writer) throws Exception{
		this.writer=writer;
		this.synchAddition=new Object();
	}

	protected RSBLOBWriter(RSWriterCreationParams initParams, RSFullWriter writer) throws Exception{
	//	attributes = initParams;
		this.writer=writer;
		this.synchAddition=new Object();
	}

	/**
	 * Retrieves an {@link RSLocator} to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} beeing authored
	 * @see RSFullWriter#getRSLocator(RSResourceType)
	 * 
	 * @param type The type of {@link RSLocator} to be retrieved
	 * @return The created{@link RSLocator}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSLocator getRSLocator(RSResourceType type) throws Exception{
		try{
			synchronized(synchAddition){
				return writer.getRSLocator(type);
			}
		}catch(Exception e){
			log.error("Could not retrieve RSLocator. Throwing Exception",e);
			throw new Exception("Could not retrieve RSLocator");
		}
	}
	
	/**
	 * Retrieves an {@link RSLocator} to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} beeing authored
	 * @see RSFullWriter#getRSLocator(RSResourceType)
	 * 
	 * @param type The type of {@link RSLocator} to be retrieved
	 * @param scope The RS scope
	 * @return The created{@link RSLocator}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSLocator getRSLocator(RSResourceType type, GCUBEScope scope) throws Exception{
		try{
			synchronized(synchAddition){
				return writer.getRSLocator(type, scope);
			}
		}catch(Exception e){
			log.error("Could not retrieve RSLocator. Throwing Exception",e);
			throw new Exception("Could not retrieve RSLocator");
		}
	}

	/**
	 * Checks whether or not more results should be produced. THis operation blocks until
	 * an answer can be produced. This answer will define either that more results are nessecary,
	 * or the producer can relinguish any resources it has for producing more results because the 
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} is no longer
	 * used, or the timeout the user specified has been reached
	 * 
	 * @param time The timeout for the answer
	 * @return The answer
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public RSConstants.CONTROLFLOW more(long time) throws Exception{
		try{
			return this.writer.more(time);
		}catch(Exception e){
			log.error("Could not check if more records are needed. Throwing Exception",e);
			throw new Exception("Could not check if more records are needed");
		}
	}

	/**
	 * Checks whether the underlying RS is beeing fully produced by its author or is incrementaly
	 * updated on request
	 * 
	 * @return <code>true</code> if results are generated on request, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isFlowControled() throws Exception{
		return this.writer.isFlowControled();
	}

	/**
	 * Adds the provided result in the underlying {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}.
	 * If with this addition the records per part has been reached, the cashed records are stored in the current part and a new 
	 * part is created. Otherwise the result is cashed  
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addResults(java.lang.String[])
	 * 
	 * @param record The result to add
	 * @return whether or not the addition was completed
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public boolean addResults(ResultElementBLOBBase record) throws Exception{
		log.trace("client calls addResults ");
		synchronized(synchAddition){
			try{
				boolean success=true;
				writer.startNewPart();
				writer.addResults(record);
				writer.startNewPart();
				try{
					writer.wrapFile(RSFileHelper.persistStream(record.getContentOfBLOB()));
				}catch(Exception e){
					log.error("Could not persist document.setting incomplete",e);
					success=false;
				}
				return success;
			}catch(Exception e){
				log.error("Could not perform addition. Throwing Exception",e);
				throw new Exception("Could not perform addition");
			}
		}
	}

	/**
	 * Adds the provided results in the underlyoing {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}.
	 * If with this addition the records per part has been reached or exceeded, the cashed records are stored in the current part and a new 
	 * part is created. If the cashed records are still over the defined records per part, this will be repeated. Otherwise the results are cashed 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addResults(java.lang.String[])
	 * 
	 * @param record The result to add
	 * @return whether or not all of the addition were performed fully
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public boolean addResults(ResultElementBLOBBase []record) throws Exception{
		synchronized(synchAddition){
			try{
				boolean success=true;
				for(int i=0;i<record.length;i+=1){
					boolean s=this.addResults(record[i]);
					if(!s && success) success=false;
				}
				return success;
			}catch(Exception e){
				log.error("Could not perform addition. Throwing Exception",e);
				throw new Exception("Could not perform addition");
			}
		}
	}
	
	/**
	 * Ends the authoring to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} after flushing
	 * the remaing records, if any using {@link RSBLOBWriter#addResults(ResultElementBase[])}
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#endAuthoring()
	 * 
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void close() throws Exception{
		synchronized(synchAddition){
			try{
				writer.startNewPart();
				writer.endAuthoring();
			}catch(Exception e){
				log.error("Could not close writer. Throwing Exception",e);
				throw new Exception("Could not close writer");
			}
		}
	}

	/**
	 * removes any existing properties and sets the new ones
	 * 
	 * @param properties the properties to set
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void overrideProperties(PropertyElementBase []properties) throws Exception{
		try{
			writer.overrideProperties(properties);
		}catch(Exception e){
			log.error("could not override properties. Throwing Exception",e);
			throw new Exception("could not override properties");
		}
	}

	/**
	 * removes any existing properties and sets the new ones
	 * 
	 * @param properties the properties to set
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void overrideProperties(String properties) throws Exception{
		try{
			writer.overrideProperties(properties);
		}catch(Exception e){
			log.error("could not override properties. Throwing Exception",e);
			throw new Exception("could not override properties");
		}
	}
	
	
	
	/*Start of access leasing helpers*/
	/**
	 * 
	 * @throws Exception when access leasing cannot be disabled
	 */
	public void disableAccessLeasing() throws Exception{
		writer.disableAccessLeasing();
	}
	
	/**
	 * Extend the access leasing
	 * @param extend Extend for how much
	 * @throws Exception when extending failed
	 */
	public void extendAccessLeasing(int extend) throws Exception{
		writer.extendAccessLeasing(extend);
	}
	
	/**
	 * Get the access leasing
	 * @return the access leasing
	 * @throws Exception when retriving the access leasing failed
	 */
	public int getAccessLeasing() throws Exception{
		return writer.getAccessLeasing();
	}
	/*End of access leasing helpers*/

	/*Start of forward functions*/
	/**
	 * Is the RS forward reading only. 
	 * @return true if rs is of forward reading type
	 * @throws Exception when information retrival failed
	 */
	public boolean isForward() throws Exception{
		return writer.isForward();
	}

	/**
	 * Set an RS to be forward
	 * @param f true if to set forward
	 * @return true on success
	 * @throws Exception when seting forward failed
	 */
	public boolean setForward(boolean f) throws Exception{
		return writer.setForward(f);
	}
	
	/*End of forward functions*/

	/*Start of time leasing functions*/
	/**
	 * Get the time leasing
	 * @return the time leasing
	 * @throws Exception when retriving the time leasing failed
	 */
	public Date getTimeLeasing() throws Exception{
		return writer.getTimeLeasing();
	}

	/**
	 * Extend the time leasing
	 * @param extend Extend for how long
	 * @return true if successfull
	 * @throws Exception when extending failed
	 */
	public boolean extendTimeLeasing(Date extend) throws Exception{
		return writer.extendTimeLeasing(extend);
	}

	/**
	 * 
	 * @throws Exception when time leasing cannot be disabled
	 */
	public void disableTimeLeasing() throws Exception{
		writer.disableTimeLeasing();
	}
	
	/*End of time leasing functions*/

	
}
