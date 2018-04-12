package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.util.UUID;

/**
 * reference to a ResultSet resource of a stateless service
 * 
 * @author UoA
 */
public class WSRSSessionToken {
	/**
	 * The service instance
	 */
	private String serviceInstance=null;
	/**
	 * The session token
	 */
	private String token=null;
	
	/**
	 * Constructor
	 * 
	 * @param serviceInstance the service instance
	 * @param token the session token
	 */
	public WSRSSessionToken(String serviceInstance,String token){
		this.serviceInstance=serviceInstance;
		this.token=token;
	}
	
	/**
	 * retrieve service instance
	 * 
	 * @return the service isntance
	 */
	public String getServiceInstance(){
		return this.serviceInstance;
	}
	
	/**
	 * retrieve session token
	 * 
	 * @return the session token
	 */
	public String getSessionToken(){
		return this.token;
	}
	
	/**
	 * generates a GUID for the session token
	 * 
	 * @return the session token
	 */
	public static String generateSessionToken(){
		return UUID.randomUUID().toString();
	}
	
	/**
	 * serialize the given session token
	 * 
	 * @param session the session token to serialize
	 * @return the serialized session token
	 */
	public static String serialize(WSRSSessionToken session){
		return session.getServiceInstance()+"?"+session.getSessionToken();
	}
	
	/**
	 * desirializes the given session token
	 * 
	 * @param session the serialized session token
	 * @return the session token instnace
	 * @throws Exception the serialization is not valid
	 */
	public static WSRSSessionToken deserialize(String session) throws Exception{
		int index=session.lastIndexOf("?");
		if(index<=0 || index==session.length()-1) throw new Exception("invalid serialization "+session);
		return new WSRSSessionToken(session.substring(0,index),session.substring(index+1,session.length()));
	}
}
