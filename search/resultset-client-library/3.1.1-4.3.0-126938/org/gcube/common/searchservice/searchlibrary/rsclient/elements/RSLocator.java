package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import java.net.URI;
import java.security.PrivateKey;

import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.WSRSSessionToken;

/**
 * This class is used to uniquely identify a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * and make its descovery possible
 * 
 * @author UoA
 */
public class RSLocator {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSLocator.class);
	/**
	 * The type of the RS this instance locates
	 */
	private RSResourceType rsType;
	/**
	 * The location string
	 */
	private String locator;
	/**
	 * The session token if supported
	 */
	private WSRSSessionToken WStoken=null;
	
	
	private GCUBEScope scope = null;
	
	private PrivateKey privKey = null;
	
	/**
	 * Set the scope of the RS
	 * @param new_scope the scope
	 */
	public void setScope(GCUBEScope new_scope)
	{
		scope = new_scope;
	}
	
	
	/**
	 * get the scope of the RS
	 * @return the scope
	 */
	public GCUBEScope getScope()
	{
		return scope;
	}
	
	
	/**
	 * Creates a new {@link RSLocator}
	 * 
	 * @param rsType The type of the resource this locator identifies
	 * @param locator The locator string
	 */
	public RSLocator(RSResourceType rsType,String locator){
		this.rsType=rsType;
		this.locator=locator;
		this.WStoken=null;
		this.scope=null;
	}

	/**
	 * Creates a new {@link RSLocator}
	 * 
	 * @param rsType The type of the resource this locator identifies
	 * @param scope The RS scope
	 * @param locator The locator string
	 */
	public RSLocator(RSResourceType rsType,String locator, GCUBEScope scope){
		this.rsType=rsType;
		this.locator=locator;
		this.WStoken=null;
		this.scope=scope;
	}

	/**
	 * Creates a new {@link RSLocator}
	 * 
	 * @param locator The locator string
	 * @throws Exception Unrecoverable for the operation error occured
	 */
	public RSLocator(String locator) throws Exception{
		this.locator=locator;
		if(RSResourceWSRFType.isOfType(locator)) rsType=new RSResourceWSRFType();
		else if(RSResourceWSType.isOfType(locator)) rsType=new RSResourceWSType();
		else rsType=new RSResourceLocalType();
		this.WStoken=null;
		this.scope=null;
	}

	/**
	 * Creates a new {@link RSLocator}
	 * 
	 * @param locator The locator string
	 * @param scope The RS scope
	 * @throws Exception Unrecoverable for the operation error occured
	 */
	public RSLocator(String locator, GCUBEScope scope) throws Exception{
		this.locator=locator;
		if(RSResourceWSRFType.isOfType(locator)) rsType=new RSResourceWSRFType();
		else if(RSResourceWSType.isOfType(locator)) rsType=new RSResourceWSType();
		else rsType=new RSResourceLocalType();
		this.WStoken=null;
		this.scope=scope;
	}
	
//	/**
//	 * Creates a new {@link RSLocator}
//	 * 
//	 * @param epr The {@link EndpointReferenceType} to use
//	 * @throws Exception An unrecoverable for the operation error occured
//	 */
//	public RSLocator(EndpointReferenceType epr) throws Exception{
//		try{
//			rsType=new RSResourceWSRFType();
//			locator=ObjectSerializer.toString(epr,ResultSetQNames.RESOURCE_REFERENCE);
//		}catch(Exception e){
//			log.error("Could not serialize provided epr. Throwing Exception",e);
//			throw new Exception("Could not serialize provided epr");
//		}
//	}
	
//	/**
//	 * Creates a new {@link RSLocator}
//	 * 
//	 * @param epr The {@link EndpointReferenceType} to use
//	 * @throws Exception An unrecoverable for the operation error occured
//	 */
//	public RSLocator(WSRSSessionToken epr) throws Exception{
//		try{
//			rsType=new RSResourceWSType();
//			locator=WSRSSessionToken.serialize(epr);
//		}catch(Exception e){
//			log.error("Could not serialize provided epr. Throwing Exception",e);
//			throw new Exception("Could not serialize provided epr");
//		}
//	}

	/**
	 * Retrieves the type of resource this locator identifies 
	 * 
	 * @return The type of the resource
	 */
	public RSResourceType getRSResourceType(){
		return rsType;
	}

	/**
	 * Restrieves the locator string
	 * 
	 * @return The locator string
	 */
	public String getLocator(){
		return locator;
	}
	
	/**
	 * If the type of resource supports it, this operation retrieves the URI of the web service through
	 * which the resource is available
	 * 
	 * @return The URI of the web service through which the resource is available 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI getURI() throws Exception{
		if (rsType instanceof RSResourceWSRFType) {
			return RSResourceWSRFType.getURI(locator);
		}
		else if(rsType instanceof RSResourceWSType) {
			return RSResourceWSType.getURI(locator);
		}
		log.error("Cannot get URI from RSResourceLocalType. Throwing Exception");
		throw new Exception("Cannot get URI from RSResourceLocalType");
	}
	
	/**
	 * retrieves the session token if the specific RSLocator supports it
	 * 
	 * @return the session token
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public String getSessionToken() throws Exception{
		try{
			if (rsType instanceof RSResourceWSRFType || rsType instanceof RSResourceLocalType) return ""; 
			if(this.WStoken==null) this.WStoken=WSRSSessionToken.deserialize(locator);
			return this.WStoken.getSessionToken();
		}catch(Exception e){
			log.error("problem parsing the locator or the locator does not support session tokens. throwing exceptions",e);
			throw new Exception("problem parsing the locator or the locator does not support session tokens");
		}
	}

	/**
	 * Get the private key of the RS
	 * @return the private key
	 */
	public PrivateKey getPrivKey() {
		return privKey;
	}

	/**
	 * Set the private key of the RS
	 * @param privKey the key
	 */
	public void setPrivKey(PrivateKey privKey) {
		this.privKey = privKey;
	}
}
