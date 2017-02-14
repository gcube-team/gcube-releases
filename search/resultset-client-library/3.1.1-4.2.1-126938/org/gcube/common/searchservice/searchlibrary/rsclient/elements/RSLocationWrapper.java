package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import java.net.InetAddress;
import java.security.PrivateKey;
import java.util.Date;

import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.resultsetservice.stubs.CanStreamResponse;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;

/**
 * This class acts as a helper class hidding the location of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class RSLocationWrapper {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSLocationWrapper.class);
	/**
	 * The locator to use
	 */
	private RSLocator locator=null;
	/**
	 * the local RS
	 */
	private ResultSet lrs=null;
	/**
	 * The remote RS port type
	 */
	private RSTypeWrapper rrs=null;
	

	/**
	 * Retrieve the RS locator 
	 * @param type type of the RSLocator to be created
	 * @param headFileName the file name of the head
	 * @param scope the scope of the RS
	 * @return the RS locator created
	 * @throws Exception when something went wrong 
	 */
	public static RSLocator retrieveLocator(RSResourceType type,String headFileName, GCUBEScope scope) throws Exception{
		return retrieveLocator(type, headFileName, scope, null);
	}

	/**
	 * Retrieve the RS locator 
	 * @param type type of the RSLocator to be created
	 * @param headFileName the file name of the head
	 * @param pKey the private key of the RS
	 * @return the RS locator created
	 * @throws Exception when something went wrong 
	 */
	public static RSLocator retrieveLocator(RSResourceType type,String headFileName, PrivateKey pKey) throws Exception{
		return retrieveLocator(type, headFileName, null, pKey);
	}

	/**
	 * Retrieve the RS locator 
	 * @param type type of the RSLocator to be created
	 * @param headFileName the file name of the head
	 * @return the RS locator created
	 * @throws Exception when something went wrong 
	 */
	public static RSLocator retrieveLocator(RSResourceType type,String headFileName) throws Exception{
		return retrieveLocator(type, headFileName, null, null);
	}

	/**
	 * Retrieves a locator 
	 * 
	 * @param type the type of locator
	 * @param headFileName the head file name
	 * @param scope The RS scope
 	 * @param pkey the RS private key
	 * @return the locator
	 * @throws Exception An unrecoverable for the operation error has occured
	 */
	public static RSLocator retrieveLocator(RSResourceType type,String headFileName, GCUBEScope scope, PrivateKey pkey) throws Exception{
		if(type instanceof RSResourceWSRFType){
			return RSTypeWrapper.retrieveWSRFLocator(((RSResourceWSRFType)type).getServiceEndPoint().toString(),headFileName, scope, pkey);
		}
		else if(type instanceof RSResourceWSType){
			return RSTypeWrapper.retrieveWSLocator(((RSResourceWSType)type).getServiceEndPoint().toString(),headFileName, scope, pkey);
		}
		else{
			log.error("Unrecognized RSResourceType. Throwing Exception");
			throw new Exception("Unrecognized RSResourceType");
		}		
	}
	
	/**
	 * Retrieves the port used for transport
	 * 
	 * @return the port
	 */
	public int getStaticPort(){
		if(this.rrs==null) return -1;
		return this.rrs.getPort();
	}
	
	/**
	 * Retrieves the SSL support
	 * 
	 * @return SSL support
	 */
	public boolean getSSLsupport() {
		if(this.rrs==null) return true;
		return this.rrs.getSSLsupport();
	}

	
	/**
	 * Creates a new {@link RSLocationWrapper} pointing to the {@link ResultSet} the provided
	 * {@link RSLocator} identifies
	 * 
	 * @param locator The {@link RSLocator} identifying the {@link ResultSet} to be read
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSLocationWrapper(RSLocator locator) throws Exception{
		if(locator.getRSResourceType() instanceof RSResourceLocalType) {
			try{
				if (locator.getPrivKey() == null)
					lrs = new ResultSet(locator.getLocator());
				else
					lrs = new ResultSet(locator.getLocator(), locator.getPrivKey());
				this.locator=locator;
			}catch(Exception e){
				log.error("Could not created RSLocationWrapper. Throwsing Exception",e);
				throw new Exception("Could not created RSLocationWrapper");
			}
		}
		else{
			try{
				rrs=new RSTypeWrapper(locator);
				this.locator=rrs.getLocator();
			}catch(Exception e){
				log.error("Could not created RSLocationWrapper. Throwsing Exception",e);
				throw new Exception("Could not created RSLocationWrapper");
			}
		}
	}
	
	/**
	 * Retrieves an {@link RSLocator} identifying the {@link ResultSet} beeing read
	 * 
	 * @return The {@link RSLocator}
	 */
	public RSLocator getRSLocator(){
		return locator;
	}
	
	/**
	 * This operation wraps the current {@link ResultSet} in a web service. The resource
	 * beeing wrapped must be local 
	 * 
	 * @param type The type of resource to create. Must be {@link RSResourceWSRFType} or {@link org.gcube.common.searchservice.resultsetservice.impl.WSRSSessionToken}
	 * @return The locator of the created resource
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSLocator wrap(RSResourceType type) throws Exception{
		return new RSTypeWrapper(type,this.getHeadFileName()).getLocator();
	}
	
	/**
	 * This operation wraps the {@link ResultSet} whose head part name is provided in a WSRF web service.
	 * The resource beeing wrapped must be local to the service whose end point is provided 
	 * 
	 * @param type The type of resource to create. Must be {@link RSResourceWSRFType} or {@link org.gcube.common.searchservice.resultsetservice.impl.WSRSSessionToken}
	 * @param headName The name of the head part
	 * @param serviceEndPoint The end point to the {@link org.gcube.common.searchservice.resultsetservice.impl.ResultSetService}
	 * @return The locator of the created resource
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSLocator wrap(RSResourceType type,String headName,String serviceEndPoint) throws Exception{
		return new RSTypeWrapper(type,headName,serviceEndPoint).getLocator();
	}
	
	/**
	 * Retrieves the filename of the file holding the head part of the underlying {@link ResultSet}
	 * 
	 * @return The name of the file holding the head part of the {@link ResultSet}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getHeadFileName() throws Exception{
		if(lrs!=null){
			try{
				return lrs.getHeadName();
			}catch(Exception e){
				log.error("Could not retrieve head file name. Throwing Exception",e);
				throw new Exception("Could not retrieve head file name");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getHeadFileName(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve properties. Throwing Exception",e);
				throw new Exception("Could not retrieve properties");
			}
		}else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * retrieves the stream port of a remote service
	 * 
	 * @return the stream port to connect to, an excewption in thrown in case of a local result set 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public CanStreamResponse getStreamPort() throws Exception{
		if(lrs!=null){
			log.error("requested stream port for local RS. Thropwing Exception");
			throw new Exception ("requested stream port for local RS"); 
		}
		else if(rrs!=null){
			try{
				CanStreamResponse streamInfo = rrs.canStream(this.locator.getSessionToken());
				log.trace("Can Stream from port: "+streamInfo.getPort());
				return streamInfo;
			}catch(Exception e){
				log.error("Could not retrieve stream port. Throwing Exception",e);
				throw new Exception("Could not retrieve stream port");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Clears underlying structures
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void clear() throws Exception{
		if(lrs!=null){
			try{
				lrs.clear();
			}catch(Exception e){
				log.error("Could not clear underlying structures. Throwing Exception",e);
				throw new Exception("Could not clear underlying structures");
			}
		}
		else if(rrs!=null){
			try{
				rrs.clear(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not clear underlying structures. Throwing Exception",e);
				throw new Exception("Could not clear underlying structures");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Retireves the properties form the head part that are of the specified type
	 * 
	 * @param type The type of properties to be retrieved
	 * @return The retrieved properties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []getProperties(String type) throws Exception{
		if(lrs!=null){
			try{
				return lrs.getProperties(type);
			}catch(Exception e){
				log.error("Could not retrieve properties. Throwing Exception",e);
				throw new Exception("Could not retrieve properties");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getProperties(type,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve properties. Throwing Exception",e);
				throw new Exception("Could not retrieve properties");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Checks whether the underlying RS is beeing fully produced by its author or is incrementaly
	 * updated on request
	 * 
	 * @return <code>true</code> if results are generated on request, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isFlowContoled() throws Exception{
		if(lrs!=null){
			try{
				return lrs.getRSRef().isDataFlow();
			}catch(Exception e){
				log.error("Could not check if RS is flow controled. Throwing Exception",e);
				throw new Exception("Could not check if RS is flow controled");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.isFlowControled(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not check if RS is flow controled. Throwing Exception",e);
				throw new Exception("Could not check if RS is flow controled");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Moves the underlying {@link ResultSet} to point to the next payload part
	 * 
	 * @param times The maximum amount of millisecond to block waiting for the next
	 * part to be produced
	 * @return <code>true</code> if a next part exists and the move has been performed,<code>false</code> if this is the last part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean getNextPart(int times) throws Exception{
		if(lrs!=null){
			try{
				return lrs.getNextPart(times);
			}catch(Exception e){
				log.error("Could not move to next part. Throwing Exception",e);
				throw new Exception("Could not move to next part");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getNextPart(times,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not move to next part. Throwing Exception",e);
				throw new Exception("Could not move to next part");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Checks if the next part is availabe
	 * 
	 * @return <code>true</code> true if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean nextAvailable() throws Exception{
		if(lrs!=null){
			try{
				return lrs.nextAvailable();
			}catch(Exception e){
				log.error("Could not check if next part is available. Throwing Exception",e);
				throw new Exception("Could not check if next part is available");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.nextAvailable(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not check if next part is available. Throwing Exception",e);
				throw new Exception("Could not check if next part is available");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Moves the {@link ResultSet} to point to the previous payload part
	 * 
	 * @return <code>true</code> if a previous part exists and the move has been performed,<code>false</code> if this is the last part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean getPreviousPart() throws Exception{
		if(lrs!=null){
			try{
				return lrs.getPreviousPart();
			}catch(Exception e){
				log.error("Could not move to previous part. Throwing Exception",e);
				throw new Exception("Could not move to previous part");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getPreviousPart(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not move to previous part. Throwing Exception",e);
				throw new Exception("Could not move to previous part");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Moves the {@link ResultSet} to point to the first payload part
	 * 
	 * @return <code>true</code> if a next part exists and the RS now points to that, <code>false</code> oterwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean getFirstPart() throws Exception{
		if(lrs!=null){
			try{
				return lrs.getFirstPart();
			}catch(Exception e){
				log.error("Could not move to Head part. Throwing Exception",e);
				throw new Exception("Could not move to Head part");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getFirstPart(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not move to Head part. Throwing Exception",e);
				throw new Exception("Could not move to Head part");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Retrieves a representation of the custom properties of this {@link ResultSet}
	 * 
	 * @return Teh xml resilaization
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String retrieveCustomProperties() throws Exception{
		if(lrs!=null){
			try{
				return lrs.retrieveCustomProperties();
			}catch(Exception e){
				log.error("Could not retrieve Custom Properties. Throwing Exception",e);
				throw new Exception("Could not retrieve Custom Properties");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.retrieveCustomProperties(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve Custom Properties. Throwing Exception",e);
				throw new Exception("Could not retrieve Custom Properties");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Checks if the current part is the first payload part
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isFirst() throws Exception{
		if(lrs!=null){
			try{
				return lrs.isFirst();
			}catch(Exception e){
				log.error("Could not check if is First. Throwing Exception",e);
				throw new Exception("Could not check if is First");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.isFirst(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not check if is First. Throwing Exception",e);
				throw new Exception("Could not check if is First");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Checks if the current part is the last part
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isLast() throws Exception{
		if(lrs!=null){
			try{
				return lrs.isLast();
			}catch(Exception e){
				log.error("Could not check if is Last. Throwing Exception",e);
				throw new Exception("Could not check if is Last");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.isLast(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not check if is Last. Throwing Exception",e);
				throw new Exception("Could not check if is Last");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Retrieves the number of results available in the current payload part
	 * 
	 * @param type the type of RS
	 * @return The number of results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public int getNumberOfResults(String type) throws Exception{
		if(lrs!=null){
			try{
				return lrs.getNumberOfResults(type);
			}catch(Exception e){
				log.error("Could not get number of results. Throwing Exception",e);
				throw new Exception("Could not get number of results");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getNumberOfResults(type, this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not get number of results. Throwing Exception",e);
				throw new Exception("Could not get number of results");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Retrieves the result with the specified index of the current payload part
	 * 
	 * @param index The index of the result that must be retrieved
	 * @return The result element
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getResults(int index) throws Exception{
		if(lrs!=null){
			try{
				return lrs.getResult(index);
			}catch(Exception e){
				log.warn("Could not get result with index"+index+". In case of a BLOB rs this is not alarming :-). Throwing Exception",e);
				throw new Exception("Could not get result with index"+index+". In case of a BLOB rs this is not alarming :-)");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getResult(index,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not get result with index"+index+". Throwing Exception",e);
				throw new Exception("Could not get result with index"+index);
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Retrieves all the results of the current payload part
	 * 
	 * @return The retrieved result elements
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []getResults() throws Exception{
		if(lrs!=null){
			try{
				String []res=lrs.getResults();
				if(res==null){
					return new String[0];
				}
				return res;
			}catch(Exception e){
				log.error("Could not get results. Throwing Exception",e);
				throw new Exception("Could not get results");
			}
		}
		else if(rrs!=null){
			try{
				String []res=rrs.getAllResults(this.locator.getSessionToken());
				if(res==null){
					return new String[0];
				}
				return res;
			}catch(Exception e){
				log.error("Could not get results. Throwing Exception",e);
				throw new Exception("Could not get results");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Retrieves all the results of the current payload part whose index falls in the specified area
	 * 
	 * @param from The starting index
	 * @param to The end index
	 * @return The retrieved result elements
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []getResults(int from,int to) throws Exception{
		if(lrs!=null){
			try{
				String []res=lrs.getResults(from,to);
				if(res==null){
					return new String[0];
				}
				return res;
			}catch(Exception e){
				log.error("Could not get results. Throwing Exception",e);
				throw new Exception("Could not get results");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getResults(from,to,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not get results. Throwing Exception",e);
				throw new Exception("Could not get results");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * gets the attached payload content if can be attached, stores it locally and returns the local filename
	 * 
	 * @return the local filename
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String canAttach() throws Exception{
		if(lrs!=null){
			try{
				String localName=RSFileHelper.generateName(RSConstants.CONTENT,null);
				RSFileHelper.copy(lrs.getCurrentContentPartName(),localName);
				return localName;
			}catch(Exception e){
				log.error("Could not get attached content. Throwing Exception",e);
				throw new Exception("Could not get attached content");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.canAttach(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not get attached content. Throwing Exception",e);
				throw new Exception("Could not get attached content");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Splits the current payload part to smaller parts as defined by the {@link org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants#partSize}
	 * property. The split parts are encoded using Base64 encoding.
	 * 
	 * @return An array with the produced parts
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []splitEncoded() throws Exception{
		if(lrs!=null){
			try{
				String []res=lrs.splitEncoded();
				if(res==null){
					return new String[0];
				}
				return res;
			}catch(Exception e){
				log.error("Could not split content encoded. Throwing Exception",e);
				throw new Exception("Could not split content encoded");
			}
		}
		else if(rrs!=null){
			try{
				String []res=rrs.splitEncoded(this.locator.getSessionToken());
				if(res==null){
					return new String[0];
				}
				return res;
			}catch(Exception e){
				log.error("Could not split content encoded. Throwing Exception",e);
				throw new Exception("Could not split content encoded");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Splits the current payload part to smaller parts as defined by the {@link org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants#partSize}
	 * property
	 * 
	 * @return An array with the produced parts
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []splitClear() throws Exception{
		if(lrs!=null){
			try{
				String []res=lrs.splitClear();
				if(res==null){
					return new String[0];
				}
				return res;
			}catch(Exception e){
				log.error("Could not split content encoded. Throwing Exception",e);
				throw new Exception("Could not split content encoded");
			}
		}
		else if(rrs!=null){
			try{
				String []res=rrs.splitClear(this.locator.getSessionToken());
				if(res==null){
					return new String[0];
				}
				return res;
			}catch(Exception e){
				log.error("Could not split content clear. Throwing Exception",e);
				throw new Exception("Could not split content clear");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Performs the provided xPath expression on the document of the current part. The output
	 * of the ervaluation is returned in a string serialization
	 * 
	 * @param xPath The xPath to be evaluated
	 * @return The serialization of the evaluation output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String executeQueryOnDocument(String xPath) throws Exception{
		if(lrs!=null){
			try{
				return lrs.executeQueryOnDocument(xPath);
			}catch(Exception e){
				log.error("Could not execute query on document. Throwing Exception",e);
				throw new Exception("Could not execute query on document");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.executeQueryOnDocument(xPath,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not execute query on document. Throwing Exception",e);
				throw new Exception("Could not execute query on document");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Performs the provided xPath expression on the document of the head part. The output
	 * of the ervaluation is returned in a string serialization
	 * 
	 * @param xPath The xPath to be evaluated
	 * @return The serialization of the evaluation output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String executeQueryOnHead(String xPath) throws Exception{
		if(lrs!=null){
			try{
				return lrs.executeQueryOnHead(xPath);
			}catch(Exception e){
				log.error("Could not execute query on head. Throwing Exception",e);
				throw new Exception("Could not execute head on document");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.executeQueryOnHead(xPath,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not execute query on head. Throwing Exception",e);
				throw new Exception("Could not execute head on document");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Performs the provided xPath expression on the results of the current payload part. if the evaluation
	 * returns output then this result is incuded in the returned value 
	 * 
	 * @param xPath The xPath that should be evaluated. The expression must start from the current node
	 * @return The matching result elements
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []executeQueryOnResults(String xPath) throws Exception{
		if(lrs!=null){
			try{
				String []ret=lrs.executeQueryOnResults(xPath);
				if(ret==null){
					return new String[0];
				}
				return ret;
			}catch(Exception e){
				log.error("Could not execute query on results. Throwing Exception",e);
				throw new Exception("Could not execute query on results");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.executeQueryOnResults(xPath,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not execute query on results. Throwing Exception",e);
				throw new Exception("Could not execute query on results");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * Checks if the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * is in the same host with the {@link RSLocationWrapper}
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isLocal() throws Exception{
		if(lrs!=null){
			return true;
		}
		else if(rrs!=null){
			try{
				String ip=rrs.getHostIP(this.locator.getSessionToken());
				if(InetAddress.getLocalHost().getHostAddress().compareTo(ip)==0){
					return true;
				}
				return false;
			}catch(Exception e){
				log.error("Could not check if is local. Throwing Exception",e);
				throw new Exception("Could not check if is local");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}
	
	/**
	 * retrieves the ip of the machine the underlying result set is hosted
	 * 
	 * @return the ip
	 * @throws Exception an unrecoverable for the operation error has occured
	 */
	public String getHostIP() throws Exception{
		if(lrs!=null){
			try{
				return InetAddress.getLocalHost().getHostAddress();
			}catch(Exception e){
				log.error("Could not retrieve local host ip. Throwing Exception",e);
				throw new Exception("Could not retrieve local host ip");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getHostIP(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve remote host ip. Throwing Exception",e);
				throw new Exception("Could not retrieve remote host ip");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
		
	}
	
	/**
	 * retrieves the name of the machine the underlying result set is hosted
	 * 
	 * @return the host name
	 * @throws Exception an unrecoverable for the operation error has occured
	 */
	public String getHostName() throws Exception{
		if(lrs!=null){
			try{
				return InetAddress.getLocalHost().getHostName();
			}catch(Exception e){
				log.error("Could not retrieve local host name. Throwing Exception",e);
				throw new Exception("Could not retrieve local host name");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getHostName(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve remote host name. Throwing Exception",e);
				throw new Exception("Could not retrieve remote host name");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
		
	}

	/**
	 * Retrieves the full payload of the provided file
	 * 
	 * @param filename The name of the file
	 * @return The content of the file
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getFileContent(String filename) throws Exception{
		if(lrs!=null){
			try{
				return lrs.getFileContent(filename);
			}catch(Exception e){
				log.error("Could not retrieve file content. Throwing Exception",e);
				throw new Exception("Could not retrieve file content");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getFileContent(filename,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve file content. Throwing Exception",e);
				throw new Exception("Could not retrieve file content");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Retrieves the payload of the current content part
	 * 
	 * @return The part payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getCurrentContentPartPayload() throws Exception{
		if(lrs!=null){
			try{
				return lrs.getCurrentContentPartPayload();
			}catch(Exception e){
				log.error("Could not retrieve file content. Throwing Exception",e);
				throw new Exception("Could not retrieve file content");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getCurrentContentPartPayload(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve file content. Throwing Exception",e);
				throw new Exception("Could not retrieve file content");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Performs the provided xslt transformation on the current part and retrieves the output of this transformation
	 * 
	 * @param transformation The xslt transformation to be performed
	 * @return The output of the transformation
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformByXSLT(String transformation) throws Exception{
		if(lrs!=null){
			try{
				return lrs.transformByXSLT(transformation);
			}catch(Exception e){
				log.error("Could not transform and retrieve content. Throwing Exception",e);
				throw new Exception("Could not transform and retrieve content");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.transformByXSLT(transformation,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not transform and retrieve content. Throwing Exception",e);
				throw new Exception("Could not transform and retrieve content");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Forwards a filter operation in the underlying {@link ResultSet}
	 * 
	 * @param xPath The xpath to base the filtering on
	 * @param properties The properties the new {@link ResultSet} should have
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String filterRS(String xPath,String []properties) throws Exception{
		if(lrs!=null){
			try{
				return lrs.filterRS(xPath,properties);
			}catch(Exception e){
				log.error("Could not filter By xPath Service Side. Throwing Exception",e);
				throw new Exception("Could not filter By xPath Service Side");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.filterRSProp(xPath,properties,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not filter By xPath Service Side. Throwing Exception",e);
				throw new Exception("Could not filter By xPath Service Side");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Forwards a filter operation in the underlying {@link ResultSet}
	 * 
	 * @param xPath The xpath to base the filtering on
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String filterRS(String xPath) throws Exception{
		if(lrs!=null){
			try{
				return lrs.filterRS(xPath);
			}catch(Exception e){
				log.error("Could not filter By xPath Service Side. Throwing Exception",e);
				throw new Exception("Could not filter By xPath Service Side");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.filterRS(xPath,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not filter By xPath Service Side. Throwing Exception",e);
				throw new Exception("Could not filter By xPath Service Side");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Forwards a transform operation in the underlying {@link ResultSet}
	 * 
	 * @param xslt The xslt to be applied
	 * @param properties The properties the new {@link ResultSet} should have
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformRS(String xslt,String []properties) throws Exception{
		if(lrs!=null){
			try{
				return lrs.transformRS(xslt,properties);
			}catch(Exception e){
				log.error("Could not transform By XSLT Service Side. Throwing Exception",e);
				throw new Exception("Could not transform By XSLT Service Side");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.transformRSProp(xslt,properties, this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not filter By xPath Service Side. Throwing Exception",e);
				throw new Exception("Could not filter By xPath Service Side");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Forwards a transform operation in the underlying {@link ResultSet}
	 * 
	 * @param xslt The xslt to be applied
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformRS(String xslt) throws Exception{
		if(lrs!=null){
			try{
				return lrs.transformRS(xslt);
			}catch(Exception e){
				log.error("Could not filter By xPath Service Side. Throwing Exception",e);
				throw new Exception("Could not filter By xPath Service Side");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.transformRS(xslt,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not filter By xPath Service Side. Throwing Exception",e);
				throw new Exception("Could not filter By xPath Service Side");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Forwards a keep top operation in the underlying {@link ResultSet}
	 * 
	 * @param count The number of top results to keep
	 * @param properties The properties the new {@link ResultSet} should have
	 * @param type The type of keep top operation to perform. This can be one of {@link org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric#PERPART}
	 * and {@link org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric#PERRECORD}
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String keepTop(int count,String []properties,short type) throws Exception{
		if(lrs!=null){
			try{
				return lrs.keepTop(properties,count,type);
			}catch(Exception e){
				log.error("Could not transform By XSLT Service Side. Throwing Exception",e);
				throw new Exception("Could not transform By XSLT Service Side");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.keepTopProp(count,properties,type,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not keep Top Service Side. Throwing Exception",e);
				throw new Exception("Could not keep Top Service Side");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Forwards a keep top operation in the underlying {@link ResultSet}
	 * 
	 * @param count The number of top results to keep
	 * @param type The type of keep top operation to perform. This can be one of {@link org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric#PERPART}
	 * and {@link org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric#PERRECORD}
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String keepTop(int count,short type) throws Exception{
		if(lrs!=null){
			try{
				return lrs.keepTop(count,type);
			}catch(Exception e){
				log.error("Could not keep Top Service Side. Throwing Exception",e);
				throw new Exception("Could not keep Top Service Side");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.keepTop(count,type,this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not keep Top Service Side. Throwing Exception",e);
				throw new Exception("Could not keep Top Service Side");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Forwards a cloning operation to the underlying {@link ResultSet}
	 * 
	 * @return The head part name of the new {@link ResultSet} in the machine that holds the current {@link ResultSet} 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String cloneRS() throws Exception{
		if(lrs!=null){
			try{
				return lrs.cloneRS();
			}catch(Exception e){
				log.error("Could not clone rs Service Side. Throwing Exception",e);
				throw new Exception("Could not clone rs Service Side");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.cloneRS(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not clone rs Service Side. Throwing Exception",e);
				throw new Exception("Could not clone rs Service Side");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Retrieves the name of the currnt content part holding file
	 * 
	 * @return The name of the file holding the current content part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getCurrentContentPart() throws Exception{
		if(lrs!=null){
			try{
				return lrs.getCurrentContentPartName();
			}catch(Exception e){
				log.error("Could not retrieve current content part. Throwing Exception",e);
				throw new Exception("Could not retrieve current content part");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.getCurrentContentPartName(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve current content part. Throwing Exception",e);
				throw new Exception("Could not retrieve current content part");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * It the RS forward only?
	 * @return true if the RS is forward
	 * @throws Exception failed to retrive forward only information
	 */
	public boolean isForward() throws Exception{
		if(lrs!=null){
			try{
				return lrs.isForward();
			}catch(Exception e){
				log.error("Could not retrieve current content part. Throwing Exception",e);
				throw new Exception("Could not retrieve current content part");
			}
		}
		else if(rrs!=null){
			try{
				return rrs.isForward(this.locator.getSessionToken());
			}catch(Exception e){
				log.error("Could not retrieve current content part. Throwing Exception",e);
				throw new Exception("Could not retrieve current content part");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Has the RS access leasing
	 * @return true if access leasing for this RS is enabled
	 * @throws Exception failed to retrive leasing information
	 */
	public boolean hasAccessLeasing() throws Exception{
		if(lrs!=null){
			try{
				return (lrs.getAccessLeasing() >= 0);
			}catch(Exception e){
				log.error("Could not retrieve current content part. Throwing Exception",e);
				throw new Exception("Could not retrieve current content part");
			}
		}
		else if(rrs!=null){
			try{
				return (rrs.getAccessLeasing(this.locator.getSessionToken()) >= 0);
			}catch(Exception e){
				log.error("Could not retrieve current content part. Throwing Exception",e);
				throw new Exception("Could not retrieve current content part");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Has the RS time leasing
	 * @return true if time leasing for this RS is enabled
	 * @throws Exception failed to retrive leasing information
	 */
	public boolean hasTimeLeasing() throws Exception{
		if(lrs!=null){
			try{
				Date rsdate = lrs.getTimeLeasing();
				if (rsdate!=null){
					Date zerodate = new Date(0); 
					return (rsdate.getTime() != zerodate.getTime());
				}
				return false;
			}catch(Exception e){
				log.error("Could not retrieve current content part. Throwing Exception",e);
				throw new Exception("Could not retrieve current content part");
			}
		}
		else if(rrs!=null){
			try{
				Date rsdate = rrs.getTimeLeasing(this.locator.getSessionToken());
				if (rsdate!=null){
					Date zerodate = new Date(0); 
					return (rsdate.getTime() != zerodate.getTime());
				}
				return false;
			}catch(Exception e){
				log.error("Could not retrieve current content part. Throwing Exception",e);
				throw new Exception("Could not retrieve current content part");
			}
		}
		else{
			log.error("Initialization not complete. Throwing Exception");
			throw new Exception("Initialization not complete");
		}
	}

	/**
	 * Is the RS secured?
	 * @return true if the RS is encrypted
	 */
	public boolean isSecure() {
		return (locator.getPrivKey() != null);
	}

	/**
	 * Is the RS in a scope?
	 * @return true if the RS is scoped
	 */
	public boolean isScoped() {
		return (locator.getScope() != null);
	}

}
