package org.gcube.data.tm.context;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.activationrecord.ActivationRecord;
import org.gcube.data.tm.activationrecord.ActivationRecordBody;
import org.gcube.data.tm.consumers.ActivationRecordConsumer;
import org.gcube.data.tm.consumers.SourceRemovalConsumer;
import org.gcube.data.tm.plugin.PluginManager;
import org.gcube.data.tm.utils.BindParametersWrapper;

/**
 * The context of the service.
 * 
 * @author Fabio Simeoni
 *
 */
public class ServiceContext extends GCUBEServiceContext {

	/** Single context instance, created eagerly. */
	private static ServiceContext cache = new ServiceContext();
	
	/** Returns cached instance */
	public static ServiceContext getContext() {
		return cache;
	}
	
	/** Prevents accidental creation of more instances */
	private ServiceContext(){};
	
	private List<GCUBENotificationTopic> registeredNotficationTopic= new ArrayList<ISNotifier.GCUBENotificationTopic>();
	
	/**"{@inheritDoc}*/
	@Override protected String getJNDIName() {
		return Constants.JNDI_NAME;
	}
	
	private JAXBContext binder;
	
	/**
	 * Returns the data binder.
	 * @return the binder
	 * @throws Exception if the binder could not be returned
	 */
	public synchronized JAXBContext getDataBinder() throws Exception {
		if (binder==null) {
			binder = JAXBContext.newInstance(ActivationRecordBody.class,BindParametersWrapper.class);
		}
		return binder;
	}
	
	/** {@inheritDoc} */
	protected void onReady() throws Exception {
		
		super.onReady();
		
		this.initialisegRS2();
		
		logger.info("creating T-Binder resources in all RI scopes");
		
		TBinderContext binderContext = TBinderContext.getContext();
		for (GCUBEScope scope : this.getInstance().getScopes().values()) {
			this.setScope(scope);
			binderContext.createBinder();
			
		}
		
		if (GHNContext.getContext().getMode()==GHNContext.Mode.CONNECTED)
			selfStage();
	}
	
	protected void onShutdown() throws Exception {
		
		if (GHNContext.getContext().getMode()==GHNContext.Mode.CONNECTED) {
			ISNotifier notifier = GHNContext.getImplementation(ISNotifier.class);
			for (GCUBEScope scope : this.getInstance().getScopes().values())
				notifier.unregisterFromISNotification(this,this.registeredNotficationTopic,scope);
		}
		
		new PluginManager().stop();
		
	}
	
	@Override
	protected void onFailure() throws Exception {
		
		new PluginManager().stop();
	}
	
	
	private void selfStage() throws Exception {
		
		stageForActivationRecords();
		stageForSourceProfiles();
	}
	
	//subscribe for activation record creation and first polling
	private void stageForActivationRecords() throws Exception {
		
		ISNotifier notifier = GHNContext.getImplementation(ISNotifier.class);
		ISClient client = GHNContext.getImplementation(ISClient.class);
		
		GCUBENotificationTopic notificationTopic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry",GCUBEGenericResource.TYPE));
		notificationTopic.setPrecondition("//profile[contains(.,'<SecondaryType>"+Constants.ACTIVATIONRECORD_TYPE+"</SecondaryType>') and contains(.,'<Name>"+Constants.ACTIVATIONRECORD_NAME+"</Name>')] and //operationType[text()='create']");		
		this.registeredNotficationTopic.add(notificationTopic);
		
		List<GCUBENotificationTopic> topic = Collections.singletonList(notificationTopic);
		for (GCUBEScope scope : this.getInstance().getScopes().values()) {
			
			//query for activation records
			logger.trace("looking for activation records in "+scope);
			GCUBEGenericResourceQuery query = client.getQuery(GCUBEGenericResourceQuery.class);
			String thisServiceCondition = String.format("$result/Profile/Name eq '%1$s'",Constants.ACTIVATIONRECORD_NAME); 
			query.addGenericCondition(thisServiceCondition);
			String notSameRICondition = String.format("$result/descendant::*[local-name()='createdBy'] ne '%1$s'",getInstance().getID());
			query.addGenericCondition(notSameRICondition);
			final List<GCUBEGenericResource> resources = client.execute(query, scope);	
			final ActivationRecordConsumer consumer = new ActivationRecordConsumer(scope);
			
			//safe not to create resources until service is READY, to avoid deadlock with other
			// state management processes.
			// here we simulate notifications by calling the consumer from another thread
			final CountDownLatch latch = new CountDownLatch(1);
			if (resources.size()>0) {//let service become ready
				new Thread() {
					public void run() {
						try {latch.await();} catch (InterruptedException e) {}
						for (GCUBEGenericResource resource : resources) 
							consumer.onNewActivationRecord(ActivationRecord.newInstance(resource));
						
					};
				}.start();
			}
			
			logger.trace("subscribing for activation records in "+scope);
			
			notifier.registerToISNotification(consumer, topic, this, scope);	
			latch.countDown(); //let fake notification thread proceed
		}
	}
	
	//subscribe for collection profile removal
	private void stageForSourceProfiles() throws Exception {
		
		ISNotifier notifier = GHNContext.getImplementation(ISNotifier.class);
		
		GCUBENotificationTopic notificationTopic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry",GCUBEGenericResource.TYPE));
		notificationTopic.setPrecondition("//profile[contains(.,'<SecondaryType>GCUBECollection</SecondaryType>')] and //operationType[text()='destroy']");
		
		this.registeredNotficationTopic.add(notificationTopic);
		
		List<GCUBENotificationTopic> topic = Collections.singletonList(notificationTopic);
		for (GCUBEScope scope : this.getInstance().getScopes().values()) {
			
			logger.trace("subscribing for collection profile removals in "+scope);
			notifier.registerToISNotification(new SourceRemovalConsumer(scope), topic, this, scope);	
		}
	}

	//helper
	private void initialisegRS2() throws UnknownHostException{
		List<PortRange> ports=new ArrayList<PortRange>(); //The ports that the TCPConnection manager should use
		ports.add(new PortRange(3050, 3100));   
		String host = null;
		try {
			host = InetAddress.getLocalHost().getHostName();
		}
		catch(Exception e) {
			host = "localhost";
		}
		TCPConnectionManager.Init(
		  new TCPConnectionManagerConfig(host, //The hostname by which the machine is reachable 
		    ports,                                    //The ports that can be used by the connection manager
		    true                                      //If no port ranges were provided, or none of them could be used, use a random available port
		));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());      //Register the handler for the gRS2 incoming requests
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler()); //Register the handler for the gRS2 store incoming requests
	}
}
