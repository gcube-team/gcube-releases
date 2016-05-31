package org.gcube.common.vremanagement.ghnmanager.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.List;

import org.gcube.common.vremanagement.ghnmanager.impl.contexts.ServiceContext;
import org.gcube.common.vremanagement.ghnmanager.impl.platforms.Finder;
import org.gcube.common.vremanagement.ghnmanager.impl.platforms.GHNPlatforms;
import org.gcube.common.vremanagement.ghnmanager.impl.platforms.PlatformApplication;
import org.gcube.common.vremanagement.ghnmanager.impl.platforms.PlatformCall;
import org.gcube.common.vremanagement.ghnmanager.stubs.AddScopeInputParams;
import org.gcube.common.vremanagement.ghnmanager.stubs.RIData;
import org.gcube.common.vremanagement.ghnmanager.stubs.ShutdownOptions;
import org.gcube.common.vremanagement.ghnmanager.stubs.ScopeRIParams;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBEUnrecoverableFault;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.virtualplatform.image.PlatformConfiguration;
import org.gcube.vremanagement.virtualplatform.image.Platforms;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;

/**
 * <em>GHNManager</em> port-type service implementation
 * 
 * @author Manuele Simi (CNR-ISTI)
 * 
 */
public class GHNManager extends GCUBEPortType {

	/**
	 * Object logger.
	 */
	protected final GCUBELog logger = new GCUBELog(this, ServiceContext.getContext());
	
	
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
	
	/**
	 * Joins the GHN to a new {@link GCUBEScope} 
	 * 
	 * @param params WSDL defined parameters
	 * @throws GCUBEUnrecoverableFault if it cannot add the scope
	 */
	public boolean addScope(AddScopeInputParams params) throws GCUBEUnrecoverableFault {

		logger.info("Adding scope " + params.getScope() + " to the GHN");		
		try {
			GCUBEScope scope = GCUBEScope.getScope(params.getScope());
			//load the service map
			ServiceMap map = new ServiceMap();		
			if (params.getMap() == null || params.getMap().compareToIgnoreCase("") == 0) { 
				//look for a local available map for the given scope
				if (scope.getType() == Type.VO)
					map.load(new FileReader(GHNContext.getContext().getGHN().getMapFile(scope)));
				else 
					map.load(new FileReader(GHNContext.getContext().getGHN().getMapFile(scope.getEnclosingScope())));
					
			} else { //try to load the one passed as parameter				
			    map.load(new StringReader(params.getMap()));	
				new FileWriter(GHNContext.getContext().getGHN().getMapFile(scope)).close();
			}			
			GHNContext.getContext().addScope(scope);			
		} catch (Exception e) {
			logger.error("Unable to assign the scope " + params.getScope()
					+ " to this GHN", e);
			throw new GCUBEUnrecoverableFault("Unable to assign the scope "	+ params.getScope() + " to this GHN: " + e.getMessage());
		}		
		return true;
	}

