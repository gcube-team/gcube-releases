package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import java.io.StringReader;
import java.net.URI;
import java.security.PrivateKey;
import java.util.Date;
//import java.io.File; //ws core 4.1 specific for attachments
//import java.io.FileOutputStream; //ws core 4.1 specific for attachments
//import javax.activation.DataHandler; //ws core 4.1 specific for attachments
//import org.apache.axis.attachments.AttachmentPart; //ws core 4.1 specific for attachments
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.gcube.common.searchservice.resultsetservice.ResultSetQNames;
import org.gcube.common.searchservice.resultsetservice.stubs.*;
import org.gcube.common.searchservice.resultsetservice.stubs.service.ResultSetServiceAddressingLocator;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.WSRSSessionToken;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.xml.sax.InputSource;

import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;

/**
 * Wrapps over the different types of remote RS service implementations and corresponding technologies
 * 
 * @author UoA
 */
public class RSTypeWrapper {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSTypeWrapper.class);
	
	/**
	 * The porttype
	 */
	private ResultSetPortType rrs=null;
	
	/**
	 * The locator
	 */
	private RSLocator locator=null;
	/**
	 * The port
	 */
	private int port=-1;
	/**
	 * SSL support
	 */
	private boolean SSLsupport = true;
	/**
	 * checks if it is wsrf type
	 * 
	 * @param locatorString the locator
	 * @return whether or not it is wsrf type
	 */
	public static boolean isWSRFType(String locatorString){
		try{
			ObjectDeserializer.deserialize(new InputSource(new StringReader(locatorString)),EndpointReferenceType.class);
			return true;
		}catch (Exception e){
			return false;
		}
	}
	
	/**
	 * Retrieves the port
	 * 
	 * @return the port
	 */
	public int getPort(){
		return this.port;
	}
	
	/**
	 * Retrieves the SSL support
	 * 
	 * @return the SSL support
	 */
	public boolean getSSLsupport(){
		return this.SSLsupport;
	}

	/**
	 * checks if it is ws type
	 * 
	 * @param locatorString the locator
	 * @return whether or not it is ws type
	 */
	public static boolean isWSType(String locatorString){
		try{
			WSRSSessionToken.deserialize(locatorString);
			return true;
		}catch (Exception e){
			return false;
		}
	}
	
	/**
	 * retireves the URI of the locator
	 * 
	 * @param locatorString the locator
	 * @return the URI
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static URI getURIofWSRF(String locatorString)throws Exception {
		EndpointReferenceType instanceEPR = (EndpointReferenceType) ObjectDeserializer.deserialize(new InputSource(new StringReader(locatorString)),EndpointReferenceType.class);
		return new URI(instanceEPR.getAddress().toString());
	}
	
	/**
	 * retireves the URI of the locator
	 * 
	 * @param locatorString the locator
	 * @return the URI
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static URI getURIofWS(String locatorString)throws Exception {
		WSRSSessionToken instanceEPR = WSRSSessionToken.deserialize(locatorString);
		return new URI(instanceEPR.getServiceInstance());
	}
	
	/**
	 * retrieves the base path of the service host
	 * 
	 * @return the base path
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static URI retrieveServiceHostBasePath()throws Exception {
		return new URI(ServiceHost.getBaseURL().toString());
	}
	
	/**
	 * Creates a locator from an Endpoint reference type
	 * 
	 * @param epr the endpoint reference type
	 * @return the locator
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static RSLocator getLocator(EndpointReferenceType epr) throws Exception{
		return new RSLocator(ObjectSerializer.toString(epr,ResultSetQNames.RESOURCE_REFERENCE));
	}

	
	
	/**
	 * retrieves a wsrf locator
	 * 
	 * @param serviceEndPoint the service end point
	 * @param headFileName the head filename
	 * @param scope the scope
 	 * @param pKey the RS private key
	 * @return the locator
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static RSLocator retrieveWSRFLocator( String serviceEndPoint, String headFileName,
								GCUBEScope scope , PrivateKey pKey) throws Exception {
//		if (scope != null) System.out.println("In retrieveWSRFLocator "+scope.getName()); else System.out.println("In retrieveWSRFLocator null"); 
		RSLocator remoteLocator=null;
		EndpointReferenceType instanceEPR=null;
		EndpointReferenceType factoryEPR = new EndpointReferenceType();
		try{
			factoryEPR.setAddress(new Address(serviceEndPoint));
		}catch(Exception e){
			log.error("could not initiliaze factory EPR. Throwing Exception",e);
			throw new Exception("could not initiliaze factory EPR");
		}
		ResultSetPortType resultsetFactory;
		ResultSetServiceAddressingLocator factoryLocator = new ResultSetServiceAddressingLocator();
		try{
			resultsetFactory = factoryLocator.getResultSetPortTypePort(factoryEPR);
			if (scope != null)
				resultsetFactory=GCUBERemotePortTypeContext.getProxy( resultsetFactory, scope);
			
		}catch (Exception e){
			log.error("could not initiliaze factory porttype. Throwing Exception",e);
			throw new Exception("could not initiliaze factory porttype");
		}
		try{
			WrapResourceRequest req=new WrapResourceRequest();
			req.setHeadFileName(headFileName);
			req.setResourceType(RSConstants.RESOURCETYPE.WSRF.toString());
			if (pKey != null){
				req.setPrivateKey(new sun.misc.BASE64Encoder().encode(pKey.getEncoded()).getBytes());
			}
			WrapResourceResponse wrapResponse=resultsetFactory.wrapResource(req);
			instanceEPR = wrapResponse.getEndpointReference();
		}catch (Exception e){
			log.error("could not wrap local resultset. Throwing Exception",e);
			throw new Exception("could not wrap local resultset");
		}
		try{
			remoteLocator=new RSLocator(ObjectSerializer.toString(instanceEPR,ResultSetQNames.RESOURCE_REFERENCE));
			remoteLocator.setScope(scope);
		}catch(Exception e){
			log.error("Could not create RSLocator",e);
			throw new Exception("Could not create RSLocator");
		}
		return remoteLocator;
	}

	/**
	 * retrieves a wsrf locator
	 * 
	 * @param serviceEndPoint the sertvice end point
	 * @param headFileName the head filename
	 * @return the locator
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSLocator retrieveWSRFLocator(String serviceEndPoint, String headFileName) throws Exception {
		return retrieveWSRFLocator(serviceEndPoint, headFileName, null, null);
	}

	/**
	 * retrieves a wsrf locator
	 * 
	 * @param serviceEndPoint the sertvice end point
	 * @param headFileName the head filename
	 * @param pKey the private key
	 * @return the locator
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSLocator retrieveWSRFLocator(String serviceEndPoint, String headFileName, PrivateKey pKey) throws Exception {
		return retrieveWSRFLocator(serviceEndPoint, headFileName, null, pKey);
	}

	/**
	 * retrieves a wsrf locator
	 * 
	 * @param serviceEndPoint the sertvice end point
	 * @param headFileName the head filename
	 * @param scope the scope
	 * @return the locator
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSLocator retrieveWSRFLocator( String serviceEndPoint, String headFileName, GCUBEScope scope) throws Exception {
		return retrieveWSRFLocator(serviceEndPoint, headFileName, scope, null);
	}

	/**
	 * retrieves a ws locator
	 * 
	 * @param serviceEndPoint the sertvice end point
	 * @param headFileName the headFileName
	 * @param scope the scope
 	 * @param pKey the RS private key
	 * @throws Exception An unrecoverable for the operation error occured
	 * @return the locator
	 */
	public static RSLocator retrieveWSLocator( String serviceEndPoint, String headFileName, 
								GCUBEScope scope , PrivateKey pKey) throws Exception {
//		if (scope != null) System.out.println("In retrieveWSLocator "+scope.getName()); else System.out.println("In retrieveWSLocator null"); 
		RSLocator remoteLocator=null;
		EndpointReferenceType endpoint = new EndpointReferenceType();
		endpoint.setAddress(new Address(serviceEndPoint));
		ResultSetServiceAddressingLocator rlocator= new ResultSetServiceAddressingLocator();
		ResultSetPortType trrs= rlocator.getResultSetPortTypePort(endpoint);
		if (scope != null)
			trrs=GCUBERemotePortTypeContext.getProxy(trrs,scope);
		
		WrapResourceRequest req=new WrapResourceRequest();;
		req.setHeadFileName(headFileName);
		req.setResourceType(RSConstants.RESOURCETYPE.WS.toString());
		if (pKey != null){
			req.setPrivateKey(new sun.misc.BASE64Encoder().encode(pKey.getEncoded()).getBytes());
		}
		WrapResourceResponse wrapResourceResponse=trrs.wrapResource(req);
		remoteLocator=new RSLocator(wrapResourceResponse.getSessionToken());
		remoteLocator.setScope(scope);
		return remoteLocator;
	}

	/**
	 * retrieves a ws locator
	 * 
	 * @param serviceEndPoint the sertvice end point
	 * @param headFileName the headFileName
	 * @param scope The RS scope
	 * @throws Exception An unrecoverable for the operation error occured
	 * @return the locator
	 */
	public static RSLocator retrieveWSLocator( String serviceEndPoint, String headFileName, GCUBEScope scope)throws Exception{
		return retrieveWSLocator(serviceEndPoint, headFileName, scope, null);
	}

	/**
	 * retrieves a ws locator
	 * 
	 * @param serviceEndPoint the sertvice end point
	 * @param headFileName the headFileName
	 * @throws Exception An unrecoverable for the operation error occured
	 * @return the locator
	 */
	public static RSLocator retrieveWSLocator(String serviceEndPoint,String headFileName)throws Exception{
		return retrieveWSLocator(serviceEndPoint, headFileName, null, null);
	}

	/**
	 * retrieves a ws locator
	 * 
	 * @param serviceEndPoint the sertvice end point
	 * @param headFileName the headFileName
	 * @param pKey The private key
	 * @throws Exception An unrecoverable for the operation error occured
	 * @return the locator
	 */
	public static RSLocator retrieveWSLocator(String serviceEndPoint,String headFileName, PrivateKey pKey)throws Exception{
		return retrieveWSLocator(serviceEndPoint, headFileName, null, pKey);
	}

	/**
	 * initialize a new instance
	 * 
	 * @param locator the locator
	 * @param scope the scope
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	private void InitRSTypeWrapper(RSLocator locator, GCUBEScope scope,
												PrivateKey pKey) throws Exception{
//		if (scope != null) System.out.println("In InitRSTypeWrapper "+scope.getName()); else System.out.println("In InitRSTypeWrapper null"); 
		if(locator.getRSResourceType() instanceof RSResourceWSRFType) {
			try{
				EndpointReferenceType instanceEPR=null;
				ResultSetServiceAddressingLocator instanceLocator = new ResultSetServiceAddressingLocator();
				ResultSetPortType resultset=null;
				try{
					instanceEPR = (EndpointReferenceType) ObjectDeserializer.deserialize(new InputSource(new StringReader(locator.getLocator())),EndpointReferenceType.class);
				}catch(Exception e){
					log.error("Could not retrieve EndPointReferenceType. Throwsing Exception",e);
					throw new Exception("Could not retrieve EndPointReferenceType");
				}
				try{
					resultset=instanceLocator.getResultSetPortTypePort(instanceEPR);
					if (scope != null)
						resultset=GCUBERemotePortTypeContext.getProxy(resultset,scope);
					
				}catch(Exception e){
					log.error("Could not retrieve PortType. Throwsing Exception",e);
					throw new Exception("Could not retrieve PortType");
				}
				try{
					AccessResourceRequest req=new AccessResourceRequest();
					req.setSessionToken(locator.getSessionToken());
					req.setResourceType(RSConstants.RESOURCETYPE.WSRF.toString());
					if (pKey != null){
						req.setPrivateKey(new sun.misc.BASE64Encoder().encode(pKey.getEncoded()).getBytes());
					}

					AccessResourceResponse accessResourceResponse=resultset.accessResource(req);
					instanceEPR = accessResourceResponse.getEndpointReference();
					this.port =accessResourceResponse.getPort();
					this.SSLsupport = accessResourceResponse.isSSLsupport();
				}catch(Exception e){
					log.error("Could not retrieve EPR to new Resource. Throwsing Exception",e);
					throw new Exception("Could not retrieve EPR to new Resource");
				}
				try{
					rrs=instanceLocator.getResultSetPortTypePort(instanceEPR);
					if (scope != null)
						rrs=GCUBERemotePortTypeContext.getProxy(rrs,scope);
										
				}catch (Exception e){
					log.error("Could not retrieve porttype to new Resource. Throwsing Exception",e);
					throw new Exception("Could not retrieve porttype to new Resource");
				}
				try{
					this.locator=new RSLocator(ObjectSerializer.toString(instanceEPR,ResultSetQNames.RESOURCE_REFERENCE));
					this.locator.setScope(scope);
					this.locator.setPrivKey(pKey);
				}catch(Exception e){
					log.error("Could not create locator for new Resource. Throwsing Exception",e);
					throw new Exception("Could not create locator for new Resource");
				}
			}catch(Exception e){
				log.error("Could not created RSLocationWrapper. Throwsing Exception",e);
				throw new Exception("Could not created RSLocationWrapper");
			}
		}
		else if(locator.getRSResourceType() instanceof RSResourceWSType) {
			try{
				EndpointReferenceType endpoint = new EndpointReferenceType();
				endpoint.setAddress(new Address(locator.getURI().toString()));
				ResultSetServiceAddressingLocator rlocator= new ResultSetServiceAddressingLocator();
				rrs= rlocator.getResultSetPortTypePort(endpoint);
				if (scope != null)
					rrs=GCUBERemotePortTypeContext.getProxy(rrs,scope);

				AccessResourceRequest req=new AccessResourceRequest();
				req.setSessionToken(locator.getSessionToken());
				req.setResourceType(RSConstants.RESOURCETYPE.WS.toString());
				if (pKey != null){
					req.setPrivateKey(new sun.misc.BASE64Encoder().encode(pKey.getEncoded()).getBytes());
				}

				AccessResourceResponse accessResourceResponse=rrs.accessResource(req);
				this.port =accessResourceResponse.getPort();
				this.SSLsupport = accessResourceResponse.isSSLsupport();
				this.locator=new RSLocator(accessResourceResponse.getSessionToken());
				this.locator.setScope(scope);
				this.locator.setPrivKey(pKey);
			}catch(Exception e){
				log.error("Could not created RSLocationWrapper. Throwsing Exception",e);
				throw new Exception("Could not created RSLocationWrapper");
			}
		}
		else{
			log.error("Unknown RSResourceType. Throwing Exception");
			throw new Exception("Unknown RSResourceType");
		}
	}

	
	/**
	 * Creates a new instace
	 * 
	 * @param type the type of resource to crate
	 * @param headFileName the head filename
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSResourceType type,String headFileName)throws Exception{
		InitRSTypeWrapper(type, headFileName, 
				null, null);
	}

	/**
	 * Creates a new instance
	 * 
	 * @param type the type of resource to crate
	 * @param headFileName the head filename
	 * @param scope The RS scope
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSResourceType type,String headFileName, GCUBEScope scope)throws Exception{
		InitRSTypeWrapper(type, headFileName, scope, null);
	}

	/**
	 * Creates a new instance
	 * 
	 * @param type the type of resource to crate
	 * @param headFileName the head filename
	 * @param scope The RS scope
	 * @param pKey The private key
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSResourceType type,String headFileName, GCUBEScope scope, PrivateKey pKey)throws Exception{
		InitRSTypeWrapper(type, headFileName, scope, pKey);
	}

	/**
	 * Creates a new instance
	 * 
	 * @param type the type of resource to crate
	 * @param headFileName the head filename
	 * @param pKey The private key
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSResourceType type,String headFileName, PrivateKey pKey)throws Exception{
		InitRSTypeWrapper(type, headFileName,
				null, pKey);
	}

	/**
	 * Initialize a new instace
	 * 
	 * @param type the type of resource to crate
	 * @param headFileName the head filename
	 * @param scope the scope
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	private void InitRSTypeWrapper(RSResourceType type,String headFileName, GCUBEScope scope, PrivateKey pKey)throws Exception{
//		if (scope != null) System.out.println("In InitRSTypeWrapper "+scope.getName()); else System.out.println("In InitRSTypeWrapper null"); 
		if(type instanceof RSResourceWSRFType) {
			EndpointReferenceType instanceEPR=null;
			EndpointReferenceType factoryEPR = new EndpointReferenceType();
			try{
				factoryEPR.setAddress(new Address(((RSResourceWSRFType)type).getServiceEndPoint().toString()));
			}catch(Exception e){
				log.error("could not initiliaze factory EPR. Throwinf Exception",e);
				throw new Exception("could not initiliaze factory EPR");
			}
			ResultSetPortType resultsetFactory;
			ResultSetServiceAddressingLocator factoryLocator = new ResultSetServiceAddressingLocator();
			try{
				resultsetFactory = factoryLocator.getResultSetPortTypePort(factoryEPR);
				if (scope != null)
					resultsetFactory = GCUBERemotePortTypeContext.getProxy(resultsetFactory, scope);

			}catch (Exception e){
				log.error("could not initiliaze factory porttype. Throwinf Exception",e);
				throw new Exception("could not initiliaze factory porttype");
			}
			try{
				WrapResourceRequest req=new WrapResourceRequest();;
				req.setHeadFileName(headFileName);
				req.setResourceType(RSConstants.RESOURCETYPE.WSRF.toString());
				if (pKey != null){
					req.setPrivateKey(new sun.misc.BASE64Encoder().encode(pKey.getEncoded()).getBytes());
				}

				WrapResourceResponse wrapResponse=resultsetFactory.wrapResource(req);
				this.port =wrapResponse.getPort();
				this.SSLsupport = wrapResponse.isSSLsupport();
				instanceEPR = wrapResponse.getEndpointReference();
			}catch (Exception e){
				log.error("could not wrap local resultset. Throwing Exception",e);
				throw new Exception("could not wrap local resultset");
			}
			try{
				this.locator = new RSLocator(ObjectSerializer.toString(instanceEPR,ResultSetQNames.RESOURCE_REFERENCE));
				this.locator.setScope(scope);
				this.locator.setPrivKey(pKey);
			}catch(Exception e){
				log.error("Could not create RSLocator",e);
				throw new Exception("Could not create RSLocator");
			}
		}
		else if(type instanceof RSResourceWSType) {
			try{
				WrapResourceRequest req=new WrapResourceRequest();;
				req.setHeadFileName(headFileName);
				req.setResourceType(RSConstants.RESOURCETYPE.WS.toString());
				EndpointReferenceType endpoint = new EndpointReferenceType();
				endpoint.setAddress(new Address(((RSResourceWSType)type).getServiceEndPoint().toString()));
				ResultSetServiceAddressingLocator rlocator= new ResultSetServiceAddressingLocator();
				ResultSetPortType trrs= rlocator.getResultSetPortTypePort(endpoint);
				if (scope != null)
					trrs=GCUBERemotePortTypeContext.getProxy(trrs,scope);
				if (pKey != null){
					req.setPrivateKey(new sun.misc.BASE64Encoder().encode(pKey.getEncoded()).getBytes());
				}

				
				WrapResourceResponse wrapResourceResponse=trrs.wrapResource(req);
				this.port =wrapResourceResponse.getPort();
				this.SSLsupport = wrapResourceResponse.isSSLsupport();
				this.locator = new RSLocator(wrapResourceResponse.getSessionToken());
				this.locator.setScope(scope);
				this.locator.setPrivKey(pKey);
			}catch(Exception e){
				log.error("Could not wrap resource. Throwsing Exception",e);
				throw new Exception("Could not wrap resource");
			}
		}
		else{
			log.error("Not suported RSResourceType. Throwing Exception");
			throw new Exception("Not suported RSResourceType");
		}
	}
	
	/**
	 * initialize a new instance
	 * 
	 * @param type the type of resource
	 * @param headName the head name of the rs
	 * @param serviceEndPoint the end poit of the service
	 * @param scope the scope of the service
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	private void InitRSTypeWrapper(RSResourceType type, String headName, String serviceEndPoint,
											GCUBEScope scope, PrivateKey pKey) throws Exception {
//		if (scope != null) System.out.println("In InitRSTypeWrapper "+scope.getName()); else System.out.println("In InitRSTypeWrapper null"); 
		if(type instanceof RSResourceWSRFType) {
			EndpointReferenceType instanceEPR=null;
			EndpointReferenceType factoryEPR = new EndpointReferenceType();
			try{
				factoryEPR.setAddress(new Address(serviceEndPoint));
			}catch(Exception e){
				log.error("could not initiliaze factory EPR. Throwinf Exception",e);
				throw new Exception("could not initiliaze factory EPR");
			}
			ResultSetPortType resultsetFactory;
			ResultSetServiceAddressingLocator factoryLocator = new ResultSetServiceAddressingLocator();
			try{
				resultsetFactory = factoryLocator.getResultSetPortTypePort(factoryEPR);
				if (scope != null)
					resultsetFactory=GCUBERemotePortTypeContext.getProxy(resultsetFactory,scope);

			}catch (Exception e){
				log.error("could not initiliaze factory porttype. Throwinf Exception",e);
				throw new Exception("could not initiliaze factory porttype");
			}
			try{
				WrapResourceRequest req=new WrapResourceRequest();;
				req.setHeadFileName(headName);
				req.setResourceType(RSConstants.RESOURCETYPE.WSRF.toString());
				if (pKey != null){
					req.setPrivateKey(new sun.misc.BASE64Encoder().encode(pKey.getEncoded()).getBytes());
				}

				WrapResourceResponse wrapResponse=resultsetFactory.wrapResource(req);
				this.port =wrapResponse.getPort();
				this.SSLsupport = wrapResponse.isSSLsupport();
				instanceEPR = wrapResponse.getEndpointReference();
			}catch (Exception e){
				log.error("could not wrap local resultset. Throwing Exception",e);
				throw new Exception("could not wrap local resultset");
			}
			try{
				this.locator =new RSLocator(ObjectSerializer.toString(instanceEPR,ResultSetQNames.RESOURCE_REFERENCE));
				this.locator.setScope(scope);
				this.locator.setPrivKey(pKey);
			}catch(Exception e){
				log.error("Could not create RSLocator",e);
				throw new Exception("Could not create RSLocator");
			}
		}
		else if(type instanceof RSResourceWSType) {
			try{
				EndpointReferenceType endpoint = new EndpointReferenceType();
				endpoint.setAddress(new Address(serviceEndPoint));
				ResultSetServiceAddressingLocator rlocator= new ResultSetServiceAddressingLocator();
				ResultSetPortType trrs= rlocator.getResultSetPortTypePort(endpoint);
				if (scope != null)
					trrs=GCUBERemotePortTypeContext.getProxy(trrs,scope);
				
				WrapResourceRequest req=new WrapResourceRequest();;
				req.setHeadFileName(headName);
				req.setResourceType(RSConstants.RESOURCETYPE.WS.toString());
				if (pKey != null){
					req.setPrivateKey(new sun.misc.BASE64Encoder().encode(pKey.getEncoded()).getBytes());
				}

				WrapResourceResponse wrapResourceResponse=trrs.wrapResource(req);
				this.port =wrapResourceResponse.getPort();
				this.SSLsupport = wrapResourceResponse.isSSLsupport();
				this.locator= new RSLocator(wrapResourceResponse.getSessionToken());
				this.locator.setScope(scope);
				this.locator.setPrivKey(pKey);
			}catch(Exception e){
				log.error("could not wrap local resultset. Throwsing Exception",e);
				throw new Exception("could not wrap local resultset");
			}
		}
		else{
			log.error("Not supported RSResourceType. Throwing Exception");
			throw new Exception("Not supported RSResourceType");
		}
	}
	

	/**
	 * creates a new instance
	 * 
	 * @param locator the locator
	 * @param scope the scope
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSLocator locator, GCUBEScope scope) throws Exception{
		InitRSTypeWrapper(locator, scope, locator.getPrivKey());
	}

	/**
	 * creates a new instance
	 * 
	 * @param locator the locator
	 * @param scope the scope
	 * @param pKey the private key
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSLocator locator, GCUBEScope scope, PrivateKey pKey) throws Exception{
		InitRSTypeWrapper(locator, scope, pKey);
	}


	/**
	 * creates a new instance
	 * 
	 * @param locator the locator
	 * @param pKey the private key
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSLocator locator, PrivateKey pKey) throws Exception{
		GCUBEScope scope = locator.getScope();
		InitRSTypeWrapper(locator, scope, pKey);
	}

	
	/**
	 * creates a new instance
	 * 
	 * @param locator the locator
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSLocator locator)throws Exception{
		GCUBEScope scope = locator.getScope();
		PrivateKey pkey = locator.getPrivKey();
		InitRSTypeWrapper(locator, scope, pkey);
	}
	
	
	/**
	 * creates a new instance
	 * 
	 * @param type the type of resource
	 * @param headName the head name of the rs
	 * @param serviceEndPoint the end poit of the service
	 * @param scope the scope of the service
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSResourceType type, String headName, String serviceEndPoint, GCUBEScope scope) throws Exception {
		InitRSTypeWrapper(type, headName, serviceEndPoint, scope, null);
	}

	/**
	 * creates a new instance
	 * 
	 * @param type the type of resource
	 * @param headName the head name of the rs
	 * @param serviceEndPoint the end poit of the service
	 * @param scope the scope of the service
	 * @param pKey the private key
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSResourceType type, String headName, String serviceEndPoint,
			GCUBEScope scope, PrivateKey pKey) throws Exception {
		InitRSTypeWrapper(type, headName, serviceEndPoint, scope, pKey);		
	}
	
	/**
	 * creates a new instance
	 * 
	 * @param type the type of resource
	 * @param headName the head name of the rs
	 * @param serviceEndPoint the end poit of the service
	 * @param pKey the private key
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSResourceType type, String headName, String serviceEndPoint,
			PrivateKey pKey) throws Exception {
		InitRSTypeWrapper(type, headName, serviceEndPoint,
				null, pKey);		
	}

	/**
	 * creates a new instance
	 * 
	 * @param type the type of resource
	 * @param headName the head name of the rs
	 * @param serviceEndPoint the end poit of the service
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTypeWrapper(RSResourceType type, String headName, String serviceEndPoint) throws Exception {
		InitRSTypeWrapper(type, headName, serviceEndPoint,
				 null, null);		
	}

	
	/**
	 * Retrieves the locator
	 * 
	 * @return the locator
	 */
	public RSLocator getLocator(){
		return this.locator;
	}
	
	/**
	 * retrieve the head file name
	 * 
	 * @param sessionToken the session token
	 * @return teh head filename
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getHeadFileName(String sessionToken) throws Exception{
		return rrs.getHeadFileName(sessionToken);
	}
	
	/**
	 * retrieves the streaming port of the remote ResultSet
	 * 
	 * @param sessionToken the session token
	 * @return the stream port
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public CanStreamResponse canStream(String sessionToken) throws Exception{
		return rrs.canStream(sessionToken);
	}
	
	/**
	 * clears underlying structures
	 * 
	 * @param sessionToken the session token
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void clear(String sessionToken) throws Exception{
		rrs.clear(sessionToken);
	}
	
	/**
	 * Retrieves the properties of the3 specified type
	 * 
	 * @param type the type
	 * @param sessionToken the session token
	 * @return the properties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []getProperties(String type,String sessionToken) throws Exception{
		GetPropertiesRequest req=new GetPropertiesRequest();
		req.setSessionToken(sessionToken);
		req.setType(type);
		String []props=rrs.getProperties(req).getProperties();
		if(props==null){
			return new String [0];
		}
		return props;
	}
	
	/**
	 * Checks whether the result set supports flow control
	 * 
	 * @param sessionToken the session token
	 * @return true or false
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public boolean isFlowControled(String sessionToken) throws Exception{
		return rrs.isFlowControled(sessionToken);
	}
	
	/**
	 * moves to next part
	 * 
	 * @param time the time to wait
	 * @param sessionToken the session token
	 * @return whether or not the move was made
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public boolean getNextPart(int time, String sessionToken) throws Exception{
		GetNextPartRequest req=new GetNextPartRequest();
		req.setMaxWaitTime(time);
		req.setSessionToken(sessionToken);
		return rrs.getNextPart(req);
	}
	
	/**
	 * checks if the next part is available
	 * 
	 * @param sessionToken the session token
	 * @return true or false
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public boolean nextAvailable(String sessionToken) throws Exception{
		return rrs.nextAvailable(sessionToken);
	}
	
	/**
	 * moves to previous part
	 * 
	 * @param sessionToken the session token
	 * @return true or false
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean getPreviousPart(String sessionToken) throws Exception{
		return rrs.getPreviousPart(sessionToken);
	}
	
	/**
	 * moves to the first part
	 * 
	 * @param sessionToken the session token
	 * @return true or false
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public boolean getFirstPart(String sessionToken) throws Exception{
		return rrs.getFirstPart(sessionToken);
	}
	
	/**
	 * Retrieves all th custom properties
	 * 
	 * @param sessionToken the session token
	 * @return the serialization of the proeprties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String retrieveCustomProperties(String sessionToken) throws Exception{
		return rrs.retrieveCustomProperties(sessionToken);
	}
	
	/**
	 * Checks if it is the first part
	 * 
	 * @param sessionToken the session token
	 * @return true or false
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isFirst(String sessionToken) throws Exception{
		return rrs.isFirst(sessionToken);
	}
	
	/**
	 * Checks if is last
	 * 
	 * @param sessionToken the session token
	 * @return true or false
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isLast(String sessionToken) throws Exception{
		return rrs.isLast(sessionToken);
	}
	
	/**
	 * retrieves the number of results
	 * 
	 * @param type the type of records
	 * @param sessionToken the session token
	 * @return the number
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public int getNumberOfResults(String type,String sessionToken) throws Exception{
		GetNumberOfResultsRequest req=new GetNumberOfResultsRequest();
		req.setSessionToken(sessionToken);
		req.setType(type);
		return rrs.getNumberOfResults(req);
	}
	
	/**
	 * retriegfves the specified result
	 * 
	 * @param index the index
	 * @param sessionToken the session token
	 * @return the result
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getResult(int index,String sessionToken) throws Exception{
		GetResultRequest req=new GetResultRequest();
		req.setIndex(index);
		req.setSessionToken(sessionToken);
		return rrs.getResult(req);
	}
	
	/**
	 * retrieves all teh results
	 * 
	 * @param sessionToken the session token
	 * @return the results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []getAllResults(String sessionToken) throws Exception{
		return rrs.getAllResults(sessionToken).getResults();
	}
	
	/**
	 * retireves the specified range of results
	 * 
	 * @param from the starting index
	 * @param to the ending index
	 * @param sessionToken the session token
	 * @return the results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []getResults(int from, int to, String sessionToken) throws Exception{
		GetResultsRequest params=new GetResultsRequest();
		params.setFrom(from);
		params.setTo(to);
		params.setSessionToken(sessionToken);
		String []res=rrs.getResults(params).getResults();
		if(res==null){
			return new String[0];
		}
		return res;
	}
	
	/**
	 * checks if the content part can be attached and retrieve the tranported file name
	 * 
	 * @param sessionToken teh session token
	 * @return the local file name
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String canAttach(String sessionToken) throws Exception{
		log.error("Could not get attached content. Throwing Exception");
		throw new Exception("Could not get attached content");
//		ws core 4.1 specific for attachments
//		if(!rrs.canAttach(sessionToken)){
//			log.error("Could not get attached content. Throwing Exception");
//			throw new Exception("Could not get attached content");
//		}
//		Object [] objArray = ((org.apache.axis.client.Stub)rrs).getAttachments();
//		AttachmentPart part = (AttachmentPart)objArray[0];
//        FileOutputStream out = null;
//        String localName=RSFileHelper.generateName(RSConstants.CONTENT,null);
//        DataHandler dh = part.getDataHandler();
//        out = new FileOutputStream(new File(localName));
//        dh.writeTo(out);
//        return localName;
	}
	
	/**
	 * Splits the current payload part to smaller parts as defined by the {@link org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants#partSize}
	 * property. The split parts are encoded using Base64 encoding.
	 * 
	 * @param sessionToken the session token
	 * @return An array with the produced parts
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []splitEncoded(String sessionToken) throws Exception{
		return rrs.splitEncoded(this.locator.getSessionToken()).getPages();
	}
	
	/**
	 * Splits the current payload part to smaller parts as defined by the {@link org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants#partSize}
	 * property
	 * 
	 * @param sessionToken the session token
	 * @return An array with the produced parts
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []splitClear(String sessionToken) throws Exception{
		return rrs.splitClear(sessionToken).getPages();
	}

	/**
	 * Executes the query on the current document
	 * 
	 * @param xPath the xpath
	 * @param sessionToken the session token
	 * @return the result
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String executeQueryOnDocument(String xPath,String sessionToken) throws Exception{
		ExecuteQueryOnDocumentRequest req=new ExecuteQueryOnDocumentRequest();
		req.setSessionToken(sessionToken);
		req.setXPath(xPath);
		return rrs.executeQueryOnDocument(req);
	}

	/**
	 * executes the xpath oin the xead document
	 * 
	 * @param xPath the xpath
	 * @param sessionToken the session token
	 * @return the result
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String executeQueryOnHead(String xPath,String sessionToken) throws Exception{
		ExecuteQueryOnHeadRequest req=new ExecuteQueryOnHeadRequest();
		req.setSessionToken(sessionToken);
		req.setXPath(xPath);
		return rrs.executeQueryOnHead(req);
	}
	
	/**
	 * executes the xpath on the results and returns the results
	 * 
	 * @param xPath the xpath
	 * @param sessionToken the session token
	 * @return the results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []executeQueryOnResults(String xPath,String sessionToken) throws Exception{
		ExecuteQueryOnResultsRequest req=new ExecuteQueryOnResultsRequest();
		req.setSessionToken(sessionToken);
		req.setXPath(xPath);
		String []ret=rrs.executeQueryOnResults(req).getResults();
		if(ret==null){
			return new String[0];
		}
		return ret;
	}
	
	/**
	 * retrieve the remote IP
	 * 
	 * @param sessionToken the session token
	 * @return the ip
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getHostIP(String sessionToken) throws Exception{
		return rrs.getHostIP(sessionToken);
	}
	
	/**
	 * retrieve the remote host name
	 * 
	 * @param sessionToken the session token
	 * @return the host name
	 * @throws Exception An unrecoverable for the operation error occured
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getHostName(String sessionToken) throws Exception{
		return rrs.getHostName(sessionToken);
	}
	
	/**
	 * retrieves the content of the file
	 * 
	 * @param filename the file
	 * @param sessionToken the session token
	 * @return the content
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getFileContent(String filename,String sessionToken) throws Exception{
		GetFileContentRequest req=new GetFileContentRequest();
		req.setFilename(filename);
		req.setSessionToken(sessionToken);
		return rrs.getFileContent(req);
	}
	
	/**
	 * retrieves teh current payload part content
	 * 
	 * @param sessionToken the session token
	 * @return teh payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getCurrentContentPartPayload(String sessionToken) throws Exception{
		return rrs.getCurrentContentPartPayload(sessionToken);
	}
	
	/**
	 * performs the provided xslt transdfgormration on the current payload part and returns the results
	 * 
	 * @param transformation the transfomration
	 * @param sessionToken the session token
	 * @return the reuslts
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformByXSLT(String transformation, String sessionToken) throws Exception{
		TransformByXSLTRequest req=new TransformByXSLTRequest();
		req.setSessionToken(sessionToken);
		req.setTransformation(transformation);
		return rrs.transformByXSLT(req);
	}
	
	/**
	 * Forwards a filter operation in the remote {@link ResultSet}
	 * 
	 * @param xPath the xpath
	 * @param properties the proerpties to set
	 * @param sessionToken the session token
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String filterRSProp(String xPath,String []properties, String sessionToken) throws Exception{
		FilterRSPropRequest params=new FilterRSPropRequest();
		params.setProperties(properties);
		params.setXpath(xPath);
		params.setSessionToken(this.locator.getSessionToken());
		return rrs.filterRSProp(params);
	}
	
	/**
	 * Forwards a filter operation in the remote {@link ResultSet}
	 * 
	 * @param xPath the xpath
	 * @param sessionToken the session token
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String filterRS(String xPath, String sessionToken) throws Exception{
		FilterRSRequest req=new FilterRSRequest();
		req.setXpath(xPath);
		req.setSessionToken(sessionToken);
		return rrs.filterRS(req);
	}
	
	/**
	 * Forwards a transform operation in the underlying {@link ResultSet}
	 * 
	 * @param xslt The xslt to be applied
	 * @param properties The properties the new {@link ResultSet} should have
	 * @param sessionToken the session token
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformRSProp(String xslt,String []properties, String sessionToken) throws Exception{
		TransformRSPropRequest params=new TransformRSPropRequest();
		params.setProperties(properties);
		params.setXslt(xslt);
		params.setSessionToken(sessionToken);
		return rrs.transformRSProp(params);
	}
	
	/**
	 * Forwards a transform operation in the underlying {@link ResultSet}
	 * 
	 * @param xslt the xslt
	 * @param sessionToken tjhe session token
	 * @return the name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformRS(String xslt,String sessionToken) throws Exception{
		TransformRSRequest req=new TransformRSRequest();
		req.setTransformation(xslt);
		req.setSessionToken(sessionToken);
		return rrs.transformRS(req);
	}
	
	/**
	 * Forwards a keep top operation in the underlying {@link ResultSet}
	 * 
	 * @param count The number of top results to keep
	 * @param properties The properties the new {@link ResultSet} should have
	 * @param type The type of keep top operation to perform. This can be one of {@link org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric#PERPART}
	 * and {@link org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric#PERRECORD}
	 * @param sessionToken the session token
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String keepTopProp(int count,String []properties,short type,String sessionToken) throws Exception{
		KeepTopPropRequest params=new KeepTopPropRequest();
		params.setProperties(properties);
		params.setCount(count);
		params.setType(type);
		params.setSessionToken(this.locator.getSessionToken());
		return rrs.keepTopProp(params);
	}
	
	/**
	 * Forwards a keep top operation in the underlying {@link ResultSet}
	 * 
	 * @param count The number of top results to keep
	 * @param type The type of keep top operation to perform. This can be one of {@link org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric#PERPART}
	 * and {@link org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric#PERRECORD}
	 * @param sessionToken tjhe session token
	 * @return The name of the file that stores the head part of the new {@link ResultSet} in the underlyin {@link ResultSet} local machine
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String keepTop(int count,short type,String sessionToken) throws Exception{
		KeepTopRequest params=new KeepTopRequest();
		params.setCount(count);
		params.setType(type);
		params.setSessionToken(sessionToken);
		return rrs.keepTop(params);
	}
	
	/**
	 * Forwards a cloning operation to the underlying {@link ResultSet}
	 * 
	 * @param sessionToken tjhe session token
	 * @return The head part name of the new {@link ResultSet} in the machine that holds the current {@link ResultSet} 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String cloneRS(String sessionToken) throws Exception{
		return rrs.cloneRS(sessionToken);
	}
	
	/**
	 * Retrieves the name of the currnt content part holding file
	 * 
	 * @return The name of the file holding the current content part
	 * @param sessionToken tjhe session token
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getCurrentContentPartName(String sessionToken) throws Exception{
		return rrs.getCurrentContentPartName(sessionToken);
	}

	/**
	 * Is the RS only forward reading?
	 * @param sessionToken the session token
	 * @return true if the RS is forward only
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isForward(String sessionToken) throws Exception{
		return rrs.isForward(sessionToken);
	}

	/**
	 * Get access leasing property
	 * @param sessionToken the session tocken
	 * @return the access leasing
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public int getAccessLeasing(String sessionToken) throws Exception{
		return rrs.getAccessLeasing(sessionToken);
	}

	/**
	 * Get time leasing property
	 * @param sessionToken the session tocken
	 * @return the time leasing
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public Date getTimeLeasing(String sessionToken) throws Exception{
		return new Date(rrs.getTimeLeasing(sessionToken));
	}
}
