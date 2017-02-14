package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import java.net.URI;

import org.apache.log4j.Logger;

/**
 * Indicates thet the resource is of WS type
 * 
 * @author UoA
 */
public class RSResourceWSType implements RSResourceType{
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSResourceWSType.class);
	/**
	 * The service path
	 */
	private static String ResultSetServicePath="gcube/common/searchservice/ResultSet";
	
	/**
	 * The end point to the service 
	 */
	private URI serviceEndPoint;
	
	/**
	 * checks if it is ws type
	 * 
	 * @param locatorString the locator
	 * @return whether or not it is ws type
	 */
	public static boolean isOfType(String locatorString){
		return RSTypeWrapper.isWSType(locatorString);
	}
	
	/**
	 * Retrieves the URI of the locator
	 * 
	 * @param locatorString the locator
	 * @return the URI
	 * @throws Exception An unrecoverable for the operation orror occured
	 */
	public static URI getURI(String locatorString) throws Exception{
		return RSTypeWrapper.getURIofWS(locatorString);
	}
	
	/**
	 * Creates a new {@link RSResourceWSType} initializing the service end point to the ResultSetService
	 * @see ServiceHost#getBaseURL()
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSResourceWSType() throws Exception{
		try{
			this.serviceEndPoint=new URI(RSTypeWrapper.retrieveServiceHostBasePath().toString()+RSResourceWSType.ResultSetServicePath);
		}catch(Exception e){
			log.error("Could not retrieve container configuration. Throwing Exception",e);
			throw new Exception ("Could not retrieve container configuration"); 
		}
	}
	
	/**
	 * Creates a new {@link RSResourceWSType} initializing the service end point to the ResultSetService
	 * pointed to by the provided service path
	 * 
	 * @param fullPath the full path to the service
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSResourceWSType(String fullPath) throws Exception{
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
}