	/**
	 * Unbinds the gHN from a {@link GCUBEScope}
	 * 
	 * @param params WSDL defined parameters
	 * @throws GCUBEUnrecoverableFault
	 */
	public boolean removeScope(String scope) throws GCUBEUnrecoverableFault {		
		logger.info("Removing scope " + scope + " from GHN");
						
		try {
			GCUBEScope targetScope = GCUBEScope.getScope(scope);
			//Deleting the GHN from the IS
			GHNContext.getImplementation(ISPublisher.class).removeGCUBEResource(GHNContext.getContext().getGHN().getID(), 
					GCUBEHostingNode.TYPE, targetScope, ServiceContext.getContext());
			//unregistering from the context
			GHNContext.getContext().removeScope(targetScope);			
			
		} catch (Exception e) {
			logger.error("Unable to remove the scope " + scope + " from this GHN", e);
			throw new GCUBEUnrecoverableFault("Unable to remove the scope " + scope + " from this GHN: " + e.getMessage());
		}
		
		return true;
	}
	/**
	 * Adds a new scope to a local RI
	 * 
	 * @param params WSDL defined parameters
	 * @throws GCUBEUnrecoverableFault, GCUBEFault
	 */
	public boolean addRIToScope(ScopeRIParams params)	throws GCUBEUnrecoverableFault, GCUBEFault {
		logger.info("Adding scope "	+ params.getScope() + " to RI <" + params.getClazz() +","+ params.getName() +">");
		try {
			// if the new scope is not included in the GHN scopes, the GCUBEScope.getScope() fails
			GHNContext.getContext().getServiceContext(params.getClazz(), params.getName()).addScope(GCUBEScope.getScope(params.getScope()));			
		} catch (Exception e) {
			logger.debug("Checking the RI in the local virtual platforms");
			//check in the platforms
			try {
				GCUBERunningInstance instance = GHNContext.getContext().getLocalInstanceContext().getInstance(params.getClazz(), params.getName());
				VirtualPlatform platform = Finder.find(instance.getPlatform());
				if (platform.isAvailable()) {
					PlatformApplication app = new PlatformApplication(instance);
					instance.addScope(GCUBEScope.getScope(params.getScope()));
					app.publish(instance.getScopes().values(), ServiceContext.getContext(), Status.READIED);
					GHNContext.getContext().getLocalInstanceContext().registerInstance(instance);
				}
			} catch (Exception e1) {
				logger.warn("unable to find an instance of service "
						+ params.getClazz() + ", " + params.getName(),e1);

				throw new GCUBEUnrecoverableFault("unable to find an instance of service "
						+ params.getClazz() + ", " + params.getName());
			}
				
			
		} 
		return true;
	}
	
	/**
	 * Activates an instance hosted on the local node
	 * @param ri name and class of the instance
	 * @return <tt>true</tt> if the instance was successfully activated (of if it was already active), false otherwise
	 */
	public boolean activateRI(RIData ri) throws GCUBEUnrecoverableFault, GCUBEFault {
		
		logger.info("Activating RI <" + ri.getClazz() +","+ ri.getName() +">");
		try {
			// if the new scope is not included in the GHN scopes, the GCUBEScope.getScope() fails
			GHNContext.getContext().getServiceContext(ri.getClazz(), ri.getName()).setStatus(Status.READIED);
		} catch (Exception e) {
			logger.debug("Checking the RI in the local virtual platforms");
			//check in the platforms
			try {
				GCUBERunningInstance instance = GHNContext.getContext().getLocalInstanceContext().getInstance(ri.getClazz(), ri.getName());
				VirtualPlatform platform = Finder.find(instance.getPlatform());
				if (platform.isAvailable()) {
					PlatformCall call = new PlatformCall(platform);
					PlatformApplication app = call.activate(instance);
					app.publish(instance.getScopes().values(), ServiceContext.getContext(), Status.READIED);
					GHNContext.getContext().getLocalInstanceContext().registerInstance(instance);
				}
			} catch (Exception e1) {
				logger.warn("unable to find an instance of service "
						+ ri.getClazz() + ", " + ri.getName(),e1);

				throw new GCUBEUnrecoverableFault("unable to find an instance of service "
						+ ri.getClazz() + ", " + ri.getName());
			}
				
		} 

		return true;
		
	}
	/**
	 * Deactivates an instance hosted on the local node
	 * @param ri name and class of the instance
	 * @return <tt>true</tt> if the instance was successfully deactivated (of if it was already deactivate), false otherwise
	 */
	
