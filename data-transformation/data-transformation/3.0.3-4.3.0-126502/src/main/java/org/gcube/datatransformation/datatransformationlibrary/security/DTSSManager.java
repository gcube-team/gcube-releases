package org.gcube.datatransformation.datatransformationlibrary.security;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.ISManager;
import org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraphImpl;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Helper class which maintains <tt>scope</tt> and <tt>credentials</tt> in the context of the transformationUnit.
 */
public class DTSSManager {
//	private static InheritableThreadLocal<SecurityCredentials> transactionCredentials = new InheritableThreadLocal<SecurityCredentials>();
//	
//	/** Embedded security manager. */ 
//	private static volatile GCUBESecurityManager securityManager = null;
//		
//	/**
//	 * Sets the security manager which will be used by the service.
//	 * 
//	 * @param manager The security manager.
//	 */
//	public static void setSecurityManager(GCUBESecurityManager manager) {securityManager=manager;}
//	
//	/**
//	 * Returns the security manager after setting to it the thread local credentials.
//	 * 
//	 * @return The security manager.
//	 */
//	public static GCUBESecurityManager getSecurityManager() {
//		try {
//			securityManager.useCredentials(transactionCredentials.get());
//		} catch (Exception e) {
//			log.error("Cannot use credentials in the current thread",e);
//		}
//		return securityManager;
//	}
//	/**
//	 * Uses the credentials in the current thread.
//	 * 
//	 * @param credentials The credentials which will be used.
//	 */
//	public static void useCredentials(SecurityCredentials credentials) {transactionCredentials.set(credentials);}
	
	private static Logger log = LoggerFactory.getLogger(DTSSManager.class);
	
	protected static volatile HashMap<String, DTSCore> dCoreMap = new HashMap<String, DTSCore>();
	
	/**
	 * Returns the {@link DTSCore} instance which is used in this <tt>scope</tt>.
	 * 
	 * @return The {@link DTSCore} instance which is used in this <tt>scope</tt>.
	 * @throws GCUBEFault If the service is not deployed in the scope in which the request is maid.
	 */
	public static DTSCore getDTSCore(String scope) throws Exception {
		if(scope == null){
			log.error("Scope not specified");
			throw new Exception("Scope not specified");
		}
		DTSCore dCore = dCoreMap.get(scope);
		if(dCore==null){
			log.error("DTS is not deployed in scope "+scope);
			throw new Exception("DTS is not deployed in scope "+scope);
		}
		return dCore;
	}
	
	/**
	 * Initializes the <tt>DTSSManager</tt>.
	 * 
	 * @param serviceContext The gCube context of the service.
	 * @throws Exception If the <tt>DTSSManager</tt> could not be initialized.
	 */
	public static void init(String addedScopes[]) throws Exception {
		log.debug("DTSSManager is initialized...");
		
		log.debug("Scopes were added...");
		if(addedScopes!=null && addedScopes.length>0){
			for(String scope: addedScopes){
				if(DTSSManager.dCoreMap.get(scope)!=null){
					log.warn("Instance for scope "+scope+" already exist");
				}else{
					try {
						log.debug("Going to create dCore instance for scope "+scope);
						ISManager imanager = new ISManager();
						imanager.setScope(scope.toString());
						TransformationsGraphImpl graph = new TransformationsGraphImpl(imanager);
						DTSCore dcore = new DTSCore(imanager, graph);
						DTSSManager.dCoreMap.put(scope, dcore);
					} catch (Exception e) {
						log.error("Could not create DTSCore Instance for scope "+scope,e);
					}
				}
			}
		}else{
			log.warn("No scope to add");
		}
//		DTSGHNConsumer consumer = new DTSGHNConsumer();
//		SERVICECONTEXT.GETINSTANCE().SUBSCRIBERESOURCEEVENTs(consumer, GCUBEResource.ResourceTopic.ADDSCOPE, GCUBEResource.ResourceTopic.REMOVESCOPE);
//		GHNContext.getContext().getGHN().subscribeResourceEvents(consumer, GCUBEResource.ResourceTopic.ADDSCOPE, GCUBEResource.ResourceTopic.REMOVESCOPE);
	}
}

///**
// * @author Dimitris Katris, NKUA
// * 
// * <p>The <tt>DTSGHNConsumer</tt> receives notifications when a scope is added in the <tt>GHN</tt> in which <tt>DTS</tt> is deployed.</p>
// * <p>If a new <tt>scope</tt> is added then a new {@link DTSCore} instance is created for this <tt>scope</tt>.</p> 
// */
//class DTSGHNConsumer extends GCUBEResource.ResourceConsumer {
//	
//	private static Logger log = LoggerFactory.getLogger(DTSGHNConsumer.class);
//	
//	protected void onAddScope(AddScopeEvent event){
//		log.debug("Scopes were added...");
//		GCUBEScope[] addedScopes = event.getPayload();
//		if(addedScopes!=null && addedScopes.length>0){
//			for(GCUBEScope scope: addedScopes){
//				if(DTSSManager.dCoreMap.get(scope)!=null){
//					log.warn("Instance for scope "+scope+" already exist");
//				}else{
//					try {
//						log.debug("Going to create dCore instance for scope "+scope);
//						ISManager imanager = new ISManager();
//						imanager.setScope(scope.toString());
//						TransformationsGraphImpl graph = new TransformationsGraphImpl(imanager);
//						DTSCore dcore = new DTSCore(imanager, graph);
//						DTSSManager.dCoreMap.put(scope, dcore);
//					} catch (Exception e) {
//						log.error("Could not create DTSCore Instance for scope "+scope,e);
//					}
//				}
//			}
//		}else{
//			log.warn("Add scope event but with empty payload");
//		}
//	}
//	
//	protected void onRemoveScope(RemoveScopeEvent event){
//		log.debug("Scopes were removed...");
//		GCUBEScope[] removedScopes = event.getPayload();
//		if(removedScopes!=null && removedScopes.length>0){
//			for(GCUBEScope scope: removedScopes){
//				if(DTSSManager.dCoreMap.get(scope)==null){
//					log.warn("Instance for scope "+scope+" does not exist");
//				}else{
//					try {
//						log.debug("Going to remove dCore instance for scope "+scope);
//						DTSSManager.dCoreMap.get(scope).destroy();
//						DTSSManager.dCoreMap.remove(scope);
//					} catch (Exception e) {
//						log.error("Could not remove DTSCore Instance for scope "+scope);
//					}
//				}
//			}
//		}else{
//			log.warn("Remove scope event but with empty payload");
//		}
//	}
//}
