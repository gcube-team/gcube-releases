package org.gcube.common.vremanagement.ghnmanager.impl.contexts;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.ghn.GHNConsumer;
import org.gcube.common.core.contexts.ghn.Events.GHNLifeTimeEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNTopic;
import org.gcube.common.core.contexts.GHNContext.Mode;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.vremanagement.ghnmanager.impl.platforms.PlatformMonitor;

/**
 * Service context implementation for GHNManager
 * 
 * 
 * @author Manuele Simi (CNR-ISTI)
 * 
 */
public class ServiceContext extends GCUBEServiceContext {

	/**
	 * JNDI resource
	 */
	public static final String JNDI_NAME = "gcube/common/vremanagement/ghnmanager";

	protected static ServiceContext context = new ServiceContext();

	private boolean starting = true;
		
	private ISClient client;
	
	private ISPublisher publisher;
	
	/*protected volatile ISClient client = null;
	
	protected volatile ISPublisher publisher = null;*/
	/**
	 * 
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	protected class HCleanup extends GCUBEHandler<ServiceContext> {

		@Override
		public void run() throws Exception {
			//cannot delete if in a STANDALONE mode
			if ( GHNContext.getContext().getMode() == Mode.STANDALONE)
				return;			
			
			if (! GHNContext.getContext().isGHNReady())
				throw new Exception();
			//query the IS and cleanup the old GHN profiles for each known scope
			this.getHandled().logger.debug("Checking the GHN profiles on the IS");
			GCUBEHostingNode ghnProfile = GHNContext.getContext().getGHN();
			GCUBEGHNQuery node = client.getQuery(GCUBEGHNQuery.class);
			node.addAtomicConditions(new AtomicCondition("/Profile/GHNDescription/Name", ghnProfile.getNodeDescription().getName()));			
			for (GCUBEScope scope : this.getHandled().getInstance().getScopes().values()) {												
				List<GCUBEHostingNode> ghnList = client.execute(node, scope);				
				if ((ghnList == null) || (ghnList.size() == 0)) 
					throw new Exception("no GHN registered on the IS yet");//maybe the current one is not yet registered				
				if ((ghnList.size() == 1) && (ghnList.get(0).getID().compareToIgnoreCase(ghnProfile.getID())== 0)) 					
					continue; //it's the current one								
				for (GCUBEHostingNode otherNode : ghnList) {					
					if (otherNode.getID().compareToIgnoreCase(ghnProfile.getID())== 0)
						continue;
					this.getHandled().logger.info("Deleting old GHN " + otherNode.getID() + " from scope " + scope);
					publisher.removeGCUBEResource(otherNode.getID(), GCUBEHostingNode.TYPE, scope, ServiceContext.this); 
				}					
				
			}			
			//query the IS and cleanup the no longer available RIs
			this.getHandled().logger.debug("Checking the running instance profiles on the IS");
			Set<GCUBEServiceContext> gCubeRIs = GHNContext.getContext().getServiceContexts();
			Collection<GCUBERunningInstance> vpRIs = GHNContext.getContext().getLocalInstanceContext().getAllInstances();
			if (gCubeRIs.size() == 0)
				throw new Exception("no Service registered on the GHN yet");
			//look up on all GHN scopes to find RIs
			GCUBEGenericQuery query =  client.getQuery("RIOnGHN");			
			query.addParameters(new QueryParameter("ID", ghnProfile.getID()));		
			for (GCUBEScope scope : this.getHandled().getInstance().getScopes().values()) {
				List<XMLResult> results = client.execute(query, scope);
				if (results == null) 
					throw new Exception("no RI registered on the IS yet");
				outer: for (XMLResult xml : results) {					
					String riid = xml.evaluate("/Resource/ID/text()").get(0);
					//looks in the gCube instances
					for (GCUBEServiceContext service : gCubeRIs) {
						if (service.getInstance().getID().compareToIgnoreCase(riid) == 0) 
							continue outer;	
					}
					//looks in the VP instances
					for (GCUBERunningInstance ri : vpRIs) {
						if (ri.getID().compareToIgnoreCase(riid) == 0) 
							continue outer;	
					}
					//if we are here, there is no instance with this ID on the GHN
					ServiceContext.this.setScope(scope);
					ServiceContext.this.logger.info("Deleting old RI " + riid + " from scope " + scope);
					publisher.removeGCUBEResource(riid, GCUBERunningInstance.TYPE, scope, ServiceContext.this);
				}
			}
		}		
	}

	/**
	 * Cleans up the GHN and the no longer available RIs on the IS at GHN startup 
	 * 
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	@SuppressWarnings("unchecked")
	protected class HCleanupScheduler extends GCUBEScheduledHandler {
		/**
		 * {@inheritDoc}
		 */
		public HCleanupScheduler(long interval, Mode mode) {
			super(interval, mode);
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean repeat(Exception exception, int exceptionCount)  {
			if (exception!=null) {
				//logger.warn("Failed to cleanup the GHN (attempt "+exceptionCount+" out of 20)",exception);
				if (exceptionCount >= 20) {
					logger.warn("Max attempts reached, no more chance to cleanup the GHN");
					//ServiceContext.getContext().setStatus(GCUBEServiceContext.Status.FAILED, true);
					return false;
				} else 
					return true;
			} else {
				return false;
			}
		}
		
	}
	