	public boolean deactivateRI(RIData ri) throws GCUBEUnrecoverableFault, GCUBEFault {
		logger.info("Deactivating RI <" + ri.getClazz() +","+ ri.getName() +">");
		try {
			// if the new scope is not included in the GHN scopes, the GCUBEScope.getScope() fails
			GHNContext.getContext().getServiceContext(ri.getClazz(), ri.getName()).setStatus(Status.DOWN);
		} catch (Exception e) {
			logger.debug("Checking the RI in the local virtual platforms");
			//check in the platforms
			try {
				GCUBERunningInstance instance = GHNContext.getContext().getLocalInstanceContext().getInstance(ri.getClazz(), ri.getName());
				logger.debug("RI found in platform  " + instance.getPlatform().getName() );
				VirtualPlatform platform = Finder.find(instance.getPlatform());
				if (platform.isAvailable()) {
					logger.debug("Platform " + platform.getName() + " is available");
					PlatformCall call = new PlatformCall(platform);
					PlatformApplication app = call.deactivate(instance);
					app.publish(instance.getScopes().values(), ServiceContext.getContext(), Status.DOWN);
					GHNContext.getContext().getLocalInstanceContext().registerInstance(instance);
				}
			} catch (Exception e1) {
				logger.warn("unable to find an instance of service "
						+ ri.getClazz() + ", " + ri.getName(),e1);
				throw new GCUBEUnrecoverableFault("unable to find an instance of service "
						+ ri.getClazz() + ", " + ri.getName());
			}
		}

		return true;		
	}
	
	/**
	 * Adds a new scope to a local RI
	 * 
	 * @param params WSDL defined parameters
	 * @throws GCUBEUnrecoverableFault, GCUBEFault
	 */
	public boolean removeRIFromScope(ScopeRIParams params) throws GCUBEUnrecoverableFault, GCUBEFault {
		logger.info("Removing scope "	+ params.getScope() + " to RI <" + params.getClazz() +","+ params.getName() +">");
		try {			
			GHNContext.getContext().getServiceContext(params.getClazz(), params.getName()).removeScope(GCUBEScope.getScope(params.getScope()));			
		} catch (GCUBEException e) {
			logger.debug("Checking the RI in the local virtual platforms");
			//check in the platforms
			try {
				GCUBERunningInstance instance = GHNContext.getContext().getLocalInstanceContext().getInstance(params.getClazz(), params.getName());
				VirtualPlatform platform = Finder.find(instance.getPlatform());
				if (platform.isAvailable()) {
					PlatformApplication app = new PlatformApplication(instance);
					instance.removeScope(GCUBEScope.getScope(params.getScope()));
					app.publish(instance.getScopes().values(), ServiceContext.getContext(), Status.READIED);
					GHNContext.getContext().getLocalInstanceContext().registerInstance(instance);
				}
			} catch (Exception e1) {
				logger.warn("unable to find an instance of service "
						+ params.getClazz() + ", " + params.getName(),e1);

				throw new GCUBEUnrecoverableFault("unable to find an instance of service "
						+ params.getClazz() + ", " + params.getName());
			}
				
		} catch (Exception e) {
			throw ServiceContext.getContext().getDefaultException(	e.getMessage(), e).toFault();
		}

		return true;
	}
	
	/**
	 * Shuts down the local GHN
	 * 
	 * @param options 
	 * 	<ul>
	 * 		<li>restart <code>true</code> if the container must be restarted after the shutdown
	 *  </ul> 
	 * @throws GCUBEUnrecoverableFault if the operation fails
	 */
	public void shutdown(ShutdownOptions options) throws GCUBEUnrecoverableFault {
		logger.info("Remote request for shutting down the gHN received");
		logger.trace("Shutting down from " + GHNContext.getContext().getStatus().name());
		List<PlatformConfiguration> configurations = Platforms.listAvailablePlatforms(new File(GHNContext.getContext().getVirtualPlatformsLocation()));
		for (PlatformConfiguration config : configurations){
			try {
				new PlatformCall(GHNPlatforms.get(config)).shutdown();
			} catch (Exception e) {
				logger.warn("Unable to shutdown platform " +config.getName(),e);
			}
		}
		try {
			if (options.isRestart()) {
				if (options.getClean()) 
					new Thread() {public void run() {GHNContext.getContext().restartAndClean();}}.start();
				else					
					new Thread() {public void run() {GHNContext.getContext().restart();}}.start();
			}
			else
				new Thread() {public void run() {GHNContext.getContext().shutdown();}}.start();
		} catch (Exception e) {
			throw new GCUBEUnrecoverableFault("unable to shutdown the container");
		}
		
	}
}
