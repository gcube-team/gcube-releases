package org.gcube.common.searchservice.searchlibrary.rswriter;

import java.security.PrivateKey;
import java.util.Date;

import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.searchlibrary.resultset.*;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.*;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocationWrapper;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPoolObject;

/**
 * This class provides full functionality to author a local {@link org..searchlibrary.resultset.ResultSet}
 *  
 * @author UoA
 */
public class RSFullWriter extends RSPoolObject {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSFullWriter.class);
	/**
	 * The local {@link org..searchlibrary.resultset.ResultSet}
	 */
	private ResultSet rs=null;
	/**
	 * a locator to the wraped RS
	 */
	private RSLocator remoteLocator=null;
	/**
	 * a locator to the local RS
	 */
	private RSLocator localLocator=null;
	
	private RSWriterCreationParams initParams = null;
	
	/**
	 * Create a new writer
	 * @param initParams creation parameters
	 * @return the RS writer
	 * @throws Exception failed to create the writer
	 */
	public static RSFullWriter getRSFullWriter(RSWriterCreationParams initParams) throws Exception{
		return new RSFullWriter(initParams);
	}
	/**
	 * Creates a new {@link RSFullWriter} declaring that all results will be produced fully.
	 * @see {@link org.gcube.common.searchlibrary.resultset.ResultSet#ResultSet(java.lang.String[])}
	 * 
	 * @param properties The properties the {@link ResultSet} must have
	 * @return The {@link RSFilterWriter}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSFullWriter getRSFullWriter(PropertyElementBase []properties) throws Exception{
		try{
			return new RSFullWriter(properties,false);
		}catch(Exception e){
			log.error("Could not create full writer. Throwing Exception",e);
			throw new Exception("Could not create full writer");
		}
	}
	
	/**
	 * Creates a new {@link RSFullWriter} 
	 * @see {@link org..searchlibrary.resultset.ResultSet#ResultSet(java.lang.String[])}
	 * 
	 * @param properties The properties the {@link ResultSet} must have
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The {@link RSFilterWriter}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSFullWriter getRSFullWriter(PropertyElementBase []properties,boolean dataFlow) throws Exception{
		try{
			return new RSFullWriter(properties,dataFlow);
		}catch(Exception e){
			log.error("Could not create full writer. Throwing Exception",e);
			throw new Exception("Could not create full writer");
		}
	}
	
	/**
	 * Creates a new {@link RSFullWriter} declaring that all results will be produced fully. 
	 * @see {@link org..searchlibrary.resultset.ResultSet#ResultSet(java.lang.String)}
	 * 
	 * @param properties The properties the {@link ResultSet} must have
	 * @return The {@link RSFilterWriter}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSFullWriter getRSFullWriter(String properties) throws Exception{
		try{
			return new RSFullWriter(properties,false);
		}catch(Exception e){
			log.error("Could not create full writer. Throwing Exception",e);
			throw new Exception("Could not create full writer");
		}
	}

	/**
	 * Creates a new {@link RSFullWriter} declaring that all results will be produced fully. 
	 * @see {@link org..searchlibrary.resultset.ResultSet#ResultSet(java.lang.String)}
	 * 
	 * @param properties The properties the {@link ResultSet} must have
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The {@link RSFilterWriter}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSFullWriter getRSFullWriter(String properties,boolean dataFlow) throws Exception{
		try{
			return new RSFullWriter(properties,dataFlow);
		}catch(Exception e){
			log.error("Could not create full writer. Throwing Exception",e);
			throw new Exception("Could not create full writer");
		}
	}

	/**
	 * Creates a new {@link RSFullWriter} 
	 * @see {@link org..searchlibrary.resultset.ResultSet#ResultSet(java.lang.String[])}
	 * 
	 * @param properties The properties the {@link ResultSet} must have
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	protected RSFullWriter(PropertyElementBase []properties,boolean dataFlow) throws Exception{
		try{
			String []props=null;
			if(properties!=null){
				props=new String[properties.length];
				for(int i=0;i<properties.length;i+=1){
					try{
						props[i]=properties[i].RS_toXML();
					}catch(Exception e){
						log.error("Could not serialize property. Continuing",e);
					}
				}
			}
			this.rs=new ResultSet(props,dataFlow);
		}catch(Exception e){
			log.error("Could not create ResultSet. Throwing Exception",e);
			throw new Exception("Could not create ResultSet");
		}
	}
	
	/**
	 * Creates a new {@link RSFullWriter} 
	 * @see {@link org.gcube.common.searchlibrary.resultset.ResultSet#ResultSet(java.lang.String)
	 * 
	 * @param properties The properties the {@link ResultSet} must have
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	protected RSFullWriter(String properties,boolean dataFlow) throws Exception{
		try{
			this.rs=new ResultSet(properties,dataFlow);
		}catch(Exception e){
			log.error("Could not create ResultSet. Throwing Exception",e);
			throw new Exception("Could not create ResultSet");
		}
	}

	/**
	 * Creates a new {@link RSFullWriter} 
	 * @see {@link org.gcube.common.searchlibrary.resultset.ResultSet#ResultSet(java.lang.String)
	 * 
	 * @param initParams creation parameters
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public RSFullWriter(RSWriterCreationParams initParams) throws Exception{
		log.trace(" RSFullWriter(RSWriterCreationParams initParams)");
		this.initParams = initParams;
		CreationParams rsParams = new CreationParams();
		PropertyElementBase[] properties = initParams.properties.toArray(new PropertyElementBase[initParams.properties.size()]);
		
		for(int i=0;i<properties.length;i+=1){
			try{
				rsParams.properties.add(properties[i].RS_toXML());
			}catch(Exception e){
				log.error("Could not serialize property. Continuing",e);
			}
		}
		rsParams.setAccessReads(initParams.getAccessReads());
		rsParams.setDataflow(initParams.isDataflow());
		rsParams.setExpire_date(initParams.getExpire_date());
		rsParams.setForward(initParams.isForward());
		rsParams.setPKey(initParams.getPubKey());
		this.rs=new ResultSet(rsParams);
	}
	/**
	 * Retrieves an {@link RSLocator} for the {@link ResultSet} beeing authored 
	 * `
	 * @param type The type of {@link RSLocator} that must be produced. 
	 * @return The created {@link RSLocator}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSLocator getRSLocator(RSResourceType type) throws Exception{
		if (initParams != null){
			if (initParams.getPrivKey() != null)
				return getRSLocator(type, null, initParams.getPrivKey());
		}
		return getRSLocator(type, null, null);
	}
	

	/**
	 * Retrieves an {@link RSLocator} for the {@link ResultSet} being authored 
	 * 
	 * @param type The type of {@link RSLocator} that must be produced. 
	 * @param scope The RS scope
 	 * @return the RS locator
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public RSLocator getRSLocator(RSResourceType type, GCUBEScope scope) throws Exception{
		return getRSLocator(type, scope, null);
	}

	/**
	 * Retrieves an {@link RSLocator} for the {@link ResultSet} being authored 
	 * 
	 * @param type The type of {@link RSLocator} that must be produced. 
 	 * @param pKey the RS private key
 	 * @return the RS locator
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public RSLocator getRSLocator(RSResourceType type, PrivateKey pKey) throws Exception{
		return getRSLocator(type, null, pKey);
	}

	/**
	 * Retrieves an {@link RSLocator} for the {@link ResultSet} beeing authored 
	 * 
	 * @param type The type of {@link RSLocator} that must be produced. 
	 * @param scope The RS scope
 	 * @param pKey the RS private key
 	 * @return the RS locator
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSLocator getRSLocator(RSResourceType type, GCUBEScope scope, PrivateKey pKey) throws Exception{
		try{
			if(this.remoteLocator!=null && (type instanceof RSResourceWSRFType || type instanceof RSResourceWSType)){
				return this.remoteLocator;
			}
			else if(this.localLocator!=null && (type instanceof RSResourceLocalType)){
				return this.localLocator;
			}
			else if (type instanceof RSResourceLocalType) {
				this.localLocator=new RSLocator(new RSResourceLocalType(),rs.getHeadName());
				return this.localLocator;
			}
			remoteLocator=RSLocationWrapper.retrieveLocator(type,rs.getHeadName(), scope, pKey);
			return this.remoteLocator;
		}catch(Exception e){
			log.error("Could not create RSLocator",e);
			throw new Exception("Could not create RSLocator");
		}
	}

	/**
	 * Retrieves the name of the file holding the head part
	 * @see org..searchlibrary.resultset.ResultSet#getHeadName()
	 * 
	 * @return The name of the file holding the head part
	 * @throws Exception An unrecoverable for the operation error has occured
	 */
	public String getHeadName() throws Exception{
		try{
			return rs.getHeadName();
		}catch(Exception e){
			log.error("Could not retrieve head part name. Throwing Exception",e);
			throw new Exception("Could not retrieve head part name");
		}
	}
	
	/**
	 * Start a new content part
	 * @see org..searchlibrary.resultset.ResultSet#startNewPart()
	 * 
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public void startNewPart() throws Exception{
		try{
			rs.startNewPart();
		}catch(Exception e){
			log.error("Could not start new part. Throwing Exception",e);
			throw new Exception("Could not start new part");
		}
	}

	/**
	 * Ends the authoring to the current part and the {@link ResultSet} chain
	 * @see org..searchlibrary.resultset.ResultSet#endAuthoring()
	 * 
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public void endAuthoring() throws Exception{
		try{
			rs.endAuthoring();
		}catch(Exception e){
			log.error("Could not end authoring. Throwing Exception",e);
			throw new Exception("Could not end authoring");
		}
	}
	
	/**
	 * Checks whether or not more results should be produced. THis operation blocks until
	 * an answer can be produced. This answer will define either that more results are nessecary,
	 * or the producer can relinguish any resources it has for producing more results because the 
	 * {@link org..searchlibrary.resultset.ResultSet} is no longer
	 * used, or the timeout the user specified has been reached
	 * 
	 * @param time The timeout for the answer
	 * @return The answer
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public RSConstants.CONTROLFLOW more(long time) throws Exception{
		try{
			return rs.more(time);
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
		return this.rs.getRSRef().isDataFlow();
	}

	/**
	 * Adds the provided {@link ResultElementBase} results to the current content part
	 * @see org..searchlibrary.resultset.ResultSet#addResults(java.lang.String[])
	 * 
	 * @param results The results to be added
	 * @return <code>true</code> or <code>false</code> depending on whether the addition was entirly succesful
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public boolean addResults(ResultElementBase []results) throws Exception{
		try{
			if(results==null){
				return true;
			}
			if(results.length==0){
				return true;
			}
			String []res=new String [results.length];
			for(int i=0;i<results.length;i+=1){
				try{
					res[i]=results[i].RS_toXML();
				}catch(Exception e){
					log.error("Could not add results. Continuing",e);
				}
			}
			return rs.addResults(res);
		}catch(Exception e){
			log.error("Could not add results. Throwing Exception",e);
			throw new Exception("Could not add results");
		}
	}
	
	/**
	 * Adds the provided results to the current content part
	 * @see org..searchlibrary.resultset.ResultSet#addText(java.lang.String[])
	 * 
	 * @param results The results to be added
	 * @return <code>true</code> or <code>false</code> depending on whether the addition was entirly succesful
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public boolean addText(String []results) throws Exception{
		try{
			if(results==null){
				return true;
			}
			if(results.length==0){
				return true;
			}
			return rs.addText(results);
		}catch(Exception e){
			log.error("Could not add results. Throwing Exception",e);
			throw new Exception("Could not add results");
		}
	}
	
	/**
	 * Adds the provided {@link ResultElementBase} result to the current content part
	 * @see org..searchlibrary.resultset.ResultSet#addResults(java.lang.String[])
	 * 
	 * @param results The results to be added
	 * @return <code>true</code> or <code>false</code> depending on whether the addition was succesful
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public boolean addResults(ResultElementBase results) throws Exception{
		try{
			if(results==null){
				return true;
			}
			String []res=new String[1];
			try{
				res[0]=results.RS_toXML();
			}catch(Exception e){
				log.error("Could not serialize result. returning false",e);
				return false;
			}
			return rs.addResults(res);
		}catch(Exception e){
			log.error("Could not start new part. Throwing Exception",e);
			throw new Exception("Could not start new part");
		}
	}
	
	/**
	 * Adds the provided result to the current content part
	 * 
	 * @param results The result to add
	 * @return <code>true</code> or <code>false</code> depending on whether the addition was succesful
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public boolean addText(String results) throws Exception{
		if(results==null){
			return true;
		}
		return rs.addText(new String[]{results});
	}
	
	/**
	 * Wraps the content of the provided file to the current {@link ResultSet} content part
	 * @see org..searchlibrary.resultset.ResultSet#wrapFile(java.lang.String)
	 * 
	 * @param filename The file name whose content must be wrapped
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public void wrapFile(String filename) throws Exception{
		rs.wrapFile(filename);
	}

	/**
	 * removes any existing properties and sets the new ones
	 * 
	 * @param properties the properties to set
	 * @throws Exception An unrecoverable for thew operation error occured
	 */
	public void overrideProperties(PropertyElementBase []properties) throws Exception{
		rs.overrideProperties(properties);
	}

	/**
	 * removes any existing properties and sets the new ones
	 * 
	 * @param properties the properties to set
	 * @throws Exception An unrecoverable for thew operation error occured
	 */
	public void overrideProperties(String properties) throws Exception{
		rs.overrideProperties(properties);
	}
	
	/*Start of access leasing helpers*/
	/**
	 * 
	 * @throws Exception when access leasing cannot be disabled
	 */
	public void disableAccessLeasing() throws Exception{
		rs.disableAccessLeasing();
	}
	
	/**
	 * Extend the access leasing
	 * @param extend Extend for how much
	 * @throws Exception when extending failed
	 */
	public void extendAccessLeasing(int extend) throws Exception{
		rs.extendAccessLeasing(extend);
	}
	
	/**
	 * Is the RS forward reading only. 
	 * @return true if rs is of forward reading type
	 * @throws Exception when information retrival failed
	 */
	public int getAccessLeasing() throws Exception{
		return rs.getAccessLeasing();
	}
	/*End of access leasing helpers*/

	/*Start of forward functions*/
	/**
	 * Is the RS forward reading only. 
	 * @return true if rs is of forward reading type
	 * @throws Exception when information retrival failed
	 */
	public boolean isForward() throws Exception{
		return rs.isForward();
	}

	/**
	 * Set an RS to be forward
	 * @param f true if to set forward
	 * @return true on success
	 * @throws Exception when seting forward failed
	 */
	public boolean setForward(boolean f) throws Exception{
		return rs.setForward(f);
	}
	
	/*End of forward functions*/

	/*Start of time leasing functions*/
	/**
	 * Get the time leasing
	 * @return the time leasing
	 * @throws Exception when retriving the time leasing failed
	 */
	public Date getTimeLeasing() throws Exception{
		return rs.getTimeLeasing();
	}

	/**
	 * Extend the time leasing
	 * @param extend Extend for how long
	 * @return true if successfull
	 * @throws Exception when extending failed
	 */
	public boolean extendTimeLeasing(Date extend) throws Exception{
		return rs.extendTimeLeasing(extend);
	}

	/**
	 * 
	 * @throws Exception when time leasing cannot be disabled
	 */
	public void disableTimeLeasing() throws Exception{
		rs.disableTimeLeasing();
	}
	
	/*End of time leasing functions*/
}