	private ServiceContext() {}

	/**
	 * {@inheritDoc}
	 */
	public static ServiceContext getContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onInitialisation() throws Exception {

		try {
			this.client =  GHNContext.getImplementation(ISClient.class);			
			this.publisher =  GHNContext.getImplementation(ISPublisher.class);
			this.completeGHNProfile();
		} catch (Exception e) {
			throw new RuntimeException("unable to find the IS*.class implementation");
		}
		
		GHNConsumer sManager = new GHNConsumer() {
			
			/** {@inheritDoc} */			
			protected void onGHNUpdated(GHNLifeTimeEvent  event) {
				GHNContext context = GHNContext.getContext();
				logger.debug(event.getTopic().name()+ " event received");
				
				ISPublisher publisher = null;
				try {
					publisher = GHNContext.getImplementation(ISPublisher.class);
				} catch (Exception e1) {
					logger.fatal("Unable to find a valid ISPublisher implementation");
					throw new RuntimeException();
				}
				
				// register/update the GHN in the IS
				try {
					if (context.getGHN().getScopes().values().size() == 0)
						logger.fatal("No scope found in the GHN");
					for (GCUBEScope scope : context.getGHN().getScopes().values()) {
						if ((scope == null) || (scope.toString().compareToIgnoreCase("null") == 0) || (scope.getType() == Type.VRE))
							continue;
						logger.debug("Publishing GHN profile in scope "	+ scope);						
						try {							
							
							if (context.getMode() != Mode.STANDALONE) {																
								if (ServiceContext.this.isStarting()) 
									publisher.registerGCUBEResource(context.getGHN(), scope, ServiceContext.this);								
								else 
									publisher.updateGCUBEResource(context.getGHN(), scope, ServiceContext.this);
							} //else, nothing to do, the profile is not published
						} catch (Exception e) {
							logger.error("Unable to publish GHN profile in " + scope.getName(), e);
							StringWriter writer = new StringWriter();
							context.getGHN().store(writer);
							logger.debug(writer);					
							//break;
						}
					}
										
				} catch (Exception e) {
					logger.error("Unable to publish GHN profile", e);
				}

			}

		};
		GHNContext.getContext().subscribeGHNEvents(sManager, GHNTopic.UPDATE);
					
		this.setStarted();
	}

	private void completeGHNProfile() {
		//compile the list of virtual platforms on the node profile
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new PlatformMonitor(), 0, 120, TimeUnit.SECONDS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getJNDIName() {
		return JNDI_NAME;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	protected void onReady() throws Exception {	
		super.onReady();
		//start the cleanup Handler
		HCleanupScheduler cleanupScheduler = new HCleanupScheduler(10, GCUBEScheduledHandler.Mode.LAZY);
		HCleanup c = new HCleanup();
		c.setHandled(ServiceContext.this);
		cleanupScheduler.setScheduled(c);
		cleanupScheduler.run();
		
	} 
	
	/**
	 * Checks if the service is in its starting phase
	 * @return
	 */
	protected boolean isStarting() {
		return this.starting;
	}
	
	private void setStarted() {
		this.starting = false;
	}

	/**
	 * @return the platforms configured on this ghn
	 */
	public List<PlatformDescription> getConfiguredPlatforms() {
		return Collections.unmodifiableList(GHNContext.getContext().getGHN().getNodeDescription().getAvailablePlatforms());
	}
}
