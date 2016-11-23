package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import java.net.URI;
import org.apache.log4j.Logger;

/**
 * Indicates thet the resource is of WSRF type
 * 
 * @author UoA
 */
public class RSResourceWSRFType implements RSResourceType{
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSResourceWSRFType.class);
	/**
	 * The service path
	 */
	private static String ResultSetServicePath="gcube/common/searchservice/ResultSet";
	
	/**
	 * The end point to the service 
	 */
	private URI serviceEndPoint;
	
	/**
	 * checks if it is wsrf type
	 * 
	 * @param locatorString the locator
	 * @return whether or not it is wsrf type
	 */
	public static boolean isOfType(String locatorString){
		return RSTypeWrapper.isWSRFType(locatorString);
	}
	
	/**
	 * Retrieves the URI of the locator
	 * 
	 * @param locatorString the locator
	 * @return the URI
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static URI getURI(String locatorString) throws Exception{
		return RSTypeWrapper.getURIofWSRF(locatorString);
	}
	
	/**
	 * Creates a new {@link RSResourceWSRFType} initializing the service end point to the ResultSetService
	 * @see ServiceHost#getBaseURL()
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSResourceWSRFType() throws Exception{
		try{
			this.serviceEndPoint=new URI(RSTypeWrapper.retrieveServiceHostBasePath().toString()+RSResourceWSRFType.ResultSetServicePath);
		}catch(Exception e){
			log.error("Could not retrieve container configuration. Throwing Exception",e);
			throw new Exception ("Could not retrieve container configuration"); 
		}
	}
	
	/**
	 * Creates a new {@link RSResourceWSRFType} initializing the service end point to the ResultSetService
	 * pointed to by the provided service path
	 * 
	 * @param fullPath the full path to the service
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSResourceWSRFType(String fullPath) throws Exception{
		try{
			this.serviceEndPoint=new URI(fullPath);
		}catch(Exception e){
			log.error("Could not create URI for ResultSetService.Throwing Exception",e);
			throw new Exception("Could not create URI for ResultSetService");
		}
	}
	
	/**
	 * Retrieves the URI of the service that is the fron end to the resources
	 * 
	 * @return The service URI
	 */
	public URI getServiceEndPoint(){
		return serviceEndPoint;
	}

	/**
	 * Sets the service end point
	 * 
	 * @param serviceEndPoint The URI to the service en point
	 * @deprecated
	 */
	public void setServiceEndPoint(URI serviceEndPoint){
		this.serviceEndPoint=serviceEndPoint;
	}
}
